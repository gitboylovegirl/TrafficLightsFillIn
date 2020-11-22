package com.fred.trafficlightsfillin.network.http.interceptor;

import com.fred.trafficlightsfillin.network.NetEnv;
import com.fred.trafficlightsfillin.network.log.IRequestOutput;
import com.fred.trafficlightsfillin.network.log.JsonLog;
import com.fred.trafficlightsfillin.network.log.RequestLog;
import com.fred.trafficlightsfillin.utils.LogUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 重试拦截器
 */
public class RetryInterceptor implements Interceptor {

    private static final String TAG = "http retry ==";
    /**
     * 最大重试次数
     **/
    private int maxRetry;
    /**
     * 假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）
     **/
    private int retryNum = 0;

    public RetryInterceptor(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        RequestLog requestLog = JsonLog.logForRequest(TAG, request);

        LogUtils.d("retry: ", "retryNum = " + retryNum);
        Response response = chain.proceed(request);
        while (!response.isSuccessful() && retryNum < maxRetry) {
            retryNum++;
            LogUtils.d("retryNum index = " + retryNum);

            long startTime = System.nanoTime();
            response = chain.proceed(request);
            long costTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            JsonLog.logForResponse(TAG, response, costTime, requestLog);

            IRequestOutput iRequestLog = NetEnv.getInstance().getRequestLog();
            if (iRequestLog != null) {
                iRequestLog.onLogOutput(requestLog);
            }
        }
        return response;
    }
}
