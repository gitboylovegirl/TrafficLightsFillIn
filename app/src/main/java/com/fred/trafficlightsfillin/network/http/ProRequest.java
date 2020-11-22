package com.fred.trafficlightsfillin.network.http;

import android.content.Context;

import com.fred.trafficlightsfillin.network.download.DownloadFileListener;
import com.fred.trafficlightsfillin.network.download.DownloadInfo;
import com.fred.trafficlightsfillin.network.download.DownloadManager;
import com.fred.trafficlightsfillin.network.http.core.RetrofitRequestImpl;
import com.fred.trafficlightsfillin.utils.MapUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 * User: chw
 * Date: 2017/7/26
 * 公共请求入口类
 */

public class ProRequest {
    public static RequestBuilder get() {
        return new RequestBuilder();
    }

    public static void download(String url, String savePath, DownloadFileListener listener) {
        DownloadInfo info = new DownloadInfo(url, savePath);
        info.setState(DownloadInfo.START);
        info.setListener(listener);
        DownloadManager downloadManager = DownloadManager.getInstance();
        downloadManager.startDown(info);
    }

    public static void download(DownloadInfo info) {
        DownloadManager downloadManager = DownloadManager.getInstance();
        downloadManager.startDown(info);
    }

    public static DownloadManager getDownloadManager() {
        return DownloadManager.getInstance();
    }

    public static class RequestBuilder {

        public String url;
        public Context mContext;

        public int retry;

        public long readTimeOut;
        public long writeTimeOut;
        public long connTimeOut;

        public RequestBody body;

        public Converter.Factory factory;

        public Map<String, String> params = new HashMap<>();
        public Map<String, String> headers = new HashMap<>();
        public Map<String, RequestBody> bodyMaps = new HashMap<>();

        public List<String> uploadFiles = new ArrayList<>();

        public RequestBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public RequestBuilder setContext(Context context){
            this.mContext = context;
            return this;
        }
        public RequestBuilder setRetry(int count) {
            this.retry = count;
            return this;
        }

        public RequestBuilder addHeader(String key, String value) {
            if (key != null && value != null) {
                headers.put(key, value);
            }
            return this;
        }

        public RequestBuilder addParam(String key, String value) {
            if (key != null) {
                if (value == null) {
                    value = "";
                }
                params.put(key, value);
            }
            return this;
        }

        public RequestBuilder addParams(Map<String, String> map) {
            if (map != null) {
                params = MapUtil.getNullReplace(map);
            }
            return this;
        }

        public RequestBuilder addUploadFiles(List<String> files) {
            uploadFiles.addAll(files);
            return this;
        }

        public RequestBuilder setReadTimeOut(long readTimeOut) {
            this.readTimeOut = readTimeOut;
            return this;
        }

        public RequestBuilder setWriteTimeOut(long writeTimeOut) {
            this.writeTimeOut = writeTimeOut;
            return this;
        }

        public RequestBuilder setConnTimeOut(long connTimeOut) {
            this.connTimeOut = connTimeOut;
            return this;
        }

        public RequestBuilder setConvertFactory(Converter.Factory factory) {
            this.factory = factory;
            return this;
        }

        public RequestBuilder setBody(RequestBody body) {
            this.body = body;
            return this;
        }

        public RequestBuilder setBody(String key, RequestBody body) {
            bodyMaps.put(key, body);
            return this;
        }

        public RetrofitRequestImpl build() {
            return new RetrofitRequestImpl(this);
        }

    }
}
