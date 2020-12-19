package com.fred.trafficlightsfillin.record.bean;

import com.fred.trafficlightsfillin.base.BaseResponse;

public class EngigeerResponse extends BaseResponse {

    public EngigeerChannel data;
    /**
     * {
     * 	"code": 0,
     * 	"data": {
     * 		"age": 0,
     * 		"carNumber": "",
     * 		"createTime": 0,
     * 		"createUser": "",
     * 		"flag": 0,
     * 		"id": 0,
     * 		"lat": 0,
     * 		"lon": 0,
     * 		"name": "",
     * 		"password": "",
     * 		"phone": "",
     * 		"profession": "",
     * 		"remark": "",
     * 		"sex": 0,
     * 		"teamId": 0,
     * 		"teamName": "",
     * 		"updateTime": 0,
     * 		"updateUser": ""
     *        },
     * 	"msg": ""
     * }
     */

    public class EngigeerChannel{
        public String age;
        public String carNumber;
        public String id;
        public String lat;
        public String lon;
        public String name;
        public String phone;
        public String remark;
        public String teamId;
        public String teamName;

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getCarNumber() {
            return carNumber;
        }

        public void setCarNumber(String carNumber) {
            this.carNumber = carNumber;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }
    }
}
