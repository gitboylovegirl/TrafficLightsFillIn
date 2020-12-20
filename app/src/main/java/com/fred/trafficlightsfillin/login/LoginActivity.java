package com.fred.trafficlightsfillin.login;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fred.trafficlightsfillin.MainActivity;
import com.fred.trafficlightsfillin.R;
import com.fred.trafficlightsfillin.base.BaseActivity;
import com.fred.trafficlightsfillin.base.RequestApi;
import com.fred.trafficlightsfillin.network.http.ProRequest;
import com.fred.trafficlightsfillin.network.http.response.ICallback;
import com.fred.trafficlightsfillin.utils.AccountManager;
import com.fred.trafficlightsfillin.utils.SharedPreferenceUtils;
import com.fred.trafficlightsfillin.utils.ToastUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

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
    @BindView(R.id.forget_password)
    TextView forgetPassowrd;
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
        forgetPassowrd.setOnClickListener(this::onClickEvent);
    }

    @Override
    public void setTitleBarLayout() {

    }

    @Override
    public void dealLogicBeforeInitView() {
        if(SharedPreferenceUtils.getInstance().getToken()!=null&&SharedPreferenceUtils.getInstance().getToken()!=""){
            openActivity(MainActivity.class);
            finish();
        }
    }

    @Override
    public void dealLogicAfterInitView() {
        if(ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){//未开启定位权限
            //开启定位权限,200是标识码
            ActivityCompat.requestPermissions(LoginActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},200);
        }

        if(!TextUtils.isEmpty(SharedPreferenceUtils.getInstance().getName())){
            userName.setText(SharedPreferenceUtils.getInstance().getName());
        }
        if(!TextUtils.isEmpty(SharedPreferenceUtils.getInstance().getPhone())){
            userPhone.setText(SharedPreferenceUtils.getInstance().getPhone());
        }
        if(!TextUtils.isEmpty(SharedPreferenceUtils.getInstance().getTeamName())){
            userTeam.setText(SharedPreferenceUtils.getInstance().getTeamName());
        }
    }

    @Override
    public void onClickEvent(View view) {
        switch (view.getId()){
            case R.id.login:
                goToLogin(userName.getText().toString().trim(),userTeam.getText().toString().trim(),userPhone.getText().toString().trim(),userPassword.getText().toString().trim());
                //openActivity(MainActivity.class);
                break;
            case R.id.cancel:
                //退出系统
                System.exit(0);
                break;
            case R.id.forget_password:
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

        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.LOGIN))
                .addParam("password",password)
                .addParam("name",name)
                .addParam("phone",phone)
                .addParam("tname",team)
                .build()
                .postAsync(new ICallback<LoginResponse>() {
                    @Override
                    public void onSuccess(LoginResponse response) {
                        Log.e("fred",response.toString());
                        if(response.code==0){
                            SharedPreferenceUtils.getInstance().setToken(response.data.accessToken);
                            SharedPreferenceUtils.getInstance().setrefreshToken(response.data.refreshToken);
                            SharedPreferenceUtils.getInstance().setAge(response.data.age);
                            SharedPreferenceUtils.getInstance().setCarNumber(response.data.carNumber);
                            SharedPreferenceUtils.getInstance().setId(response.data.id);
                            SharedPreferenceUtils.getInstance().setName(response.data.name);
                            SharedPreferenceUtils.getInstance().setPhone(response.data.phone);
                            SharedPreferenceUtils.getInstance().setRemark(response.data.remark);
                            SharedPreferenceUtils.getInstance().setSex(response.data.sex);
                            SharedPreferenceUtils.getInstance().setTeamId(response.data.teamId);
                            SharedPreferenceUtils.getInstance().setTeamName(response.data.teamName);

                            LoginResponse.LoginBean data = response.data;
                            AccountManager.getInstance().setLoginResponse(response);
                            openActivity(MainActivity.class);
                            finish();
                        }else {
                            ToastUtil.showMsg(LoginActivity.this,response.msg);
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {

                    }
                });
    }

    private static String sHA1(Context context){
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result= hexString.toString();
            return result.substring(0, result.length()-1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}