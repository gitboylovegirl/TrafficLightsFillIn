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

        public int createTime;
        public String createUser;
        public int id;
        public String name;
        public int updateTime;
        public String updateUser;

        public int getCreateTime() {
            return createTime;
        }

        public void setCreateTime(int createTime) {
            this.createTime = createTime;
        }

        public String getCreateUser() {
            return createUser;
        }

        public void setCreateUser(String createUser) {
            this.createUser = createUser;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(int updateTime) {
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
