package com.fred.trafficlightsfillin.intersection.bean;

import com.fred.trafficlightsfillin.base.BaseResponse;

import java.util.List;

public class StageResponse extends BaseResponse {

    public List<StageChanel> data;
    public class StageChanel{
        //	"createTime": 0,
        //			"createUser": "",
        //			"desc": "",
        //			"hash": "",
        //			"id": 0,
        //			"image": "",
        //			"no": 0,
        //			"updateTime": 0,
        //			"updateUser": ""
        public String createTime;
        public String createUser;
        public String desc;
        public String hash;
        public String id;
        public String image;
        public String no;
        public String updateTime;
        public String updateUser;


    }
}
