package com.example.lixiang.okhttputil.cookie;

import android.content.Context;
import android.text.TextUtils;

import com.example.lixiang.okhttputil.Bean.CookieListBean;
import com.example.lixiang.okhttputil.OkHttpUtils;
import com.example.lixiang.okhttputil.utils.ToastUtil;
import com.google.gson.Gson;

import org.kymjs.kjframe.utils.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public final class SimpleCookieJar implements CookieJar {


    private Context context;

    public SimpleCookieJar(Context context) {
        this.context = context;
    }
    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        List<Cookie> allCookies = new ArrayList<Cookie>();
        allCookies.addAll(cookies);
        //先查询是否存在cookie  如果存在则不覆盖，不存在则覆盖
        //获取本地的cookie
        String s = OkHttpUtils.getCookie();
        if (TextUtils.isEmpty(s)) {
//            StringBuilder strBudilder = new StringBuilder();
                CookieListBean cookieListBean = new CookieListBean();
            ArrayList<CookieListBean.CookieBean> cookielist= new ArrayList<>();
            cookieListBean.setChannels(cookielist);
            for (Cookie c : cookies) {
            CookieListBean.CookieBean cookieBean = new CookieListBean.CookieBean();
                cookieBean.setName(c.name());
                cookieBean.setValue(c.value());
                cookieBean.setExpiresAt(c.expiresAt());
                cookieBean.setDomain(c.domain());
                cookieBean.setPath(c.path());
                cookielist.add(cookieBean);
            }
                String Str_cookieBean = new Gson().toJson(cookieListBean);
            OkHttpUtils.setCookie(Str_cookieBean);
        }
    }

    /**@author LiXiang create at 2018/1/29 14:48*/
    /**Explain : 获取当前的cookie
     *
     * @param context Context
     * @return
     */
    public static List<Cookie> getCookie(Context context){
        List<Cookie> allCookies = new ArrayList<>();
        String s = OkHttpUtils.getCookie();

        if (s != null) {
            s = s.trim();
            CookieListBean cookieListBean = new Gson().fromJson(s, CookieListBean.class);
            try {
                for (CookieListBean.CookieBean cookieBean : cookieListBean.getChannels()) {
                    Cookie.Builder builder = new Cookie.Builder();
                    builder.name(cookieBean.getName());

                    builder.value(cookieBean.getValue());
                    builder.expiresAt(cookieBean.getExpiresAt());
                    builder.domain(cookieBean.getDomain());
                    builder.path(cookieBean.getPath());
                    Cookie build = builder.build();
//                         allCookies.clear();
                    allCookies.add(build);
                }

            }catch (Exception e){
                PreferenceHelper.remove(context, "isLogin", "isLogin");
                ToastUtil.showToast(context,"身份验证有误，请重新登录");
            }
        }
        return allCookies;
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
    	 List<Cookie> allCookies = getCookie(context);
//         访问网络，先访问本地
         List<Cookie> result = new ArrayList<>();

         for (Cookie cookie : allCookies) {
             if (cookie.matches(url)) {
                 result.add(cookie);
             }
         }
        return result;
    }
}
