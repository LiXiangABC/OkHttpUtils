package com.example.lixiang.okhttputil;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.example.lixiang.okhttputil.builder.DeleteBuilder;
import com.example.lixiang.okhttputil.builder.GetBuilder;
import com.example.lixiang.okhttputil.builder.PostFileBuilder;
import com.example.lixiang.okhttputil.builder.PostFormBuilder;
import com.example.lixiang.okhttputil.builder.PostStringBuilder;
import com.example.lixiang.okhttputil.callback.Callback;
import com.example.lixiang.okhttputil.cookie.SimpleCookieJar;
import com.example.lixiang.okhttputil.https.HttpsUtils;
import com.example.lixiang.okhttputil.request.RequestCall;
import com.example.lixiang.okhttputil.utils.LogSwitchUtils;
import com.example.lixiang.okhttputil.utils.ToastUtil;
import com.example.lixiang.quickcache.QuickCacheUtil;
import com.example.lixiang.quickcache.bean.LoadingCacheStringBean;

import org.kymjs.kjframe.utils.PreferenceHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by zhy on 15/8/17.
 */
public class OkHttpUtils
{
    public static final String TAG = "OkHttpUtils";
    public static final long DEFAULT_MILLISECONDS = 10000;
    private static OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;
    private static Context context;
    private int maxLoadTimes  = 1;//当超时，设定的自动重连次数
    private OkHttpUtils()
    {

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        //cookie enabled
        okHttpClientBuilder.cookieJar(new SimpleCookieJar(context));
        mDelivery = new Handler(Looper.getMainLooper());


        if (true)
        {
            okHttpClientBuilder.hostnameVerifier(new HostnameVerifier()
            {
                @Override
                public boolean verify(String hostname, SSLSession session)
                {
                    return true;
                }
            });
        }

        mOkHttpClient = okHttpClientBuilder.build();
    }

    private boolean debug;
    private String tag;

    public OkHttpUtils debug(String tag)
    {
        debug = true;
        this.tag = tag;
        return this;
    }


    public static OkHttpUtils getInstance()
    {
        if (mInstance == null)
        {
            synchronized (OkHttpUtils.class)
            {
                if (mInstance == null)
                {
                    mInstance = new OkHttpUtils();
                }
            }
        }
        return mInstance;
    }

    public Handler getDelivery()
    {
        return mDelivery;
    }

    public OkHttpClient getOkHttpClient()
    {
        return mOkHttpClient;
    }


    public static GetBuilder get()
    {
        return new GetBuilder();
    }//当前指定为2.36版本

    public static DeleteBuilder delete() {return new DeleteBuilder();}//当前指定为2.36版本

    public static PostStringBuilder postString()
    {
        return new PostStringBuilder();
    }

    public static PostFileBuilder postFile()
    {
        return new PostFileBuilder();
    }

    public static PostFormBuilder post()
    {
        return new PostFormBuilder();//当前指定为2.36版本
    }

    public static Map<String, String> heardsMap;
    public static Map<String, String> paramsMap;
    /**        Explain : 设置公共请求头
    * @author LiXiang create at 2018/10/15 11:51*/
    public static void setCommonHeards(Map<String, String> heards){
        heardsMap = heards;
    }

    /**        Explain : 设置公共请求参数
    * @author LiXiang create at 2018/10/15 11:51*/
    public static void setCommonParams(Map<String, String> params){
        paramsMap = params;
    }

    /**        Explain : 获取公共请求头
    * @author LiXiang create at 2018/10/15 15:01*/
    public static Map getCommonHeards(){return heardsMap;}

    /**        Explain : 获取公共请求参数
    * @author LiXiang create at 2018/10/15 15:01*/
    public static Map getCommonParams(){return paramsMap;}

    /**        Explain : 将参数写入指定集合
    * @author LiXiang create at 2018/10/15 14:57*/
    public static Map joinMap(Map<String, String> targetMap,Map<String, String> dataMap){
        if (dataMap != null) {
        //使用 entrySet()遍历集合
        Set<Map.Entry<String,String>> entry = dataMap.entrySet();
        Iterator<Map.Entry<String,String>> ite = entry.iterator();
        while(ite.hasNext()) {
            Map.Entry<String, String> en = ite.next();
            String key = en.getKey();
            String value = en.getValue();
            targetMap.put(key,value);
        }
        }
        return targetMap;
    }


