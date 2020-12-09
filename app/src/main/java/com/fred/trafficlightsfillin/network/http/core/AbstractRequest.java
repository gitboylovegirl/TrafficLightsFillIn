package com.fred.trafficlightsfillin.network.http.core;

import com.fred.trafficlightsfillin.network.http.response.ICallback;

/**
 * User: chw
 * Date: 2017/7/27
 */

public abstract class AbstractRequest {

    public abstract void getAsync(ICallback listener);

    public abstract void getAsyncTwo(ICallback listener);

    public abstract void postAsync(ICallback listener);

    public abstract void postBodyAsync(ICallback listener);

    public abstract void uploadFiles(ICallback listener);

    public abstract void putAsync(ICallback listener);

}
