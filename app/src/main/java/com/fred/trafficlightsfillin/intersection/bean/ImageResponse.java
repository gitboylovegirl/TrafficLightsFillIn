package com.fred.trafficlightsfillin.intersection.bean;

import android.net.Uri;

import com.fred.trafficlightsfillin.base.BaseResponse;

import java.io.Serializable;
import java.util.List;

public class ImageResponse extends BaseResponse implements Serializable {

    public List<ImageBean> data;

    public List<ImageBean> getData() {
        return data;
    }

    public void setData(List<ImageBean> data) {
        this.data = data;
    }

    public static class ImageBean {
        /**
         * createTime : 0
         * createUser :
         * id : 0
         * path :
         * trafficLightId : 0
         * updateTime : 0
         * updateUser :
         */

        public String createTime;
        public String createUser;
        public String id;
        public String path;
        public String trafficLightId;
        public String updateTime;
        public String updateUser;
        public Uri uri;

        public Uri getUri() {
            return uri;
        }

        public void setUri(Uri uri) {
            this.uri = uri;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getCreateUser() {
            return createUser;
        }

        public void setCreateUser(String createUser) {
            this.createUser = createUser;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getTrafficLightId() {
            return trafficLightId;
        }

        public void setTrafficLightId(String trafficLightId) {
            this.trafficLightId = trafficLightId;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getUpdateUser() {
            return updateUser;
        }

        public void setUpdateUser(String updateUser) {
            this.updateUser = updateUser;
        }
    }
}
