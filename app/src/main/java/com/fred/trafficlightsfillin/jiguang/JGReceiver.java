package com.fred.trafficlightsfillin.jiguang;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.fred.trafficlightsfillin.WelcomeActivity;

import cn.jpush.android.api.JPushInterface;

public class JGReceiver extends BroadcastReceiver {

    public final static String FROMNOTICE = "fromnotice";
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d("fred", "onReceive - " + intent.getAction());

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d("fred", "[MyReceiver] 接收 Registration Id : " + regId);
        }else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d("fred", "收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d("fred", "收到了通知");
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d("fred", "用户点击打开了通知");
            Intent loginIntent = new Intent(context, WelcomeActivity.class);  //自定义打开的界面
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            loginIntent.putExtra("action", FROMNOTICE);
            context.startActivity(loginIntent);
        } else {
            Log.d("fred", "Unhandled intent - " + intent.getAction());
        }
    }
}
