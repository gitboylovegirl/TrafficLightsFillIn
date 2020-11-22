package com.fred.trafficlightsfillin.utils;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonUtil {

    private static Gson gson = null;

    static {
        if (gson == null) {
            gson = new Gson();
        }
    }

    public static String toJsonStr(Object obj) {
        return gson.toJson(obj);
        //return JSON.toJSONString(obj);
    }


    public static <T> T parseObject(String str, Class<T> clazz) {
        return gson.fromJson(str,clazz);
        //return JSON.parseObject(str, clazz);
    }

    public static <T> T parseBean(String str, Type type) {
        return gson.fromJson(str,type);
    }

    public static <T> List<T> parseArray(String json, Class<T> cls) {
        //return JSON.parseArray(str, clazz);
        List<T> list = new ArrayList<T>();
        try {
            JsonArray array = new JsonParser().parse(json).getAsJsonArray();
            for(final JsonElement elem : array){
                list.add(gson.fromJson(elem, cls));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
