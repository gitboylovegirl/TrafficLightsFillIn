package com.fred.trafficlightsfillin.login;

import com.fred.trafficlightsfillin.base.BaseResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LocationResponse extends BaseResponse {

    public List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * area : 
         * carNumber : 
         * createTime : 0
         * desc : 
         * engineerId : 0
         * engineerName : 
         * id : 0
         * location : 
         * modelNo : 
         * modelType : 
         * remark : 
         * roadPlace : 
         * roadPlaceType : 
         * source : 
         * state : 0
         * teamId : 0
         * teamName : 
         * trafficLightId : 0
         * trafficLightLastUpdateTime : 0
         * trafficLightNo : 
         * trafficLightState : 0
         */

        public String area;
        public String carNumber;
        public int createTime;
        @SerializedName("desc")
        public String descX;
        public int engineerId;
        public String engineerName;
        public int id;
        public String location;
        public String modelNo;
        public String modelType;
        public String remark;
        public String roadPlace;
        public String roadPlaceType;
        public String source;
        public int state;
        public int teamId;
        public String teamName;
        public int trafficLightId;
        public int trafficLightLastUpdateTime;
        public String trafficLightNo;
        public int trafficLightState;

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getCarNumber() {
            return carNumber;
        }

        public void setCarNumber(String carNumber) {
            this.carNumber = carNumber;
        }

        public int getCreateTime() {
            return createTime;
        }

        public void setCreateTime(int createTime) {
            this.createTime = createTime;
        }

        public String getDescX() {
            return descX;
        }

        public void setDescX(String descX) {
            this.descX = descX;
        }

        public int getEngineerId() {
            return engineerId;
        }

        public void setEngineerId(int engineerId) {
            this.engineerId = engineerId;
        }

        public String getEngineerName() {
            return engineerName;
        }

        public void setEngineerName(String engineerName) {
            this.engineerName = engineerName;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getModelNo() {
            return modelNo;
        }

        public void setModelNo(String modelNo) {
            this.modelNo = modelNo;
        }

        public String getModelType() {
            return modelType;
        }

        public void setModelType(String modelType) {
            this.modelType = modelType;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getRoadPlace() {
            return roadPlace;
        }

        public void setRoadPlace(String roadPlace) {
            this.roadPlace = roadPlace;
        }

        public String getRoadPlaceType() {
            return roadPlaceType;
        }

        public void setRoadPlaceType(String roadPlaceType) {
            this.roadPlaceType = roadPlaceType;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public int getTeamId() {
            return teamId;
        }

        public void setTeamId(int teamId) {
            this.teamId = teamId;
        }

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }

        public int getTrafficLightId() {
            return trafficLightId;
        }

        public void setTrafficLightId(int trafficLightId) {
            this.trafficLightId = trafficLightId;
        }

        public int getTrafficLightLastUpdateTime() {
            return trafficLightLastUpdateTime;
        }

        public void setTrafficLightLastUpdateTime(int trafficLightLastUpdateTime) {
            this.trafficLightLastUpdateTime = trafficLightLastUpdateTime;
        }

        public String getTrafficLightNo() {
            return trafficLightNo;
        }

        public void setTrafficLightNo(String trafficLightNo) {
            this.trafficLightNo = trafficLightNo;
        }

        public int getTrafficLightState() {
            return trafficLightState;
        }

        public void setTrafficLightState(int trafficLightState) {
            this.trafficLightState = trafficLightState;
        }
    }
}
