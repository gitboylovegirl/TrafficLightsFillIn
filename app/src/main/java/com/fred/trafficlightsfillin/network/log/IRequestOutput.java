package com.fred.trafficlightsfillin.network.log;


/**
 *
 * @author zhaochao
 * @date 17/11/28
 */
public interface IRequestOutput {

    /**
     * 输出请求日志
     * @param requestLog
     */
    void onLogOutput(RequestLog requestLog);
}
