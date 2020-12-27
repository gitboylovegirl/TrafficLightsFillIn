package com.fred.trafficlightsfillin.base;

import com.google.gson.annotations.SerializedName;

public class AppVerRespnse extends BaseResponse {

    /**
     * data : {"createTime":0,"createUser":"","desc":"","id":0,"updateTime":0,"updateUser":"","url":"","versionNumber":0,"versionStr":""}
     */

    public DataBean data;
    
    public static class DataBean {
        /**
         * createTime : 0
         * createUser :
         * desc :
         * id : 0
         * updateTime : 0
         * updateUser :
         * url :
         * versionNumber : 0
         * versionStr :
         */

        public String createTime;
        public String createUser;
        @SerializedName("desc")
        public String descX;
        public String id;
        public String updateTime;
        public String updateUser;
        public String url;
        public String versionNumber;
        public String versionStr;

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

        public String getDescX() {
            return descX;
        }

        public void setDescX(String descX) {
            this.descX = descX;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getVersionNumber() {
            return versionNumber;
        }

        public void setVersionNumber(String versionNumber) {
            this.versionNumber = versionNumber;
        }

        public String getVersionStr() {
            return versionStr;
        }

        public void setVersionStr(String versionStr) {
            this.versionStr = versionStr;
        }
    }
}
