package com.fred.trafficlightsfillin.network.http.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * author: zhaochao
 * date:   2019/7/24
 * 说明:
 */
public class NetInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder().addHeader("Connection", "close").build();
        return chain.proceed(request);
    }
}
