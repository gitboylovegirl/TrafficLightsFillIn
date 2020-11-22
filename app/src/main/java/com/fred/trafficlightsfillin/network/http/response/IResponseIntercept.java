package com.fred.trafficlightsfillin.network.http.response;

/**
 * User: chw
 * Date: 2018/5/15
 * 数据请求返回拦截处理
 */
public interface IResponseIntercept{
    /**
     * 拦截数据回调
     * @param response
     * @return
     */
    <T> boolean onIntercept(T response);

    /**
     * 拦截信息
     * @return
     */
    String onInterceptMessage();
}
