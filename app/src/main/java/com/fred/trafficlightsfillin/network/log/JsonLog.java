package com.fred.trafficlightsfillin.network.log;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * User: chw
 * Date: 2017/8/9
 */

public class JsonLog {
    public static final int JSON_INDENT = 4;

    public static void printJson(String tag, String msg, String headString, RequestLog requestLog) {
        String message;
        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(JSON_INDENT);
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(JSON_INDENT);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }

        printLine(tag, true);
        message = headString + System.getProperty("line.separator") + message;
        String[] lines = message.split(System.getProperty("line.separator"));

        //调试彩蛋
        requestLog.responses = lines;

        for (String line : lines) {
            Log.e(tag, "║ " + line);
        }
        printLine(tag, false);
    }

    public static boolean isEmpty(String line) {
        return TextUtils.isEmpty(line) || line.equals("\n") || line.equals("\t") || TextUtils.isEmpty(line.trim());
    }

    public static void printLine(String tag, boolean isTop) {
        if (isTop) {
            Log.e(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════");
        } else {
            Log.e(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
        }
    }

    public static void printDivider(String tag) {
        Log.e(tag, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

    }

    /**
     * log格式化输出response
     *
     * @param tag
     * @param response
     * @param costTime
     * @return
     */
    public static Response logForResponse(String tag, Response response, long costTime, RequestLog requestLog) {
        try {
            Log.e(tag, "===============Response'log=======begin============================");
            Response.Builder builder = response.newBuilder();
            Response clone = builder.build();
            Log.e(tag, "url : " + clone.request().url());
            Log.e(tag, "code : " + clone.code());
            if (requestLog != null) {
                requestLog.code = clone.code() + "";
            }
            Log.e(tag, "protocol : " + clone.protocol());
            Log.e(tag, "request 耗时 : " + costTime + "ms");
            if (!TextUtils.isEmpty(clone.message())) {
                Log.e(tag, "message : " + clone.message());
            }

            ResponseBody body = clone.body();
            if (body != null) {
                MediaType mediaType = body.contentType();
                if (mediaType != null) {
                    Log.e(tag, "responseBody's contentType : " + mediaType.toString() + " type: " + mediaType.subtype());

                    if (isText(mediaType)) {
                        String resp = body.string();

                        if (isJson(mediaType)) {
                            printJson(tag, resp, "", requestLog);
                        } else {
                            Log.e(tag, "responseBody's content : " + resp);
                        }

                        body = ResponseBody.create(mediaType, resp);
                        return response.newBuilder().body(body).build();
                    } else {
                        Log.e(tag, "responseBody's content : " + " maybe [file part] , too large too print , ignored!");
                    }
                }
            }

            Log.e(tag, "===============Response'log=======end============================");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    /**
     * log格式化输出request
     *
     * @param tag
     * @param request
     */
    public static RequestLog logForRequest(String tag, Request request) {
        RequestLog requestLogBean = new RequestLog();
        try {
            String url = request.url().toString();
            Headers headers = request.headers();

            Log.e(tag, "===============Request'log======begin=============================");
            Log.e(tag, "method : " + request.method());
            Log.e(tag, "url : " + url);
            requestLogBean.url = url;
            if (headers != null && headers.size() > 0) {
                Log.e(tag, "headers : " + headers.toString());
                requestLogBean.headers = headers.toString();
            }
            RequestBody requestBody = request.body();
            if (requestBody != null) {
                MediaType mediaType = requestBody.contentType();
                if (mediaType != null) {
                    Log.e(tag, "requestBody's contentType : " + mediaType.toString() + " type: " + mediaType.subtype());

                    if (isText(mediaType)) {
                        String requestBodyContent = bodyToString(request);
                        requestLogBean.params = requestBodyContent;
                        Log.e(tag, "requestBody's content : " + requestBodyContent);
                    } else {
                        Log.e(tag, "requestBody's content : " + " maybe [file part] , too large too print , ignored!");
                    }
                }
            }
            Log.e(tag, "===============Request'log=======end============================");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return requestLogBean;
    }

    public static boolean isText(MediaType mediaType) {
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        if (mediaType.subtype() != null) {
            return mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("webviewhtml") ||
                    mediaType.subtype().equals("x-www-form-urlencoded");
        }
        return false;
    }

    public static boolean isJson(MediaType mediaType) {
        return mediaType.type() != null && mediaType.subtype().contains("json");
    }

    public static String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "something error when show requestBody.";
        }
    }
}
