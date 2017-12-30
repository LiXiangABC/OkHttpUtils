package com.example.lixiang.okhttputil.callback;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class Callback<T>


{
    private  onResponseListener responselistener;

    public Callback(){}
    public Callback(onResponseListener<T> responselistener){
        this.responselistener = responselistener;
    }

    /**
     * UI Thread
     *
     * @param request
     */
    public void onBefore(Request request)
    {
    }

    /**
     * UI Thread
     *
     * @param
     */
    public void onAfter()
    {
    }

    /**
     * UI Thread
     *
     * @param progress
     */
    public void inProgress(float progress)
    {

    }
    /**
     * Thread Pool Thread
     *
     * @param response
     */
//    public abstract T parseNetworkResponse(Response response) throws Exception;
        public Object parseNetworkResponse(Response response)throws Exception{return  response.body().string();}

//    public abstract void onError(Call call, Exception e);
      public void onError (Call call, Exception e){}


    public  void onResponse(T response){
        if (responselistener != null) {
        responselistener.onResponse(response);
        }
    };

    public interface onResponseListener<T>{
         void onResponse(T response);

    }

    public static Callback CALLBACK_DEFAULT = new Callback()
    {

        @Override
        public Object parseNetworkResponse(Response response) throws Exception
        {
            return response;
        }

        @Override
        public void onError(Call call, Exception e)
        {

        }

        @Override
        public void onResponse(Object response)
        {

        }
    };

}