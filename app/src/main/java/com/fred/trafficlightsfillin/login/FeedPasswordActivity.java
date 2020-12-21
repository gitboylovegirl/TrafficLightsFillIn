package com.fred.trafficlightsfillin.login;

import android.os.CountDownTimer;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fred.trafficlightsfillin.R;
import com.fred.trafficlightsfillin.base.BaseActivity;
import com.fred.trafficlightsfillin.base.BaseResponse;
import com.fred.trafficlightsfillin.base.RequestApi;
import com.fred.trafficlightsfillin.network.http.ProRequest;
import com.fred.trafficlightsfillin.network.http.response.ICallback;
import com.fred.trafficlightsfillin.utils.CountDownTimerUtils;
import com.fred.trafficlightsfillin.utils.SharedPreferenceUtils;
import com.fred.trafficlightsfillin.utils.ToastUtil;
import com.fred.trafficlightsfillin.utils.ValidatorUtil;

import java.util.logging.Handler;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedPasswordActivity extends BaseActivity {

    @BindView(R.id.feed_phone)
    EditText feedPhone;
    @BindView(R.id.get_code_btn)
    Button getCodeBtn;
    @BindView(R.id.phone_code)
    EditText phoneCode;
    @BindView(R.id.feed_new_password)
    EditText feedNewPassword;
    @BindView(R.id.feed_re_new_password)
    EditText feedReNewPassword;
    @BindView(R.id.feed_password_confirm_button)
    TextView confirmButton;
    @BindView(R.id.feed_password_cancel)
    TextView cancel;

    @Override
    public void setContentLayout() {
        setContainerView(R.layout.activity_feed_password);
        ButterKnife.bind(this);
    }

    @Override
    public void setTitleBarLayout() {

    }

    @Override
    public void initView() {
        confirmButton.setOnClickListener(this::onClickEvent);
        cancel.setOnClickListener(this::onClickEvent);
        getCodeBtn.setOnClickListener(this::onClickEvent);

        feedPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){//失去焦点
                    String content = feedPhone.getText() == null ? "" : feedPhone.getText().toString().trim();
                    if(!ValidatorUtil.iseMobile(content)){
                        ToastUtil.showMsg(FeedPasswordActivity.this,"请输入正确的手机号");
                    }
                }
            }
        });

        feedNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){//失去焦点
                    String content = feedNewPassword.getText() == null ? "" : feedNewPassword.getText().toString().trim();
                    if(!ValidatorUtil.checkPwd(content)){
                        ToastUtil.showMsg(FeedPasswordActivity.this,"新密码格式不对请重新输入");
                        feedNewPassword.setText("");
                    }
                }
            }
        });

        feedReNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){//失去焦点

                    String newPwd= feedNewPassword.getText() == null ? "" : feedNewPassword.getText().toString().trim();
                    String newPwdTwo = feedReNewPassword.getText() == null ? "" : feedReNewPassword.getText().toString().trim();
                    if(!newPwd.equals(newPwdTwo)){
                        ToastUtil.showMsg(FeedPasswordActivity.this,"两次设置的密码不一致");
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
           case R.id.feed_password_confirm_button:
               String newPwd= feedNewPassword.getText() == null ? "" : feedNewPassword.getText().toString().trim();
               String newPwdTwo = feedReNewPassword.getText() == null ? "" : feedReNewPassword.getText().toString().trim();
               String phone = feedPhone.getText() == null ? "" : feedPhone.getText().toString().trim();
               String code = phoneCode.getText() == null ? "" : phoneCode.getText().toString().trim();
               if(TextUtils.isEmpty(phone)){
                   ToastUtil.showMsg(FeedPasswordActivity.this,"手机号不能为空");
                   return;
               }
               if(!ValidatorUtil.iseMobile(phone)){
                   ToastUtil.showMsg(FeedPasswordActivity.this,"请输入正确的手机号");
                   return;
               }
               if(TextUtils.isEmpty(code)){
                   ToastUtil.showMsg(FeedPasswordActivity.this,"验证码不能为空");
                   return;
               }
               if(TextUtils.isEmpty(newPwd)||TextUtils.isEmpty(newPwdTwo)){
                   ToastUtil.showMsg(FeedPasswordActivity.this,"密码不能为空");
                   return;
               }
               if(!newPwd.equals(newPwdTwo)){
                   ToastUtil.showMsg(FeedPasswordActivity.this,"两次密码输入不一致");
                   return;
               }
               changeUserPassword(newPwd,phone,code);
               break;
           case R.id.feed_password_cancel:
               finish();
               break;
           case R.id.get_code_btn:
               sendSmsCode();
               CountDownTimerUtils countDownTimerUtils = new CountDownTimerUtils(getCodeBtn,120000, 1000);
               countDownTimerUtils.start();
               break;
       }
    }

    private void sendSmsCode(){
        String phone = feedPhone.getText() == null ? "" : feedPhone.getText().toString().trim();
        if(TextUtils.isEmpty(phone)){
            ToastUtil.showMsg(FeedPasswordActivity.this,"手机号不能为空");
            return;
        }
        if(!ValidatorUtil.iseMobile(phone)){
            ToastUtil.showMsg(FeedPasswordActivity.this,"请输入正确的手机号");
            return;
        }
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.SEND_SMS_CODE+phone))
                .build()
                .getAsync(new ICallback<SendSmsCodeResponse>() {
                    @Override
                    public void onSuccess(SendSmsCodeResponse response) {
                        ToastUtil.showMsg(FeedPasswordActivity.this,response.msg);
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {

                    }
                });
    }

    /**
     * 新密码  手机号
     * @param newPwd
     * @param phone
     */
    private void changeUserPassword(String newPwd,String phone,String code){
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.FEED_PASSWORD))
                .addParam("phone",phone)
                .addParam("code",code)
                .addParam("password",newPwd)
                .build()
                .postAsync(new ICallback<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        if(response.code==0){
                            finish();
                            openActivity(LoginActivity.class);
                        }
                        ToastUtil.showMsg(FeedPasswordActivity.this,response.msg);
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {

                    }
                });
    }
}
