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
               if(TextUtils.isEmpty(userNewPassword.getText().toString().trim())||TextUtils.isEmpty(userNewPasswordTwo.getText().toString().trim())||TextUtils.isEmpty(userOldPassword.getText().toString().trim())){
                   ToastUtil.showMsg(ChangePasswordActivity.this,"密码不能为空");
               }else {
                   changeUserPassword(userNewPassword.getText().toString().trim(),userOldPassword.getText().toString().trim(),userNewPasswordTwo.getText().toString().trim());
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
                            Log.e("fred",response.toString());
                            if(response.code==0){
                                finish();
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
