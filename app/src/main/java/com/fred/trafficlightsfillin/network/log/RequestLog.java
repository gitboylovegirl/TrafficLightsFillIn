package com.fred.trafficlightsfillin.network.log;

import java.io.Serializable;
import java.util.Arrays;

public class RequestLog implements Serializable {
    public String url;
    public String headers;
    public String code;
    public String params;
    public String[] responses;

    @Override
    public String toString() {
        return "DebugLogBean{" +
                "url='" + url + '\'' +
                ", headers='" + headers + '\'' +
                ", code='" + code + '\'' +
                ", params='" + params + '\'' +
                ", responses=" + Arrays.toString(responses) +
                '}';
    }
}
