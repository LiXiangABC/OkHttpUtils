package com.example.lixiang.okhttputil.request;

import com.example.lixiang.okhttputil.OkHttpUtils;
import com.example.lixiang.okhttputil.callback.Callback;
import com.example.lixiang.okhttputil.utils.LogSwitchUtils;
import com.example.lixiang.quickcache.QuickCacheUtil;
import com.example.lixiang.quickcache.bean.LoadingCacheStringBean;
import com.example.lixiang.quickcache.bean.onResponseCacheListener;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhy on 15/12/15.
 */
public class RequestCall
{
    private OkHttpRequest okHttpRequest;
    private Request request;
    private Call call;

    private long readTimeOut;
    private long writeTimeOut;
    private long connTimeOut;

    private OkHttpClient clone;




    public RequestCall(OkHttpRequest request)
    {
        this.okHttpRequest = request;
    }

    public RequestCall readTimeOut(long readTimeOut)
    {
        this.readTimeOut = readTimeOut;
        return this;
    }

    public RequestCall writeTimeOut(long writeTimeOut)
    {
        this.writeTimeOut = writeTimeOut;
        return this;
    }

    public RequestCall connTimeOut(long connTimeOut)
    {
        this.connTimeOut = connTimeOut;
        return this;
    }


    public Call generateCall(Callback callback)
    {
        request = generateRequest(callback);

        if (readTimeOut > 0 || writeTimeOut > 0 || connTimeOut > 0)
        {
            readTimeOut = readTimeOut > 0 ? readTimeOut :    OkHttpUtils.DEFAULT_MILLISECONDS;
            writeTimeOut = writeTimeOut > 0 ? writeTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;
            connTimeOut = connTimeOut > 0 ? connTimeOut :    OkHttpUtils.DEFAULT_MILLISECONDS;
            clone = OkHttpUtils.getInstance().getOkHttpClient().newBuilder()
                    .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
                    .writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
                    .connectTimeout(connTimeOut, TimeUnit.MILLISECONDS)
                    .build();
            call = clone.newCall(request);
        } else
        {
            call = OkHttpUtils.getInstance().getOkHttpClient().newCall(request);
        }
        return call;
    }

    private Request generateRequest(Callback callback)
    {
        return okHttpRequest.generateRequest(callback);
    }

    public void execute(Callback callback)
    {

        if (okHttpRequest.params != null && !okHttpRequest.params.isEmpty())
        {
            for (String key : okHttpRequest.params.keySet())
            {
                System.out.println("params name :"+key+"    value:"+okHttpRequest.params.get(key));
            }
        }

        LogSwitchUtils.Log("正在发起一次请求  url:","  "+okHttpRequest.url);
        if (isOpenCache) {
            if (callback == null) {
//                throw new Exception("please check callback ,because callback = null");
            }
            RequestCall requestCall  = this;

            LoadingCacheStringBean loadingCacheString = QuickCacheUtil.getInstance().LoadingCacheString();
            loadingCacheString.setRequestType(QuickCacheUtil.stringToRequestType(okHttpRequest.requestType))
                    .setUrl(okHttpRequest.url)
                    .setParams(okHttpRequest.params)
                    .setValidTime(validTime)
                    .setTag(okHttpRequest.tag)
                    .setAlias(alias)
                    .setIsRefreshCache(getRefreshCache())
                    .setOrc(new onResponseCacheListener() {

                        @Override
                        public void onResponseCache(String onResponseData) {
                            callback.onResponse(onResponseData);
                            LogSwitchUtils.Log("RequestCall 通过缓存获取的数据  url:","  "+okHttpRequest.url);
                        }

                        @Override
                        public void onRequestNetWork() {
                            LogSwitchUtils.Log("RequestCall 通过网络获取的数据  url:","  "+okHttpRequest.url);
                            generateCall(callback);

                            if (callback != null)
                            {
                                callback.onBefore(request);
                            }
                            OkHttpUtils.getInstance().execute(requestCall, callback,loadingCacheString);

                        }
                    })
                    .commit();


        }else {
            generateCall(callback);

            if (callback != null)
            {
                callback.onBefore(request);
            }
            OkHttpUtils.getInstance().execute(this, callback,null);

        }

    }

    public Call getCall()
    {
        return call;
    }

    public Request getRequest()
    {
        return request;
    }

    public OkHttpRequest getOkHttpRequest()
    {
        return okHttpRequest;
    }

    public Response execute() throws IOException
    {
        generateCall(null);
        return call.execute();
    }

    public void cancel()
    {
        if (call != null)
        {
            call.cancel();
        }
    }

    /**        Explain : 自创建
     * @author LiXaing create at 2017/7/20 16:59*/
    private int validTime = 8*60;
    private String alias ;
    private boolean isOpenCache = false;
    private boolean isRefreshCache = false;

    public RequestCall isRefreshCache(boolean isRefreshCache) {
        this.isRefreshCache = isRefreshCache;
        return this;
    }

    public boolean getRefreshCache() {
        return isRefreshCache;
    }

    /**        Explain : 判断是否打开网络缓存的读取
     * @author LiXaing create at 2017/7/20 16:58*/
    public RequestCall isOpenCache(boolean isOpen)
    {
        this.isOpenCache = isOpen;
        return this;
    }

    public boolean getOpenCache()
    {
        return isOpenCache;
    }

    /**        Explain : 设置缓存的有效时间
     * @author LiXaing create at 2017/7/20 17:04*/
    public RequestCall setCacheValidTime(int validTime){
//        if (isOpenCache == false) {
//            throw new Exception("please set isOpenCache = true");
//        }else {
            this.validTime = validTime;
            return this;
//        }
    }

    public RequestCall setCacheAlias(String alias) {
//        if (isOpenCache == false) {
//            throw new Exception("please set isOpenCache = true");
//        }else {
//            if (alias == null) {
//                throw new Exception("alias = null ,please set alias ");
//            }
            this.alias = alias;
            return this;
//        }
    }
}
