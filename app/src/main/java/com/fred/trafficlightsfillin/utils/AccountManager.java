package com.fred.trafficlightsfillin.utils;

import com.fred.trafficlightsfillin.login.LoginResponse;

public class AccountManager {

    private static AccountManager mInstance;

    private LoginResponse loginResponse;
    public static AccountManager getInstance() {
        if (mInstance == null) {
            mInstance = new AccountManager();
        }

        return mInstance;
    }

    public LoginResponse getLoginResponse() {
        return loginResponse;
    }

    public void setLoginResponse(LoginResponse loginResponse) {
        this.loginResponse = loginResponse;
    }
}
