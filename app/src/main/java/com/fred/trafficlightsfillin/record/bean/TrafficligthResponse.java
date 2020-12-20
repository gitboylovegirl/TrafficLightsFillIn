package com.fred.trafficlightsfillin.record.bean;

import com.fred.trafficlightsfillin.base.BaseResponse;

import java.io.Serializable;
import java.util.List;

public class TrafficligthResponse extends BaseResponse {

    public Data data;
    public class Data implements Serializable {
        /**
         * 总记录数
         */
        public Long totalRows;
        /**
         * 总页数
         */
        public Integer totalPages;
        /**
         * 当前第几页
         */
        public Integer pageNum;
        /**
         * 每页记录数
         */
        public Integer pageSize;
        /**
         * 当前页记录数
         */
        public Integer curPageSize;
        /**
         * 数据列表
         */
        public List<TrafficligthVo> list;
    }


}
