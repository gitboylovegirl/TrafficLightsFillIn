package com.fred.trafficlightsfillin.network.http.response;

import android.os.Handler;
import android.os.Looper;


import com.fred.trafficlightsfillin.network.NetEnv;
import com.fred.trafficlightsfillin.utils.LogUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * User: chw
 * Date: 2017/7/26
 */

public abstract class ICallback<T> {

    private static Handler mainHandler = new Handler(Looper.getMainLooper());

    private final Type mGenericType;

    public static final int ERROR_CODE_DEFAULT = -1000;
    /** 数据请求拦截error **/
    public static final int ERROR_CODE_INTERCEPT = -1001;

    public ICallback() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            this.mGenericType = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
        } else {
            this.mGenericType = Object.class;
        }
    }

    /**
     * 处理返回数据回调
     * @param response
     */
    public void onResolveSuccess(final T response) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                InterceptResult interceptResult = NetEnv.getInstance()
                        .isInterceptResponse(response);

                if (interceptResult.isIntercept) {
                    onResolveFail(ERROR_CODE_INTERCEPT, interceptResult.interceptMessage);
                    return;
                }

                onSuccess(response);
            }
        });
    }

    public void onResolveFail(final int status, final String errorMsg) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                LogUtils.d("onResolveFail : " + errorMsg);
                onFail(status, errorMsg);
            }
        });
    }

    public abstract void onSuccess(T response);

    public abstract void onFail(int errorCode, String errorMsg);

    public Type getGenericType() {
        return mGenericType;
    }

}
