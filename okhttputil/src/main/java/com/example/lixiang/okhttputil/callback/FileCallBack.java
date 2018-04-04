package com.example.lixiang.okhttputil.callback;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.example.lixiang.okhttputil.OkHttpUtils;
import com.example.lixiang.okhttputil.utils.L;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

/**
 * Created by zhy on 15/12/15.
 */
public abstract class FileCallBack extends Callback<File>
{
    ;
    /**
     * 目标文件存储的文件夹路径
     */
    private String destFileDir;
    /**
     * 目标文件存储的文件名
     */
    private String destFileName;


    /**        Explain : 断点续传中，读取保存在SharedPreferences文件中当前文件的保存信息
    * @author LiXiang create at 2018/3/30 16:23*/
    private  String addFileName = null;
    private  String addFileTotal = null;
    private  String addFileSum = null;

    public abstract void inProgress(float progress);
    public void inProgress(long total,long sum,float progress){
        inProgress(sum * 1.0f / total);
    };

    public FileCallBack(String destFileDir, String destFileName)
    {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
    }

    public FileCallBack(String destFileDir, String destFileName,String addFileName,String addFileTotal,String addFileSum)
    {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
        this.addFileName = addFileName;
        this.addFileTotal = addFileTotal;
        this.addFileSum = addFileSum;
    }


    @Override
    public File parseNetworkResponse(Response response) throws Exception
    {
        return (addFileName== null && addFileTotal== null&& addFileSum== null) ? saveFile(response):saveAddFile(response) ;
    }


    public File saveFile(Response response) throws IOException
    {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try
        {
            is = response.body().byteStream();
            final long total = response.body().contentLength();
            long sum = 0;

            L.e(total + "");

            File file = createFile();
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1)
            {
                sum += len;
                fos.write(buf, 0, len);
                final long finalSum = sum;
                System.out.println("Progress total:"+total);
                System.out.println("Progress finalSum:"+finalSum);
                System.out.println("Progress nowSum:"+finalSum * 1.0f / total);
                OkHttpUtils.getInstance().getDelivery().post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        inProgress(total,finalSum,finalSum * 1.0f / total);
                    }
                });
            }
            fos.flush();

            return file;

        } finally
        {
            try
            {
                if (is != null) is.close();
            } catch (IOException e)
            {
            }
            try
            {
                if (fos != null) fos.close();
            } catch (IOException e)
            {

            }

        }
    }

    @NonNull
    private File createFile() {
        File dir = new File(destFileDir);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        return new File(dir, destFileName);
    }


    /**
     * 追加文件：使用RandomAccessFile
     *
     * @param response : 服务端返回数据
     */
    public File saveAddFile(Response response) {
        RandomAccessFile randomFile = null;
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        File file = createFile();
        is = response.body().byteStream();
        final long total = response.body().contentLength();
        if (getPreferences(addFileName, addFileSum) == 0) {
        savePreferences(addFileName,addFileTotal,total);
        }
        long sum = 0;
        long tempPreferencesSum  = getPreferences(addFileName, addFileSum);
        try {
            // 打开一个随机访问文件流，按读写方式
            randomFile = new RandomAccessFile(file, "rw");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            // 将写文件指针移到文件尾。
//            randomFile.seek(fileLength);
            System.out.println("downloadImage fileLength:"+fileLength);
            System.out.println("downloadImage getPreferences:"+getPreferences(addFileName,addFileSum));
            randomFile.seek(getPreferences(addFileName,addFileSum));
//            =============测试获取本地保存的角标
            while ((len = is.read(buf)) != -1)
            {
                sum += len;
                randomFile.write(buf, 0, len);
                final long finalSum = sum;
                System.out.println("getPreferences:  "+getPreferences(addFileName,addFileSum));
                System.out.println("Progress total:"+total);
                System.out.println("Progress finalSum:"+finalSum);
                System.out.println("Progress nowSum:"+finalSum * 1.0f / total);
                OkHttpUtils.getInstance().getDelivery().post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        inProgress(total,finalSum,finalSum * 1.0f / total);
                    }
                });
            }
            final long finalTempPreferencesSum = sum+tempPreferencesSum;
            savePreferences(addFileName,addFileSum,finalTempPreferencesSum);
        } catch (IOException e) {
            e.printStackTrace();
            final long finalTempPreferencesSum = sum+tempPreferencesSum;
            savePreferences(addFileName,addFileSum,finalTempPreferencesSum);
        } finally{
            if(randomFile != null){
                try {
                    randomFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try
            {
                if (is != null) is.close();
            } catch (IOException e)
            {
            }

        }
        return file;
    }


    public static void savePreferences(String name,String key,long value) {
        SharedPreferences preferences = OkHttpUtils.getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }


    public static long getPreferences(String name,String key){
        SharedPreferences preferences = OkHttpUtils.getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
        return preferences.getLong(key, 0);
    }



}
