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
import com.fred.trafficlightsfillin.utils.RoadDataUtil;
import com.fred.trafficlightsfillin.utils.SharedPreferenceUtils;
import com.fred.trafficlightsfillin.utils.StageDataUtil;
import com.fred.trafficlightsfillin.utils.StatusBarUtils;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends Activity {

    Timer timer;
    private String action;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        StatusBarUtils.setTransparent(this);
        Intent intent = getIntent();
        action = intent.getStringExtra("action");
        getHomeActivity();
    }

    private void getHomeActivity() {
        timer=new Timer();
        TimerTask task=new TimerTask(){
            public void run(){
                freshToken();
            }
        };
        timer.schedule(task, 2000);
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
        Log.e("fred", "Token:"+ SharedPreferenceUtils.getInstance().getToken());
        Log.e("fred", "RefreshToken:"+ SharedPreferenceUtils.getInstance().getrefreshToken());
        if(TextUtils.isEmpty(SharedPreferenceUtils.getInstance().getToken())){
            Intent intent = new Intent();
            intent.setClass(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if(TextUtils.isEmpty(SharedPreferenceUtils.getInstance().getrefreshToken())){
            Intent intent = new Intent();
            intent.setClass(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.REFRESH_TOKEN))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh-token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .getAsync(new ICallback<TokenResponse>() {
                    @Override
                    public void onSuccess(TokenResponse response) {
                        if(response.code == 0){
                            SharedPreferenceUtils.getInstance().setToken(response.getData());
                            Intent intent = new Intent();
                            intent.putExtra("action", action);
                            intent.setClass(WelcomeActivity.this, MainActivity.class);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent();
                            intent.setClass(WelcomeActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {

                    }
                });
    }
}