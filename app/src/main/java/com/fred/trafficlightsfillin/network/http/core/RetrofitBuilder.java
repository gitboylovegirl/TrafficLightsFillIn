package com.fred.trafficlightsfillin.network.http.core;

import android.content.Context;

import com.fred.trafficlightsfillin.network.http.ProRequest;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * User: chw
 * Date: 2017/7/31
 */

public class RetrofitBuilder {
    public String url;

    public long readTimeOut;
    public long writeTimeOut;
    public long connTimeOut;
    public Context context;
    public int retryCount;

    public Converter.Factory factory;
    public OkHttpClient okHttpClient;

    public Map<String, String> headers = new HashMap<>();

    public RetrofitBuilder() {

    }

    public RetrofitBuilder(ProRequest.RequestBuilder builder) {
        this.readTimeOut = builder.readTimeOut;
        this.writeTimeOut = builder.writeTimeOut;
        this.connTimeOut = builder.connTimeOut;
        this.headers = builder.headers;
        this.factory = builder.factory;
        this.retryCount = builder.retry;
        this.context=builder.mContext;
    }

    public RetrofitBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public RetrofitBuilder setRetryCount(int count) {
        this.retryCount = count;
        return this;
    }

    public RetrofitBuilder setContext(Context context) {
        this.context = context;
        return this;
    }

    public RetrofitBuilder setReadTimeOut(long readTimeOut) {
        this.readTimeOut = readTimeOut;
        return this;
    }

    public RetrofitBuilder setWriteTimeOut(long writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        return this;
    }

    public RetrofitBuilder setConnTimeOut(long connTimeOut) {
        this.connTimeOut = connTimeOut;
        return this;
    }

    public RetrofitBuilder setConvertFactory(Converter.Factory factory) {
        this.factory = factory;
        return this;
    }

    public RetrofitBuilder addHeader(String key, String value) {
        if (key != null && value != null) {
            headers.put(key, value);
        }

        return this;
    }

    public Retrofit buildRetrofit() {
        return RetrofitManager.getInstance().generateRetrofit(this);
    }

    public <T> T buildRetrofitCall(Class<T> service) {
        return RetrofitManager.getInstance().generateRetrofit(this).create(service);
    }
}
