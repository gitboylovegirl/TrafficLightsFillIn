package com.fred.trafficlightsfillin.base;

import java.io.Serializable;

/**
 * author: fred
 * date:   2018/11/5
 * 说明:
 */
public class BaseResponse implements Serializable {
    public int code;
    public int ret;
    public String msg;
    public String desc;
    public int httpStatus;
    public long timestamp;

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", httpStatus=" + httpStatus +
                ", timestamp=" + timestamp +
                '}';
    }
}
