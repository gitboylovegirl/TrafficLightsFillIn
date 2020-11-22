package com.fred.trafficlightsfillin.utils;

import com.google.gson.JsonObject;

import java.util.Map;

/**
 * MapUtil
 */

public class MapUtil {

    /**
     * Map Value 值 null 替换
     *
     * @param map
     * @return
     */
    public static Map<String, String> getNullReplace(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (StringUtil.isNullOrEmpty(entry.getValue())) {
                map.put(entry.getKey(), "");
            }
        }
        return map;
    }

    public static String transToJsonStr(Map<String, Object> map) {
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            jsonObject.addProperty(entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());
        }

        return jsonObject.toString();
    }
}
