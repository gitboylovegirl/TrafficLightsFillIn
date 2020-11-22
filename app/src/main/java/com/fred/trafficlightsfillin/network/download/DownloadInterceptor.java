package com.fred.trafficlightsfillin.network.download;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class DownloadInterceptor implements Interceptor {

    private TransformProgressListener progressListener;
    private DownloadInfo downloadInfo;

    public DownloadInterceptor() {
    }

    public DownloadInterceptor(DownloadInfo downloadInfo, TransformProgressListener progressListener) {
        this.progressListener = progressListener;
        this.downloadInfo = downloadInfo;
    }

    public void setDownloadInfo(DownloadInfo downloadInfo) {
        this.downloadInfo = downloadInfo;
    }

    public void setProgressListener(TransformProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder().addHeader("RANGE", "bytes=" + downloadInfo.getReadLength() + "-").build();
        Response originalResponse = chain.proceed(request);
        DownloadResponseBody body = new DownloadResponseBody(originalResponse.body(), progressListener);
        Response response = originalResponse.newBuilder().body(body).build();
        return response;
    }

    public static DownloadInterceptor create(DownloadInfo downloadInfo, TransformProgressListener progressListener) {
        return new DownloadInterceptor(downloadInfo, progressListener);
    }

}
