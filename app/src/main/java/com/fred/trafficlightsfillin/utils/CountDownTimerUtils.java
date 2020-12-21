package com.fred.trafficlightsfillin.utils;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.Button;

import com.fred.trafficlightsfillin.R;

public class CountDownTimerUtils extends CountDownTimer {

    private Button mTextView;

    public CountDownTimerUtils(Button textView, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.mTextView = textView;
    }


    @Override
    public void onTick(long l) {
        mTextView.setClickable(false); //设置不可点击
        mTextView.setText(l / 1000 + "秒");  //设置倒计时时间
        mTextView.setBackgroundColor(Color.parseColor("#cccccc")); //设置按钮为灰色，这时是不能点击的
        mTextView.setTextColor(Color.RED);
    }

    @Override
    public void onFinish() {
        mTextView.setText("重新获取");
        mTextView.setClickable(true);//重新获得点击
        mTextView.setBackgroundResource(R.drawable.button_bc2);  //还原背景色
    }
}
