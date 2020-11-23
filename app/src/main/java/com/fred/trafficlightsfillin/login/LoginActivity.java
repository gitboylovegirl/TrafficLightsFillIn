package com.fred.trafficlightsfillin.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fred.trafficlightsfillin.MainActivity;
import com.fred.trafficlightsfillin.R;
import com.fred.trafficlightsfillin.base.BaseActivity;
import com.fred.trafficlightsfillin.base.RequestApi;
import com.fred.trafficlightsfillin.network.http.ProRequest;
import com.fred.trafficlightsfillin.utils.ToastUtil;

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
        login.setOnClickListener(this::onClickEvent);
        cancel.setOnClickListener(this::onClickEvent);
        changePassword.setOnClickListener(this::onClickEvent);
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
                //退出系统
                System.exit(0);
                break;
            case R.id.change_password:
                openActivity(ChangePasswordActivity.class);
                break;
        }
    }

    /**
     * login
     * @param name
     * @param team
     * @param phone
     * @param password
     */
    private void goToLogin(String name,String team,String phone,String password){
        if(TextUtils.isEmpty(name)){
            ToastUtil.showMsg(LoginActivity.this,getString(R.string.name_not_null));
            return;
        }
        if(TextUtils.isEmpty(team)){
            ToastUtil.showMsg(LoginActivity.this,getString(R.string.team_not_null));
            return;
        }
        if(TextUtils.isEmpty(phone)){
            ToastUtil.showMsg(LoginActivity.this,getString(R.string.phone_not_null));
            return;
        }
        if(TextUtils.isEmpty(password)){
            ToastUtil.showMsg(LoginActivity.this,getString(R.string.password_not_null));
            return;
        }

        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.))

                .build()
                .getAsync(new ICallback<HomeListResponse>() {
                    @Override
                    public void onSuccess(HomeListResponse response) {

                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {

                    }
                });
    }
}