package com.fred.trafficlightsfillin.intersection.bean;

import com.fred.trafficlightsfillin.base.BaseResponse;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TaskResultResponse extends BaseResponse implements Serializable {

    /**
     * data : {"area":"","carNumber":"","cause":"","createTime":0,"desc":"","engineerId":0,"engineerName":"","id":0,"location":"","modelNo":"","modelType":"","remark":"","roadPlace":"","roadPlaceType":"","source":"","state":0,"teamId":0,"teamName":"","trafficLightId":0,"trafficLightLastUpdateTime":0,"trafficLightNo":"","trafficLightState":0,"updateTime":0}
     */

    private TaskResultBean data;

    public TaskResultBean getData() {
        return data;
    }

    public void setData(TaskResultBean data) {
        this.data = data;
    }

    public static class TaskResultBean {
        /**
         * area :
         * carNumber :
         * cause :
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
         * updateTime : 0
         */

        private String area;
        private String carNumber;
        private String cause;
        private int createTime;
        @SerializedName("desc")
        private String descX;
        private int engineerId;
        private String engineerName;
        private int id;
        private String location;
        private String modelNo;
        private String modelType;
        private String remark;
        private String roadPlace;
        private String roadPlaceType;
        private String source;
        private int state;
        private int teamId;
        private String teamName;
        private int trafficLightId;
        private int trafficLightLastUpdateTime;
        private String trafficLightNo;
        private int trafficLightState;
        private int updateTime;

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

        public String getCause() {
            return cause;
        }

        public void setCause(String cause) {
            this.cause = cause;
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

        public int getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(int updateTime) {
            this.updateTime = updateTime;
        }
    }
}
