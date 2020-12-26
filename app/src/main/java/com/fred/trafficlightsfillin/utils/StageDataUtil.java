package com.fred.trafficlightsfillin.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.fred.trafficlightsfillin.base.RequestApi;
import com.fred.trafficlightsfillin.intersection.bean.StageResponse;
import com.fred.trafficlightsfillin.network.http.ProRequest;
import com.fred.trafficlightsfillin.network.http.response.ICallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 阶段次序工具类
 */
public class StageDataUtil {

    private static Map<String, Bitmap> stageNo2BitMap = null;
    private static Map<String, String> hash2StageNo = null;

    private static List<StageResponse.StageChanel> dataList = null;

    public static List<StageResponse.StageChanel> getAllStage(){
        if(dataList == null){
            init();
        }
        return dataList;
    }

    /**
     * 根据阶段获取Bitmap
     *
     * @param no
     * @return
     */
    public static Bitmap getBitMapByStageNo(String no){
        if(no == null || "".equals(no.trim())){
            return null;
        }
        try{
            Integer n = Integer.parseInt(no.trim());
            if(n < 1){
                return null;
            }
        } catch (Exception e){}
        if(stageNo2BitMap == null){
            init();
        }
        return stageNo2BitMap.get(no);
    }

    /**
     * 根据阶段图片hash获取阶段次序
     *
     * @param hash
     * @return
     */
    public static String getStageNoByHash(String hash){
        if(hash == null || "".equals(hash.trim())){
            return null;
        }
        if(hash2StageNo == null){
            init();
        }
        return hash2StageNo.get(hash);
    }

    public static void init(){
        stageNo2BitMap = new HashMap<>();
        hash2StageNo = new HashMap<>();
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.STAGE_LIST))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh-token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .addParam("pageSize", "100")
                .addParam("pageNum", "1")
                .build()
                .postAsync(new ICallback<StageResponse>() {
                    @Override
                    public void onSuccess(StageResponse response) {
                        if (response.code == 0) {
                            if(response.data == null || response.data.size() == 0){
                                return;
                            }
                            dataList = response.data;
                            for (StageResponse.StageChanel stageChanel : response.data
                                 ) {
                                if(stageChanel.image == null || "".equals(stageChanel.image.trim())){
                                    continue;
                                }
                                String[] split = stageChanel.image.split(",");
                                if(split.length < 2){
                                    continue;
                                }
                                try{
                                    String base64 = split[1];
                                    byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                                    stageNo2BitMap.put(stageChanel.no, BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                                    hash2StageNo.put(stageChanel.hash, stageChanel.no);
                                }catch (Exception e){
                                    Log.e("fred", "Stage data error!"+e.getMessage());
                                }
                            }
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                    }
                });
    }
}
