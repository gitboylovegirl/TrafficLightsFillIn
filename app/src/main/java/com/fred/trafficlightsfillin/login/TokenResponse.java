package com.fred.trafficlightsfillin.login;

import com.fred.trafficlightsfillin.base.BaseResponse;

public class TokenResponse extends BaseResponse {


    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
