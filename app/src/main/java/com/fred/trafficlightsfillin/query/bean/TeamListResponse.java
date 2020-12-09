package com.fred.trafficlightsfillin.query.bean;

import com.fred.trafficlightsfillin.base.BaseResponse;

import java.util.List;

public class TeamListResponse extends BaseResponse {
    public List<TeamListChannel> data;
    public class TeamListChannel{
        /**
         * createTime : 0
         * createUser :
         * id : 0
         * name :
         * updateTime : 0
         * updateUser :
         */

        public String createTime;
        public String createUser;
        public String id;
        public String name;
        public String updateTime;
        public String updateUser;
        public String source;
        public String desc;

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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
