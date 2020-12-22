package com.fred.trafficlightsfillin;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.fred.trafficlightsfillin.login.LoginActivity;
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer!=null){
            timer.cancel();
        }
    }
}