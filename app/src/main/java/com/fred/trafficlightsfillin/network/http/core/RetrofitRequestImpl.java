package com.fred.trafficlightsfillin.network.http.core;

import android.content.Context;

import com.fred.trafficlightsfillin.network.http.ProRequest;
import com.fred.trafficlightsfillin.network.http.response.CallBackResolve;
import com.fred.trafficlightsfillin.network.http.response.ICallback;
import com.fred.trafficlightsfillin.utils.BackgroundThread;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * retrofit请求实现类
 */

public class RetrofitRequestImpl extends AbstractRequest {

    private Context context;
    private ProRequest.RequestBuilder requestBuilder;

    public RetrofitRequestImpl(ProRequest.RequestBuilder requestBuilder) {
        this.requestBuilder = requestBuilder;
        this.context=requestBuilder.mContext;
    }

    @Override
    public void getAsync(ICallback listener) {
        Call<ResponseBody> call;
        if (requestBuilder.params == null || requestBuilder.params.size() == 0) {
            call = getBaseApiService().doGet(requestBuilder.url);
        } else {
            //call = getBaseApiService().doGet(requestBuilder.url, requestBuilder.params);

            JSONObject json =new JSONObject(requestBuilder.params);
            RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
            call = getBaseApiService().doGet(requestBuilder.url, body);

        }
        execute(call, listener,context);
    }

    @Override
    public void getAsyncTwo(ICallback listener) {
        Call<ResponseBody> call;
        if (requestBuilder.params == null || requestBuilder.params.size() == 0) {
            call = getBaseApiService().doGet(requestBuilder.url);
        } else {
            call = getBaseApiService().doGet(requestBuilder.url, requestBuilder.params);
        }
        execute(call, listener,context);
    }

    @Override
    public void postAsync(ICallback listener) {
        Call<ResponseBody> call;
        if (requestBuilder.params == null || requestBuilder.params.size() == 0) {
            call = getBaseApiService().doPost(requestBuilder.url);
        } else {
            //call = getBaseApiService().doPost(requestBuilder.url, requestBuilder.params);

            JSONObject json =new JSONObject(requestBuilder.params);
            RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
            call = getBaseApiService().doPost(requestBuilder.url, body);
        }
        execute(call, listener,context);
    }

    @Override
    public void postBodyAsync(ICallback listener) {
        Call<ResponseBody> call;
        long bodyLength = 0;
        if (requestBuilder.body != null) {
            try {
                bodyLength = requestBuilder.body.contentLength();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestBuilder.bodyMaps != null && requestBuilder.bodyMaps.size() > 0) {
            call = getBaseApiService().uploadFiles(requestBuilder.url, requestBuilder.bodyMaps);
        } else if (requestBuilder.body != null && bodyLength > 0) {
            if (requestBuilder.params != null && requestBuilder.params.size() > 0) {
                call = getBaseApiService().doPost(requestBuilder.url, requestBuilder.params, requestBuilder.body);
            } else {
                call = getBaseApiService().doPost(requestBuilder.url, requestBuilder.body);
            }
        } else {
            call = getBaseApiService().doPost(requestBuilder.url);
        }

        call = getBaseApiService().doPost(requestBuilder.url, requestBuilder.body);

        execute(call, listener,context);
    }

    @Override
    public void uploadFiles(ICallback listener) {
        Call<ResponseBody> call;
        //多个file上传参数封装
        if (requestBuilder.uploadFiles.size() > 1) {
            List<File> uploadFileList = new ArrayList<>();
            for (String filePath : requestBuilder.uploadFiles) {
                File file = new File(filePath);
                if (!file.exists()) {
                    continue;
                }
                uploadFileList.add(file);
            }

            if (uploadFileList.size() <= 0) {
                listener.onResolveFail(ICallback.ERROR_CODE_DEFAULT,
                        "uploadFiles files not exist, uploadFileList size: " + uploadFileList.size());
                return;
            }

            HashMap<String, RequestBody> filesMap = new HashMap<>();
            for (int i = 0; i < uploadFileList.size(); i++) {
                File file = uploadFileList.get(i);
                RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                filesMap.put("file[]\"; filename=\"" + file.getName(), body);
            }

            if (requestBuilder.params != null && requestBuilder.params.size() > 0) {
                call = getBaseApiService().uploadFiles(requestBuilder.url, filesMap ,requestBuilder.params);
            } else {
                call = getBaseApiService().uploadFiles(requestBuilder.url, filesMap);
            }
        } else {
            File file = new File(requestBuilder.uploadFiles.get(0));
            if (!file.exists()) {
                listener.onResolveFail(ICallback.ERROR_CODE_DEFAULT,
                        "uploadFiles file not exist: " + requestBuilder.uploadFiles.get(0));
                return;
            }

            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

            if (requestBuilder.params != null && requestBuilder.params.size() > 0) {
                call = getBaseApiService().uploadFile(requestBuilder.url, part ,requestBuilder.params);
            } else {
                call = getBaseApiService().uploadFile(requestBuilder.url, part);
            }
        }

        execute(call, listener,context);
    }

    @Override
    public void putAsync(ICallback listener) {
        Call<ResponseBody> call;
        if (requestBuilder.params == null || requestBuilder.params.size() == 0) {
            call = getBaseApiService().doPut(requestBuilder.url);
        } else {
            JSONObject json =new JSONObject(requestBuilder.params);
            RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
            call = getBaseApiService().doPut(requestBuilder.url, body);
            //call = getBaseApiService().doPut(requestBuilder.url, requestBuilder.params);
        }
        execute(call, listener,context);
    }

    private BaseApiService getBaseApiService() {
        return new RetrofitBuilder(requestBuilder).buildRetrofitCall(BaseApiService.class);
    }

    private void execute(Call<ResponseBody> call, final ICallback listener, final Context context) {
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                BackgroundThread.post(new Runnable() {
                    @Override
                    public void run() {
                        CallBackResolve.parseSuccess(response, listener,context);
                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.onResolveFail(ICallback.ERROR_CODE_DEFAULT, "onFailure throwable msg == " + t.getMessage());
            }
        });
    }

}
