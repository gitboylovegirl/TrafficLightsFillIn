package com.fred.trafficlightsfillin.login;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fred.trafficlightsfillin.R;
import com.fred.trafficlightsfillin.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangePasswordActivity extends BaseActivity {

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleRight)
    TextView titleRight;
    @BindView(R.id.user_old_password)
    EditText userOldPassword;
    @BindView(R.id.user_new_password)
    EditText userNewPassword;
    @BindView(R.id.user_new_password_two)
    EditText userNewPasswordTwo;
    @BindView(R.id.confirm_button)
    TextView confirmButton;
    @BindView(R.id.cancel)
    TextView cancel;

    @Override
    public void setContentLayout() {
        setContainerView(R.layout.activity_change_password);
        ButterKnife.bind(this);
    }

    @Override
    public void setTitleBarLayout() {

    }

    @Override
    public void initView() {
      confirmButton.setOnClickListener(this::onClickEvent);
      cancel.setOnClickListener(this::onClickEvent);
    }

    @Override
    public void dealLogicBeforeInitView() {

    }

    @Override
    public void dealLogicAfterInitView() {

    }

    @Override
    public void onClickEvent(View view) {
       switch (view.getId()){
           case R.id.confirm_button:

               break;
           case R.id.cancel:
               finish();
               break;
       }
    }
}
