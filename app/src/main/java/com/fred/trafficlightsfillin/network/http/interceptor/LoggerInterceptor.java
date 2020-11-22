package com.fred.trafficlightsfillin.network.http.interceptor;

import android.text.TextUtils;

import com.fred.trafficlightsfillin.network.NetEnv;
import com.fred.trafficlightsfillin.network.log.IRequestOutput;
import com.fred.trafficlightsfillin.network.log.JsonLog;
import com.fred.trafficlightsfillin.network.log.RequestLog;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 类名称:LoggerInterceptor
 * 类描述:请求参数日志监听
 */
public class LoggerInterceptor implements Interceptor {
    private static String TAG = "Logger";

    public LoggerInterceptor(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            TAG = tag;
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        RequestLog requestLog = JsonLog.logForRequest(TAG, request);

        long startTime = System.nanoTime();
        Response response = chain.proceed(request);
        long costTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

        Response newResponse = JsonLog.logForResponse(TAG, response, costTime, requestLog);

        IRequestOutput iRequestLog = NetEnv.getInstance().getRequestLog();
        if (iRequestLog != null) {
            iRequestLog.onLogOutput(requestLog);
        }

        return newResponse;
    }

}