    public void execute(final RequestCall requestCall, Callback callback, LoadingCacheStringBean loadingCacheString)
    {
        if (debug)
        {
            if(TextUtils.isEmpty(tag))
            {
                tag = TAG;
            }
            Log.d(tag, "{method:" + requestCall.getRequest().method() + ", detail:" + requestCall.getOkHttpRequest().toString() + "}");
        }

        if (callback == null)
            callback = Callback.CALLBACK_DEFAULT;
        final Callback finalCallback = callback;


        requestCall.getCall().enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                call.cancel();
                if (e.toString().contains("TimeoutException")) {
                        new Handler(Looper.getMainLooper()).post(()->{
                            ToastUtil.showToast(context, "连接超时,请重新操作");});
                        sendFailResultCallback(call, e, finalCallback);
                }else {
                    if (e.toString().contains("No address associated with hostname")) {
                        new Handler(Looper.getMainLooper()).post(()->{ToastUtil.showToast(context, "当前无网络，请重新连接");});
                    }
                    sendFailResultCallback(call, e, finalCallback);
                }
                LogSwitchUtils.Log("onFailure e.getCause()",e.getCause()+"");
                LogSwitchUtils.Log("onFailure e.getMessage()",e.getMessage()+"");
                LogSwitchUtils.Log("onFailure e.toString()",e.toString()+"");
                LogSwitchUtils.Log("onFailure e.getStackTrace()",e.getStackTrace().toString()+"");
            }

            @Override
            public void onResponse(final Call call, final Response response) {
//                call.cancel();
                if (response.code() >= 400 && response.code() <= 599) {
                    try {
//                        response.body().close();
                        sendFailResultCallback(call, new RuntimeException(response.body().string()), finalCallback);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                try {
                    Object o = finalCallback.parseNetworkResponse(response);
                    sendSuccessResultCallback(o, finalCallback,loadingCacheString,requestCall.getOpenCache());
                } catch (Exception e) {
                    sendFailResultCallback(call, e, finalCallback);
                }
            }
        });
    }


    public void sendFailResultCallback(final Call call, final Exception e, final Callback callback)
    {
        if (callback == null) return;

        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(call, e);
                callback.onAfter();
        }
        });
    }


    /**        Explain : 对外提供数据返回cookie是否存在问题的检测接口
    * @author LiXiang create at 2018/3/14 16:04*/
    public  interface checkCookieListener{
     /**@author LiXiang create at 2018/3/14 16:08*/
        /**Explain :
         * @param object ：服务器返回数据
         * @return 当正确返回true，反则反之。
         */
        public  boolean chekCookie(Object object );
    }


    public checkCookieListener chekCookie;

    public void sendSuccessResultCallback(final Object object, final Callback callback, LoadingCacheStringBean loadingCacheString, boolean isOpenCache)
    {
        if (callback == null) return;
        mDelivery.post(new Runnable()
        {
            @Override
            public void run()
            {
                    if(object != null ){
                        System.out.println("sendSuccessResultCallback OK");
                        /**        Explain : 当存在chekCookie检测监听，则进行检测
                         * @author LiXiang create at 2018/3/14 15:58*/
                        if (chekCookie != null) {
                            if (chekCookie.chekCookie(object) == false) {
                                return;//当cookie存在异常或未存在，则后面不进行数据处理
                            }
                        }

                callback.onResponse(object);
                        /**        Explain : 当只有返回的数据不为空，并且打开了缓存的时候才向本地写入
                         * @author LiXiang create at 2017/11/20 15:58*/
                        if (loadingCacheString != null) {
                        LogSwitchUtils.Log("loadingCacheString","即将写入缓存"+"url :"+loadingCacheString.getUrl());
                        }
                        if (loadingCacheString != null && isOpenCache == true) {
                        LogSwitchUtils.Log("loadingCacheString","正在写入缓存"+"url :"+loadingCacheString.getUrl());
                QuickCacheUtil.getInstance().putCacheString(loadingCacheString,object.toString());
                        LogSwitchUtils.Log("loadingCacheString","缓存写入完成"+"url :"+loadingCacheString.getUrl());
                        }
                }
                callback.onAfter();
            }
        });
    }

    public void cancelTag(Object tag)
    {
        for (Call call : mOkHttpClient.dispatcher().queuedCalls())
        {
            if (tag.equals(call.request().tag()))
            {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls())
        {
            if (tag.equals(call.request().tag()))
            {
                call.cancel();
            }
        }
    }


    public void setCertificates(InputStream... certificates)
    {
        mOkHttpClient = getOkHttpClient().newBuilder()
                .sslSocketFactory(HttpsUtils.getSslSocketFactory(certificates, null, null))
                .build();
    }


    public OkHttpUtils setTimeout(int connectTimeout,int readTimeout,int writeTimeout, TimeUnit units)
    {
        mOkHttpClient = getOkHttpClient().newBuilder()
                .connectTimeout(connectTimeout,TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout,TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout,TimeUnit.MILLISECONDS).build();
        return this;
    }

  //从APPlication里面获取context
    public static void setContext(Context contexts) {
         context = contexts;
    }


    public  static  Context getContext(){
     return context;
    }

    public  static  void setCookie(String cookie){
        PreferenceHelper.write(context, "isLogin", "isLogin", cookie);
        System.out.println("OkHttpUtils-setCookie");
    }
    public  static  String getCookie(){
        System.out.println("OkHttpUtils-getCookie");
        return  PreferenceHelper.readString(context, "isLogin", "isLogin",null);
    }
    public  static  void removeCookie(){
        System.out.println("OkHttpUtils-removeCookie");
        PreferenceHelper.remove(context, "isLogin", "isLogin");
    }

    
}

