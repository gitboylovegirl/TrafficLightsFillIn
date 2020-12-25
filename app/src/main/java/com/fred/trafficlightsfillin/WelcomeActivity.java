package com.fred.trafficlightsfillin;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.fred.trafficlightsfillin.base.RequestApi;
import com.fred.trafficlightsfillin.login.LoginActivity;
import com.fred.trafficlightsfillin.login.TokenResponse;
import com.fred.trafficlightsfillin.network.http.ProRequest;
import com.fred.trafficlightsfillin.network.http.response.ICallback;
import com.fred.trafficlightsfillin.utils.SharedPreferenceUtils;
import com.fred.trafficlightsfillin.utils.StageDataUtil;
import com.fred.trafficlightsfillin.utils.StatusBarUtils;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends Activity {

    Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        StatusBarUtils.setTransparent(this);
        getHomeActivity();
    }

    private void getHomeActivity() {
        timer=new Timer();
        TimerTask task=new TimerTask(){
            public void run(){
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                WelcomeActivity.this.finish();
            }
        };
        timer.schedule(task, 2000);
        StageDataUtil.init();
        freshToken();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer!=null){
            timer.cancel();
        }
    }


    /**
     * 刷新token
     */
    private void freshToken() {
        if(TextUtils.isEmpty(SharedPreferenceUtils.getInstance().getToken())){
            return;
        }
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.REFRESH_TOKEN))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .getAsync(new ICallback<TokenResponse>() {
                    @Override
                    public void onSuccess(TokenResponse response) {
                        if(response.code == 401001){
                            Intent intent = new Intent();
                            intent.setClass(WelcomeActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }else{
                            SharedPreferenceUtils.getInstance().setToken(response.getData());
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {

                    }
                });
    }
}