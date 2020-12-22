package com.fred.trafficlightsfillin;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.fred.trafficlightsfillin.base.RequestApi;
import com.fred.trafficlightsfillin.network.NetEnv;
import com.fred.trafficlightsfillin.network.configuration.NetSettings;

import cn.jpush.android.api.JPushInterface;

public class SoftApplication extends Application {

    public static SoftApplication instance;
    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        mContext=getApplicationContext();

        initProNetwork();
    }

    /**
     * 初始化framwwork设置参数
     */
    private void initProNetwork() {
        NetSettings netSettings = new NetSettings();
        netSettings.setBaseUrl(RequestApi.BASE_URL);
        netSettings.setDebugEnable(BuildConfig.DEBUG);
        //netSettings.addResponseInterceptor(new CancellationIntercept());
        netSettings.setRequestLog(requestLog -> {
        });

        //netSettings.setCommonHeaders(() -> CommonConfig.instance().getCommonHeaders());

        NetEnv.getInstance().init(this, netSettings);
        MultiDex.install(this);
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

    }
}
