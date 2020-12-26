package com.fred.trafficlightsfillin.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.fred.trafficlightsfillin.base.RequestApi;
import com.fred.trafficlightsfillin.intersection.bean.StageResponse;
import com.fred.trafficlightsfillin.network.http.ProRequest;
import com.fred.trafficlightsfillin.network.http.response.ICallback;
import com.fred.trafficlightsfillin.query.bean.RoadResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 阶段次序工具类
 */
public class RoadDataUtil {


    private final static List<RoadResponse.RoadChannel> dataList = new ArrayList<>();

    public static List<RoadResponse.RoadChannel> getDatatList(){
        return dataList;
    }


    public static void init(){
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.ROAD_PLACE) + "/all")
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh-token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .getAsync(new ICallback<RoadResponse>() {
                    @Override
                    public void onSuccess(RoadResponse response) {
                        if (response.data != null && response.data.size() > 0) {
                            dataList.clear();
                            dataList.addAll(response.data);
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {

                    }
                });
    }
}
