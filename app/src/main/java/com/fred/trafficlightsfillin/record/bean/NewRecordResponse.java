package com.fred.trafficlightsfillin.record.bean;

import com.fred.trafficlightsfillin.base.BaseResponse;

import java.io.Serializable;
import java.util.List;

public class NewRecordResponse extends BaseResponse {

    public Data data;
    public class Data implements Serializable {
        public List<NewRecordChannel> list;
        public String curPageSize;
        public String pageNum;
        public String pageSize;
        public String totalPages;
        public String totalRows;
    }
}
