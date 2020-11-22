package com.fred.trafficlightsfillin.network.http.core;

import android.os.Build;

import com.fred.trafficlightsfillin.network.NetEnv;
import com.fred.trafficlightsfillin.network.http.interceptor.HeaderInterceptor;
import com.fred.trafficlightsfillin.network.http.interceptor.LoggerInterceptor;
import com.fred.trafficlightsfillin.network.http.interceptor.NetInterceptor;
import com.fred.trafficlightsfillin.network.http.interceptor.RetryInterceptor;
import com.fred.trafficlightsfillin.utils.LogUtils;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 类名称:RetrofitManager
 * 类描述:请求公共配置
 */
public class RetrofitManager {

    private Retrofit mRetrofit;
    private OkHttpClient mClient;
    private static RetrofitManager mRetrofitManager;

    private static final long DEFAULT_TIMEOUT = 12;
    private String mDefaultBaseUrl;

    private RetrofitManager() {
        mDefaultBaseUrl = NetEnv.getInstance().getBaseUrl();
        RetrofitBuilder builder = new RetrofitBuilder();
        builder.setConnTimeOut(DEFAULT_TIMEOUT);
        builder.setReadTimeOut(DEFAULT_TIMEOUT);
        builder.setWriteTimeOut(DEFAULT_TIMEOUT);
        builder.setContext(NetEnv.getInstance().getContext());
        mClient = getOkHttpClient(builder);
        builder.okHttpClient = mClient;
        mRetrofit = buildRetrofit(builder);
    }

    public static RetrofitManager getInstance() {
        if (mRetrofitManager == null) {
            synchronized (RetrofitManager.class) {
                if (mRetrofitManager == null) {
                    mRetrofitManager = new RetrofitManager();
                }
            }
        }

        return mRetrofitManager;
    }

    /**
     * 返回默认初始化retrofit,
     * 有定制参数需求，使用{@link #getRetrofitBuilder}
     *
     * @return
     */
    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    public static <T> T getRetrofitCall(Class<T> service) {
        return getInstance().getRetrofit().create(service);
    }

    /**
     * 生成builder，设置okhttp和retrofit参数
     *
     * @return
     */
    public RetrofitBuilder getRetrofitBuilder() {
        return new RetrofitBuilder();
    }

    public Retrofit generateRetrofit(RetrofitBuilder builder) {
        if (builder == null) {
            throw new RuntimeException("builder can't be null");
        }

        boolean isNew = false;
        builder.okHttpClient = mClient;

        //增加超时设置
        OkHttpClient.Builder cloneBuilder = null;
        if (builder.connTimeOut > 0 || builder.readTimeOut > 0 || builder.writeTimeOut > 0) {
            cloneBuilder = mClient.newBuilder();
            if (builder.connTimeOut > 0) {
                cloneBuilder.connectTimeout(builder.connTimeOut, TimeUnit.SECONDS);
            }

            if (builder.readTimeOut > 0) {
                cloneBuilder.readTimeout(builder.readTimeOut, TimeUnit.SECONDS);
            }

            if (builder.writeTimeOut > 0) {
                cloneBuilder.writeTimeout(builder.writeTimeOut, TimeUnit.SECONDS);
            }
        }

        if (cloneBuilder != null) {
            isNew = true;
            builder.okHttpClient = cloneBuilder.build();
        }

        boolean hasHeaders = builder.headers != null && builder.headers.size() > 0;
        boolean hasRetry = builder.retryCount > 0;

        //增加Header
        if (hasHeaders || hasRetry) {
            isNew = true;
            builder.okHttpClient = getOkHttpClient(builder);
        }

        if (isNew) {
            return buildRetrofit(builder);
        } else {
            return mRetrofit;
        }
    }

    /**
     *
     */
    public void buildNewRetrofit(){
        mDefaultBaseUrl = NetEnv.getInstance().getBaseUrl();
        RetrofitBuilder builder = new RetrofitBuilder();
        builder.setConnTimeOut(DEFAULT_TIMEOUT);
        builder.setReadTimeOut(DEFAULT_TIMEOUT);
        builder.setWriteTimeOut(DEFAULT_TIMEOUT);

        mClient = getOkHttpClient(builder);
        builder.okHttpClient = mClient;
        mRetrofit = buildRetrofit(builder);
    }

    public Retrofit buildRetrofit(RetrofitBuilder builder) {
        if (builder.factory == null) {
            builder.factory = GsonConverterFactory.create();
        }
        return new Retrofit.Builder()
                .baseUrl(mDefaultBaseUrl)
                .addConverterFactory(builder.factory)
                .client(builder.okHttpClient)
                .build();
    }

    private OkHttpClient getOkHttpClient(RetrofitBuilder builder) {
        // 50 MiB
        int cacheSize = 50 * 1024 * 1024;
        Cache cache = new Cache(new File(NetEnv.getInstance().getContext().getCacheDir(), "ZYCache"), cacheSize);
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .connectTimeout(builder.connTimeOut, TimeUnit.SECONDS)
                .readTimeout(builder.readTimeOut, TimeUnit.SECONDS)
                .writeTimeout(builder.writeTimeOut, TimeUnit.SECONDS)
                .cache(cache);

        if (builder.retryCount > 0) {
            okHttpBuilder.addInterceptor(new RetryInterceptor(builder.retryCount));
        }

        okHttpBuilder.addInterceptor(new HeaderInterceptor(builder.headers));

        if (LogUtils.isLogEnabled) {
            okHttpBuilder.addInterceptor(new LoggerInterceptor("myLog"));
        }

        if (Build.VERSION.SDK != null && Build.VERSION.SDK_INT > 13) {
            okHttpBuilder.addInterceptor(new NetInterceptor());
        }

        List<Interceptor> interceptors = NetEnv.getInstance().getOkInterceptors();
        if (interceptors.size() > 0) {
            for (int i = 0, count = interceptors.size(); i < count; i++) {
                okHttpBuilder.addInterceptor(interceptors.get(i));
            }
        }

        if (NetEnv.getInstance().getNetworkInterceptor() != null) {
            okHttpBuilder.addInterceptor(NetEnv.getInstance().getNetworkInterceptor());
        }

        return okHttpBuilder.build();
    }

}
