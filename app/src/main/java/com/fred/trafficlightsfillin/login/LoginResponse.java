package com.fred.trafficlightsfillin.login;

import com.fred.trafficlightsfillin.base.BaseResponse;

import java.io.Serializable;

/**
 * 	"accessToken": "",
 * 		"age": 0,
 * 		"carNumber": "",
 * 		"id": "",
 * 		"name": "",
 * 		"phone": "",
 * 		"refreshToken": "",
 * 		"remark": "",
 * 		"sex": 0,
 * 		"teamId": 0,
 * 		"teamName": ""
 */
public class LoginResponse extends BaseResponse {
    public LoginBean data;
    public class LoginBean implements Serializable {
        public String accessToken;
        public String age;
        public String carNumber;
        public String id;
        public String name;
        public String phone;
        public String refreshToken;
        public String remark;
        public String sex;
        public String teamId;
        public String teamName;

        @Override
        public String toString() {
            return "LoginBean{" +
                    "accessToken='" + accessToken + '\'' +
                    ", age='" + age + '\'' +
                    ", carNumber='" + carNumber + '\'' +
                    ", id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", phone='" + phone + '\'' +
                    ", refreshToken='" + refreshToken + '\'' +
                    ", remark='" + remark + '\'' +
                    ", sex='" + sex + '\'' +
                    ", teamId='" + teamId + '\'' +
                    ", teamName='" + teamName + '\'' +
                    '}';
        }
    }
}
