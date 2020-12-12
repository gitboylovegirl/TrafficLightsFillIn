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

        public static class PeriodCaseListBean {
            /**
             * end :
             * start :
             * timeCaseNo : 0
             * workday : 0
             */

            private String end;
            private String start;
            private String timeCaseNo;
            private String workday;

            public String getEnd() {
                return end;
            }

            public void setEnd(String end) {
                this.end = end;
            }

            public String getStart() {
                return start;
            }

            public void setStart(String start) {
                this.start = start;
            }

            public String getTimeCaseNo() {
                return timeCaseNo;
            }

            public void setTimeCaseNo(String timeCaseNo) {
                this.timeCaseNo = timeCaseNo;
            }

            public String getWorkday() {
                return workday;
            }

            public void setWorkday(String workday) {
                this.workday = workday;
            }
        }

        public static class PlanCaseListBean {
            /**
             * t1 : 0
             * t10 : 0
             * t2 : 0
             * t3 : 0
             * t4 : 0
             * t5 : 0
             * t6 : 0
             * t7 : 0
             * t8 : 0
             * t9 : 0
             * type : 0
             * workday : 0
             */

            private String t1;
            private String t10;
            private String t2;
            private String t3;
            private String t4;
            private String t5;
            private String t6;
            private String t7;
            private String t8;
            private String t9;
            private String type;
            private String workday;

            public String getT1() {
                return t1;
            }

            public void setT1(String t1) {
                this.t1 = t1;
            }

            public String getT10() {
                return t10;
            }

            public void setT10(String t10) {
                this.t10 = t10;
            }

            public String getT2() {
                return t2;
            }

            public void setT2(String t2) {
                this.t2 = t2;
            }

            public String getT3() {
                return t3;
            }

            public void setT3(String t3) {
                this.t3 = t3;
            }

            public String getT4() {
                return t4;
            }

            public void setT4(String t4) {
                this.t4 = t4;
            }

            public String getT5() {
                return t5;
            }

            public void setT5(String t5) {
                this.t5 = t5;
            }

            public String getT6() {
                return t6;
            }

            public void setT6(String t6) {
                this.t6 = t6;
            }

            public String getT7() {
                return t7;
            }

            public void setT7(String t7) {
                this.t7 = t7;
            }

            public String getT8() {
                return t8;
            }

            public void setT8(String t8) {
                this.t8 = t8;
            }

            public String getT9() {
                return t9;
            }

            public void setT9(String t9) {
                this.t9 = t9;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getWorkday() {
                return workday;
            }

            public void setWorkday(String workday) {
                this.workday = workday;
            }
        }

        public static class TimeCaseListBean {
            /**
             * no : 0
             * t1 : 0
             * t10 : 0
             * t2 : 0
             * t3 : 0
             * t4 : 0
             * t5 : 0
             * t6 : 0
             * t7 : 0
             * t8 : 0
             * t9 : 0
             * workday : 0
             */

            private String no;
            private String t1;
            private String t10;
            private String t2;
            private String t3;
            private String t4;
            private String t5;
            private String t6;
            private String t7;
            private String t8;
            private String t9;
            private String workday;

            public String getNo() {
                return no;
            }

            public void setNo(String no) {
                this.no = no;
            }

            public String getT1() {
                return t1;
            }

            public void setT1(String t1) {
                this.t1 = t1;
            }

            public String getT10() {
                return t10;
            }

            public void setT10(String t10) {
                this.t10 = t10;
            }

            public String getT2() {
                return t2;
            }

            public void setT2(String t2) {
                this.t2 = t2;
            }

            public String getT3() {
                return t3;
            }

            public void setT3(String t3) {
                this.t3 = t3;
            }

            public String getT4() {
                return t4;
            }

            public void setT4(String t4) {
                this.t4 = t4;
            }

            public String getT5() {
                return t5;
            }

            public void setT5(String t5) {
                this.t5 = t5;
            }

            public String getT6() {
                return t6;
            }

            public void setT6(String t6) {
                this.t6 = t6;
            }

            public String getT7() {
                return t7;
            }

            public void setT7(String t7) {
                this.t7 = t7;
            }

            public String getT8() {
                return t8;
            }

            public void setT8(String t8) {
                this.t8 = t8;
            }

            public String getT9() {
                return t9;
            }

            public void setT9(String t9) {
                this.t9 = t9;
            }

            public String getWorkday() {
                return workday;
            }

            public void setWorkday(String workday) {
                this.workday = workday;
            }
        }
    }
}
