package com.fred.trafficlightsfillin.intersection.bean;

import java.io.Serializable;

public class PeriodCaseListBean implements Serializable {
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
