package com.fred.trafficlightsfillin.record.bean;

import com.fred.trafficlightsfillin.base.BaseResponse;

import java.io.Serializable;

public class TaskDetailsChannel extends BaseResponse implements Serializable {
    public TaskDetails data;
    public class TaskDetails{
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
        public String createTime;
        public String desc;
        public String engineerId;
        public String engineerName;
        public String engineerPhone;//手机号
        public String id;
        public String location;
        public String modelNo;
        public String modelType;
        public String remark;
        public String roadPlace;
        public String roadPlaceType;
        public String source;
        public String state;
        public String teamId;
        public String date;
        public String teamName;
        public String trafficLightId;
        public String trafficLightLastUpdateTime;
        public String trafficLightNo;
        public String trafficLightState;
        public String cause;
        public String updateTime;

        @Override
        public String toString() {
            return "TaskDetails{" +
                    "area='" + area + '\'' +
                    ", carNumber='" + carNumber + '\'' +
                    ", createTime='" + createTime + '\'' +
                    ", desc='" + desc + '\'' +
                    ", engineerId='" + engineerId + '\'' +
                    ", engineerName='" + engineerName + '\'' +
                    ", id='" + id + '\'' +
                    ", location='" + location + '\'' +
                    ", modelNo='" + modelNo + '\'' +
                    ", modelType='" + modelType + '\'' +
                    ", remark='" + remark + '\'' +
                    ", roadPlace='" + roadPlace + '\'' +
                    ", roadPlaceType='" + roadPlaceType + '\'' +
                    ", source='" + source + '\'' +
                    ", state='" + state + '\'' +
                    ", teamId='" + teamId + '\'' +
                    ", teamName='" + teamName + '\'' +
                    ", trafficLightId='" + trafficLightId + '\'' +
                    ", trafficLightLastUpdateTime='" + trafficLightLastUpdateTime + '\'' +
                    ", trafficLightNo='" + trafficLightNo + '\'' +
                    ", trafficLightState='" + trafficLightState + '\'' +
                    '}';
        }

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

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getEngineerId() {
            return engineerId;
        }

        public void setEngineerId(String engineerId) {
            this.engineerId = engineerId;
        }

        public String getEngineerName() {
            return engineerName;
        }

        public void setEngineerName(String engineerName) {
            this.engineerName = engineerName;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
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

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
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

        public String getTrafficLightId() {
            return trafficLightId;
        }

        public void setTrafficLightId(String trafficLightId) {
            this.trafficLightId = trafficLightId;
        }

        public String getTrafficLightLastUpdateTime() {
            return trafficLightLastUpdateTime;
        }

        public void setTrafficLightLastUpdateTime(String trafficLightLastUpdateTime) {
            this.trafficLightLastUpdateTime = trafficLightLastUpdateTime;
        }

        public String getTrafficLightNo() {
            return trafficLightNo;
        }

        public void setTrafficLightNo(String trafficLightNo) {
            this.trafficLightNo = trafficLightNo;
        }

        public String getTrafficLightState() {
            return trafficLightState;
        }

        public void setTrafficLightState(String trafficLightState) {
            this.trafficLightState = trafficLightState;
        }

        public String getCause() {
            return cause;
        }

        public void setCause(String cause) {
            this.cause = cause;
        }

        public String getEngineerPhone() {
            return engineerPhone;
        }

        public void setEngineerPhone(String engineerPhone) {
            this.engineerPhone = engineerPhone;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }
}
