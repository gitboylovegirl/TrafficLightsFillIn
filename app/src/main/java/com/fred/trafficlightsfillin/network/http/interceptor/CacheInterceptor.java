package com.fred.trafficlightsfillin.network.http.interceptor;

import com.fred.trafficlightsfillin.network.NetEnv;
import com.fred.trafficlightsfillin.utils.NetWorkUtil;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * okhttp缓存设置
 */

public class CacheInterceptor implements Interceptor {

    private final String TAG = "CacheInterceptor";

    public CacheInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        //没网强制从缓存读取(必须得写，不然断网状态下，退出应用，或者等待一分钟后，就获取不到缓存）
        if (!NetWorkUtil.isNetDeviceAvailable(NetEnv.getInstance().getContext())) {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        }
        Response response = chain.proceed(request);
        Response responseLatest;
        if (NetWorkUtil.isNetDeviceAvailable(NetEnv.getInstance().getContext())) {
            int maxAge = 60 * 3; //有网失效三分钟
            responseLatest = response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build();
        } else {
            int maxStale = 60 * 60 * 6; // 没网失效6小时
            responseLatest = response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build();
        }
        return responseLatest;
    }

}
