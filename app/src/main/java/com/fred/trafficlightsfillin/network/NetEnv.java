package com.fred.trafficlightsfillin.network;

import android.content.Context;

import com.fred.trafficlightsfillin.network.configuration.ICommonHeader;
import com.fred.trafficlightsfillin.network.configuration.NetSettings;
import com.fred.trafficlightsfillin.network.http.response.IResponseIntercept;
import com.fred.trafficlightsfillin.network.http.response.InterceptResult;
import com.fred.trafficlightsfillin.network.log.IRequestOutput;

import java.util.List;

import okhttp3.Interceptor;

/**
 * User: chw
 * Date: 2018/5/15
 */
public class NetEnv {

    private Context mContext;
    private NetSettings mNetSettings;

    private static final class Holder{
        private static NetEnv INSTANCE = new NetEnv();
    }

    public static NetEnv getInstance() {
        return Holder.INSTANCE;
    }

    public void init(Context context, NetSettings netSettings) {
        mContext = context;
        mNetSettings = netSettings;
    }

    public Context getContext() {
        return mContext;
    }

    public void setBaseUrl(String baseUrl) {
        mNetSettings.setBaseUrl(baseUrl);
    }

    public String getBaseUrl() {
        return mNetSettings.getBaseUrl();
    }

    public IRequestOutput getRequestLog(){
        return mNetSettings.getRequestLog();
    }

    public ICommonHeader getCommonHeaders() {
        return mNetSettings.getCommonHeader();
    }

    public List<Interceptor> getOkInterceptors() {
        return mNetSettings.getOkInterceptors();
    }

    public Interceptor getNetworkInterceptor() {
        return mNetSettings.getNetworkInterceptor();
    }

    /**
     * 处理返回数据拦截
     * @param response
     * @param <T>
     * @return
     */
    public <T> InterceptResult isInterceptResponse(T response) {
        List<IResponseIntercept> iResponseIntercepts = mNetSettings.getResponseInterceptors();

        InterceptResult interceptResult = new InterceptResult();
        if (iResponseIntercepts != null && iResponseIntercepts.size() > 0) {
            for (int i = 0, count = iResponseIntercepts.size(); i < count; i++) {
                IResponseIntercept iResponseIntercept = iResponseIntercepts.get(i);
                interceptResult.isIntercept = iResponseIntercept.onIntercept(response);
                interceptResult.interceptMessage = iResponseIntercept.onInterceptMessage();
                if(interceptResult.isIntercept) {
                    break;
                }
            }
        }
        return interceptResult;
    }
}
