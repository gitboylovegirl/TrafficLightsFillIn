package com.fred.trafficlightsfillin.intersection.bean;

import java.io.Serializable;
import java.util.List;

public class SubmitBean implements Serializable {

    /**
     * date : 0
     * periodCaseList : [{"end":"","start":"","timeCaseNo":0,"workday":0}]
     * planCaseList : [{"t1":0,"t10":0,"t2":0,"t3":0,"t4":0,"t5":0,"t6":0,"t7":0,"t8":0,"t9":0,"type":0,"workday":0}]
     * remark :
     * taskId : 0
     * timeCaseList : [{"no":0,"t1":0,"t10":0,"t2":0,"t3":0,"t4":0,"t5":0,"t6":0,"t7":0,"t8":0,"t9":0,"workday":0}]
     */

    private Long date;
    private String cause;
    private int taskId;
    private List<PeriodCaseListBean> periodCaseList;
    private List<PlanCaseListBean> planCaseList;
    private List<TimeCaseListBean> timeCaseList;

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getRemark() {
        return cause;
    }

    public void setRemark(String remark) {
        this.cause = remark;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
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
