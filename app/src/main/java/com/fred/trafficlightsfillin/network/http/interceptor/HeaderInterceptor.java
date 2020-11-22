package com.fred.trafficlightsfillin.network.http.interceptor;

import com.fred.trafficlightsfillin.network.NetEnv;
import com.fred.trafficlightsfillin.network.configuration.ICommonHeader;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 类名称:HeaderInterceptor
 * 类描述:配置请求头Header
 */

public class HeaderInterceptor implements Interceptor {

    private Map<String, String> mTempHeaders;

    public HeaderInterceptor(Map<String, String> headers) {
        mTempHeaders = headers;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder headerBuilder = request.newBuilder();

        ICommonHeader iCommonHeader = NetEnv.getInstance().getCommonHeaders();
        if (iCommonHeader != null) {
            Map<String, String> commonHeaders = iCommonHeader.getCommonHeader();
            if (commonHeaders != null && commonHeaders.size() > 0) {
                for (String key : commonHeaders.keySet()) {
                    headerBuilder.addHeader(key, commonHeaders.get(key));
                }
            }
        }

        //添加非共性Header
        if (mTempHeaders != null && mTempHeaders.size() > 0) {
            for (String key : mTempHeaders.keySet()) {
                headerBuilder.addHeader(key, mTempHeaders.get(key));
            }
        }

        return chain.proceed(headerBuilder.build());
    }
}
