package com.fred.trafficlightsfillin;

import android.app.Application;
import android.content.Context;

public class SoftApplication extends Application {

    public static SoftApplication instance;
    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        mContext=getApplicationContext();
    }
}
