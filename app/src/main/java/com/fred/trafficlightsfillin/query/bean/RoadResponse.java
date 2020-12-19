package com.fred.trafficlightsfillin.query.bean;

import com.fred.trafficlightsfillin.base.BaseResponse;
import java.io.Serializable;
import java.util.List;

public class RoadResponse extends BaseResponse {
    public List<RoadChannel> data;
    public class RoadChannel implements Serializable {
        /**
         * area :
         * id : 0
         * lastSelectEngineerId : 0
         * lastSelectTeamId : 0
         * modelNo :
         * modelType :
         * no :
         * peishiNo :
         * peishiRoadPlace :
         * roadPlace :
         * roadPlaceType
         */

        private String area;
        private int id;
        private int lastSelectEngineerId;
        private int lastSelectTeamId;
        private String modelNo;
        private String modelType;
        private String no;
        private String peishiNo;
        private String peishiRoadPlace;
        private String roadPlace;
        private String roadPlaceType;

        public String getRoadPlaceType() {
            return roadPlaceType;
        }

        public void setRoadPlaceType(String roadPlaceType) {
            this.roadPlaceType = roadPlaceType;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getLastSelectEngineerId() {
            return lastSelectEngineerId;
        }

        public void setLastSelectEngineerId(int lastSelectEngineerId) {
            this.lastSelectEngineerId = lastSelectEngineerId;
        }

        public int getLastSelectTeamId() {
            return lastSelectTeamId;
        }

        public void setLastSelectTeamId(int lastSelectTeamId) {
            this.lastSelectTeamId = lastSelectTeamId;
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

        public String getNo() {
            return no;
        }

        public void setNo(String no) {
            this.no = no;
        }

        public String getPeishiNo() {
            return peishiNo;
        }

        public void setPeishiNo(String peishiNo) {
            this.peishiNo = peishiNo;
        }

        public String getPeishiRoadPlace() {
            return peishiRoadPlace;
        }

        public void setPeishiRoadPlace(String peishiRoadPlace) {
            this.peishiRoadPlace = peishiRoadPlace;
        }

        public String getRoadPlace() {
            return roadPlace;
        }

        public void setRoadPlace(String roadPlace) {
            this.roadPlace = roadPlace;
        }
    }
}
