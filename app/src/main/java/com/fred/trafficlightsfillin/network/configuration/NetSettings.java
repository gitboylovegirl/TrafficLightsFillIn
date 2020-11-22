package com.fred.trafficlightsfillin.network.configuration;

import com.fred.trafficlightsfillin.network.http.response.IResponseIntercept;
import com.fred.trafficlightsfillin.network.log.IRequestOutput;
import com.fred.trafficlightsfillin.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;

/**
 */
public class NetSettings {

    private String mBaseUrl = "";

    private IRequestOutput mRequestLog;

    private ICommonHeader mCommonHeaders;

    private Interceptor networkInterceptor;
    private List<Interceptor> okInterceptors = new ArrayList<>();
    private List<IResponseIntercept> responseIntercepts = new ArrayList<>();

    public void setDebugEnable(boolean isDebug) {
        LogUtils.isLogEnabled = isDebug;
    }

    public void setBaseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return mBaseUrl;
    }

    public void setRequestLog(IRequestOutput iRequestLog) {
        mRequestLog = iRequestLog;
    }

    public IRequestOutput getRequestLog() {
        return mRequestLog;
    }

    public void addOkInterceptor(Interceptor interceptor) {
        okInterceptors.add(interceptor);
    }

    public List<Interceptor> getOkInterceptors() {
        return okInterceptors;
    }

    public void addNetworkInterceptor(Interceptor networkInterceptor) {
        this.networkInterceptor = networkInterceptor;
    }

    public Interceptor getNetworkInterceptor() {
        return networkInterceptor;
    }

    public void addResponseInterceptor(IResponseIntercept responseIntercept) {
        responseIntercepts.add(responseIntercept);
    }

    public List<IResponseIntercept> getResponseInterceptors() {
        return responseIntercepts;
    }

    public void setCommonHeaders(ICommonHeader commonHeaders) {
        mCommonHeaders = commonHeaders;
    }

    public ICommonHeader getCommonHeader() {
        return mCommonHeaders;
    }
}
