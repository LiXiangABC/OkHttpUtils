package com.example.lixiang.okhttputils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.lixiang.okhttputil.OkHttpUtils;
import com.example.lixiang.okhttputil.callback.FileCallBack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private ImageView simpleImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        simpleImage = (ImageView) findViewById(R.id.simple);

//        OkHttpUtils.setContext(this);
//        OkHttpUtils.getInstance().setTimeout(30000,30000,30000, TimeUnit.MILLISECONDS);
//        downloadImage();
    }

//    private void downloadImage() {
//        long preferences1 = FileCallBack.getPreferences("student","sum");
//        System.out.println("preferences1 : "+preferences1);
//
//        OkHttpUtils//
//                .get()//
//                .tag(this)
//                .addHeader("RANGE", "bytes=" + preferences1 + "-")//
//                .url("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=476257183,1617052169&fm=27&gp=0.jpg")//
//                .build()//context.getExternalCacheDir().getPath()、、Environment.getExternalStorageDirectory()
//                .execute(new FileCallBack( this.getExternalCacheDir().getPath()+"", "grilImg","student","sum","")//
//                {//mProgressBar.setProgress((int) (100 * progress));
//                    @Override
//                    public void inProgress(float progress) {
//
//                    }
//
//                    @Override
//                    public void inProgress(long total, long sum, float progress) {
//                        super.inProgress(total, sum, progress);
//                        if (sum >1000) {
//                            OkHttpUtils.getInstance().cancelTag(MainActivity.this);
//                        }else {
////                            FileCallBack.savePreferences("student","sum",sum);
//                        }
//                    }
//
//                    public void onError(Request request, Exception e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(File file) {
//                        System.out.println("downloadImage yes");
//                        Bitmap bitmap = loadImageFromLocalToView(file.getAbsolutePath(), simpleImage);
//                        if (bitmap != null) {
//                            simpleImage.setImageBitmap(bitmap);
//                            return;
//                        }else {
//                            file.delete();
//                        }
//                    }
//                });
//    }

//    public static Bitmap loadImageFromLocalToView(String path, ImageView imageView) {
//        Bitmap bm = decodeSampledBitmapFromPath(path, 2000, 4000);
//        return bm;
//    }
//
//    public static Bitmap decodeSampledBitmapFromPath(String path, int width, int height) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(path, options);
//        options.inSampleSize = caculateInSampleSize(options, width, height);
//        options.inJustDecodeBounds = false;
//        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
//        return bitmap;
//    }
//
//    public static int caculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        int width = options.outWidth;
//        int height = options.outHeight;
//        int inSampleSize = 1;
//        if(width > reqWidth || height > reqHeight) {
//            int widthRadio = Math.round((float)width * 1.0F / (float)reqWidth);
//            int heightRadio = Math.round((float)height * 1.0F / (float)reqHeight);
//            inSampleSize = Math.max(widthRadio, heightRadio);
//        }
//
//        return inSampleSize;
//    }

}

