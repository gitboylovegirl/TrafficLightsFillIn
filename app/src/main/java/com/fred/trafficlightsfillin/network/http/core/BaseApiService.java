package com.fred.trafficlightsfillin.network.http.core;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * User: chw
 * Date: 2017/8/22
 */

public interface BaseApiService {

    /**
     * *********************************************通用请求方式**********************************************
     */

    @GET()
    Call<ResponseBody> doGet(@Url String url);

    @GET()
    Call<ResponseBody> doGet(@Url String url, @QueryMap Map<String, String> maps);

    @GET()
    Call<ResponseBody> doGet(@Url String url, @Body RequestBody body);

    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST()
    Call<ResponseBody> doPost(@Url String url);

    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @FormUrlEncoded
    @POST()
    Call<ResponseBody> doPost(@Url String url, @FieldMap Map<String, String> map);

    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST()
    Call<ResponseBody> doPost(@Url String url, @Body RequestBody body);

    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST()
    Call<ResponseBody> doPost(@Url String url, @QueryMap Map<String, String> maps, @Body RequestBody body);

    @Multipart
    @POST
    Call<ResponseBody> uploadFiles(@Url String url, @PartMap() Map<String, RequestBody> maps);

    @Multipart
    @POST
    Call<ResponseBody> uploadFiles(@Url String url, @PartMap() Map<String, RequestBody> maps, @QueryMap Map<String, String> map);

    @Multipart
    @POST
    Call<ResponseBody> uploadFile(@Url String url, @Part MultipartBody.Part file);
    @Multipart
    @POST
    Call<ResponseBody> uploadFile(@Url String url, @Part MultipartBody.Part file, @QueryMap Map<String, String> map);
    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String fileUrl);

    @PUT
    Call<ResponseBody> doPut(@Url String fileUrl);

    @PUT
    Call<ResponseBody> doPut(@Url String fileUrl, @QueryMap Map<String, String> map);

    @PUT
    Call<ResponseBody> doPut(@Url String fileUrl, @Body RequestBody body);
}
