package com.example.lixiang.okhttputil.request;

import java.util.Map;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by lixiang on 2017/4/26.
 */
public class DeleteRequest extends OkHttpRequest
{
    public DeleteRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers)
    {
        super(url, tag, params, headers,"delete");
    }

    @Override
    protected RequestBody buildRequestBody()
    {
        return null;
    }

    @Override
    protected Request buildRequest(Request.Builder builder, RequestBody requestBody)
    {
        return builder.delete().build();
    }




}
