package com.fred.trafficlightsfillin.intersection.bean;

import java.io.Serializable;
import java.util.List;

public class TimingDetailsResponse implements Serializable {

    /**
     * code : 0
     * data : {"loginCode":"","loginNickName":"","loginRealName":"","loginRole":"","loginUserId":0,"loginUsername":"","periodCaseList":[{"end":"","start":"","timeCaseNo":0,"workday":0}],"planCaseList":[{"t1":0,"t10":0,"t2":0,"t3":0,"t4":0,"t5":0,"t6":0,"t7":0,"t8":0,"t9":0,"type":0,"workday":0}],"timeCaseList":[{"no":0,"t1":0,"t10":0,"t2":0,"t3":0,"t4":0,"t5":0,"t6":0,"t7":0,"t8":0,"t9":0,"workday":0}],"trafficLightId":0}
     * msg :
     */

    private int code;
    private DataBean data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
        /**
         * loginCode :
         * loginNickName :
         * loginRealName :
         * loginRole :
         * loginUserId : 0
         * loginUsername :
         * periodCaseList : [{"end":"","start":"","timeCaseNo":0,"workday":0}]
         * planCaseList : [{"t1":0,"t10":0,"t2":0,"t3":0,"t4":0,"t5":0,"t6":0,"t7":0,"t8":0,"t9":0,"type":0,"workday":0}]
         * timeCaseList : [{"no":0,"t1":0,"t10":0,"t2":0,"t3":0,"t4":0,"t5":0,"t6":0,"t7":0,"t8":0,"t9":0,"workday":0}]
         * trafficLightId : 0
         */

        private String loginCode;
        private String loginNickName;
        private String loginRealName;
        private String loginRole;
        private String loginUserId;
        private String loginUsername;
        private String trafficLightId;
        private List<PeriodCaseListBean> periodCaseList;
        private List<PlanCaseListBean> planCaseList;
        private List<TimeCaseListBean> timeCaseList;

        public String getLoginCode() {
            return loginCode;
        }

        public void setLoginCode(String loginCode) {
            this.loginCode = loginCode;
        }

        public String getLoginNickName() {
            return loginNickName;
        }

        public void setLoginNickName(String loginNickName) {
            this.loginNickName = loginNickName;
        }

        public String getLoginRealName() {
            return loginRealName;
        }

        public void setLoginRealName(String loginRealName) {
            this.loginRealName = loginRealName;
        }

        public String getLoginRole() {
            return loginRole;
        }

        public void setLoginRole(String loginRole) {
            this.loginRole = loginRole;
        }

        public String getLoginUserId() {
            return loginUserId;
        }

        public void setLoginUserId(String loginUserId) {
            this.loginUserId = loginUserId;
        }

        public String getLoginUsername() {
            return loginUsername;
        }

        public void setLoginUsername(String loginUsername) {
            this.loginUsername = loginUsername;
        }

        public String getTrafficLightId() {
            return trafficLightId;
        }

        public void setTrafficLightId(String trafficLightId) {
            this.trafficLightId = trafficLightId;
        }

        public List<PeriodCaseListBean> getPeriodCaseList() {
            return periodCaseList;
        }

        public void setPeriodCaseList(List<PeriodCaseListBean> periodCaseList) {
            this.periodCaseList = periodCaseList;
        }

        public List<PlanCaseListBean> getPlanCaseList() {
            return planCaseList;
        }

        public void setPlanCaseList(List<PlanCaseListBean> planCaseList) {
            this.planCaseList = planCaseList;
        }

        public List<TimeCaseListBean> getTimeCaseList() {
            return timeCaseList;
        }

        public void setTimeCaseList(List<TimeCaseListBean> timeCaseList) {
            this.timeCaseList = timeCaseList;
        }
    }
}
