package com.fred.trafficlightsfillin.network.http.response;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.fred.trafficlightsfillin.utils.JsonUtil;

import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * User: chw
 * Date: 2017/8/9
 */

public class CallBackResolve {
    /**
     * 返回数据解析
     *
     * @param response
     * @param listener
     */
    public static void parseSuccess(Response<ResponseBody> response, ICallback listener,Context context) {
        try {
            String result = response.body().string();
            response.body().close();

            if (!TextUtils.isEmpty(result)) {
                boolean returnString = false;
                Type genericType = listener.getGenericType();
                if (genericType instanceof Class) {
                    switch (((Class) genericType).getSimpleName()) {
                        case "Object":
                        case "String":
                            returnString = true;
                            break;
                        default:
                            break;
                    }
                }
//                LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
//                Intent intent = new Intent("LOCAL_BROADCAST");
//                broadcastManager.sendBroadcast(intent);
                if (returnString) {
                    listener.onResolveSuccess(result);
                } else {
                    listener.onResolveSuccess(JsonUtil.parseBean(result, listener.getGenericType()));
                }

            } else {
                listener.onResolveFail(ICallback.ERROR_CODE_DEFAULT, "callback parse result == null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.onResolveFail(ICallback.ERROR_CODE_DEFAULT, "callback parse exception " + e.getMessage());
        }
    }
}
