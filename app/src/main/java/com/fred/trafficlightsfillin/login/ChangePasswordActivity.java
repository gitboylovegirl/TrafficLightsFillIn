package com.fred.trafficlightsfillin.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fred.trafficlightsfillin.MainActivity;
import com.fred.trafficlightsfillin.R;
import com.fred.trafficlightsfillin.base.BaseActivity;
import com.fred.trafficlightsfillin.base.RequestApi;
import com.fred.trafficlightsfillin.network.http.ProRequest;
import com.fred.trafficlightsfillin.network.http.response.ICallback;
import com.fred.trafficlightsfillin.utils.AccountManager;
import com.fred.trafficlightsfillin.utils.SharedPreferenceUtils;
import com.fred.trafficlightsfillin.utils.ToastUtil;
import com.fred.trafficlightsfillin.utils.ValidatorUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        userOldPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){//失去焦点
                    String content = userOldPassword.getText() == null ? "" : userOldPassword.getText().toString().trim();
                    if(!ValidatorUtil.checkPwd(content)){
                        ToastUtil.showMsg(ChangePasswordActivity.this,"原密码格式不对请重新输入");
                        userOldPassword.setText("");
                    }
                }
            }
        });


        userNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){//失去焦点
                    String content = userNewPassword.getText() == null ? "" : userNewPassword.getText().toString().trim();
                    if(!ValidatorUtil.checkPwd(content)){
                        ToastUtil.showMsg(ChangePasswordActivity.this,"新密码格式不对请重新输入");
                        userNewPassword.setText("");
                    }
                }
            }
        });

        userNewPasswordTwo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){//失去焦点

                    String newPwd= userNewPassword.getText() == null ? "" : userNewPassword.getText().toString().trim();
                    String newPwdTwo = userNewPasswordTwo.getText() == null ? "" : userNewPasswordTwo.getText().toString().trim();
                    if(!newPwd.equals(newPwdTwo)){
                        ToastUtil.showMsg(ChangePasswordActivity.this,"两次设置的密码不一致");
                    }
                }
            }
        });
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
               String oldPwd = userOldPassword.getText() == null ? "" : userOldPassword.getText().toString().trim();
               String newPwd= userNewPassword.getText() == null ? "" : userNewPassword.getText().toString().trim();
               String newPwdTwo = userNewPasswordTwo.getText() == null ? "" : userNewPasswordTwo.getText().toString().trim();
               if(TextUtils.isEmpty(oldPwd)||TextUtils.isEmpty(newPwd)||TextUtils.isEmpty(newPwdTwo)){
                   ToastUtil.showMsg(ChangePasswordActivity.this,"密码不能为空");
               }else {
                   changeUserPassword(newPwd,oldPwd,newPwdTwo);
               }
               break;
           case R.id.cancel:
               finish();
               break;
       }
    }

    /**
     * 新密码  旧密码
     * @param newPwd
     * @param oldPwd
     */
    private void changeUserPassword(String newPwd,String oldPwd,String newPwdTwo){
        if (newPwd.equals(newPwdTwo)){
            ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.CHANGE_PASSWORD))
                    .addHeader("authorization",SharedPreferenceUtils.getInstance().getToken())
                    .addHeader("refresh_token",SharedPreferenceUtils.getInstance().getrefreshToken())
                    .addParam("newPwd",newPwd)
                    .addParam("oldPwd",oldPwd)
                    .build()
                    .putAsync(new ICallback<LoginResponse>() {
                        @Override
                        public void onSuccess(LoginResponse response) {
                            if(response.code==0){
                                finish();
                                openActivity(LoginActivity.class);
                            }
                            ToastUtil.showMsg(ChangePasswordActivity.this,response.msg);
                        }

                        @Override
                        public void onFail(int errorCode, String errorMsg) {

                        }
                    });
        }else {
            ToastUtil.showMsg(ChangePasswordActivity.this,"两次设置的密码不一致");
        }
    }
}
