package com.fred.trafficlightsfillin.login;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fred.trafficlightsfillin.MainActivity;
import com.fred.trafficlightsfillin.R;
import com.fred.trafficlightsfillin.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.user_name)
    EditText userName;
    @BindView(R.id.user_team)
    EditText userTeam;
    @BindView(R.id.user_phone)
    EditText userPhone;
    @BindView(R.id.user_password)
    EditText userPassword;
    @BindView(R.id.change_password)
    TextView changePassword;
    @BindView(R.id.password_view)
    RelativeLayout passwordView;
    @BindView(R.id.login)
    TextView login;
    @BindView(R.id.cancel)
    TextView cancel;

    @Override
    public void setContentLayout() {
        setContainerView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @Override
    public void initView() {
        login.setOnClickListener(this);
        cancel.setOnClickListener(this::onClick);
        changePassword.setOnClickListener(this::onClick);
    }

    @Override
    public void setTitleBarLayout() {

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
            case R.id.login:
                openActivity(MainActivity.class);
                break;
            case R.id.cancel:

                break;
            case R.id.change_password:
                openActivity(ChangePasswordActivity.class);
                break;
        }
    }
}