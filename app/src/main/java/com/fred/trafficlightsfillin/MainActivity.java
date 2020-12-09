package com.fred.trafficlightsfillin;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.AppOpsManagerCompat;
import androidx.core.content.ContextCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.fred.trafficlightsfillin.base.MyGlideEngine;
import com.fred.trafficlightsfillin.base.RequestApi;
import com.fred.trafficlightsfillin.feed.FeedActivity;
import com.fred.trafficlightsfillin.login.ChangePasswordActivity;
import com.fred.trafficlightsfillin.login.LocationResponse;
import com.fred.trafficlightsfillin.login.LoginActivity;
import com.fred.trafficlightsfillin.login.LoginResponse;
import com.fred.trafficlightsfillin.network.http.ProRequest;
import com.fred.trafficlightsfillin.network.http.response.ICallback;
import com.fred.trafficlightsfillin.query.QueryMainActivity;
import com.fred.trafficlightsfillin.record.RecordListActivity;
import com.fred.trafficlightsfillin.record.RecordNewActivity;
import com.fred.trafficlightsfillin.record.UpdateListActivity;
import com.fred.trafficlightsfillin.utils.LocationUtils;
import com.fred.trafficlightsfillin.utils.SharedPreferenceUtils;
import com.fred.trafficlightsfillin.utils.StatusBarUtils;
import com.fred.trafficlightsfillin.utils.ToastUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 10;
    private static final int REQUEST_CODE_CHOOSE = 99;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.new_record_top)
    LinearLayout newRecordTop;
    @BindView(R.id.update_record_top)
    LinearLayout updateRecordTop;
    @BindView(R.id.list_record_top)
    LinearLayout listRecordTop;
    @BindView(R.id.timing_record_top)
    LinearLayout timingRecordTop;
    @BindView(R.id.feed)
    TextView feed;
    @BindView(R.id.tip_set)
    TextView tipSet;
    @BindView(R.id.update)
    TextView update;
    @BindView(R.id.new_record)
    TextView newRecord;
    @BindView(R.id.update_record)
    TextView updateRecord;
    @BindView(R.id.list_record)
    TextView listRecord;
    @BindView(R.id.timing_record)
    TextView timingRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //透明状态栏
        StatusBarUtils.setTransparent(this);

        //Log.e("ferd  sha:  ",sHA1(this));
        initLocation();

        initView();
        getNewVersion();
    }

    /**
     * 定位权限
     */
    private void initLocation() {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){//未开启定位权限
            //开启定位权限,200是标识码
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }
        if (!LocationUtils.isLocServiceEnable(MainActivity.this)) {//检测是否开启定位服务
            //未开启定位服务的操作
            ToastUtil.showMsg(MainActivity.this, "请前往设置界面打开定位功能");
        } else {//检测用户是否将当前应用的定位权限拒绝
            int checkResult = LocationUtils.checkOp(MainActivity.this, 2, AppOpsManager.OPSTR_FINE_LOCATION);//其中2代表AppOpsManager.OP_GPS，如果要判断悬浮框权限，第二个参数需换成24即AppOpsManager。OP_SYSTEM_ALERT_WINDOW及，第三个参数需要换成AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW
            int checkResult2 = LocationUtils.checkOp(MainActivity.this, 1, AppOpsManager.OPSTR_FINE_LOCATION);
            if (AppOpsManagerCompat.MODE_IGNORED == checkResult || AppOpsManagerCompat.MODE_IGNORED == checkResult2) {
                //未开启定位权限或者被拒绝的操作
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            } else {
                startLocation();
            }
        }
    }

    private void startLocation() {
        //检查权限
        //声明AMapLocationClient类对象
        AMapLocationClient mLocationClient = new AMapLocationClient(MainActivity.this.getApplicationContext());
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();

        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        mLocationOption.setOnceLocation(true);
        mLocationClient.setLocationListener(aMapLocation -> {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {//成功
                    //保存在本地
                    double lat = aMapLocation.getLatitude();
                    double lng = aMapLocation.getLongitude();
                    if (lat > 0 && lng > 0) {
                        Log.e("fred", "lat: " + lat + "  lng  " + lng);
                        locationInfo(String.valueOf(lat),String.valueOf(lat));
                    }
                } else {//失败
                    Log.i("fred", "Distance: 定位失败 :" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
                }
            }
        });
        mLocationClient.setLocationOption(mLocationOption);


        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            runOnUiThread(() -> {
                mLocationClient.startLocation();
            });
        }, 0, 100, TimeUnit.SECONDS);
    }

    /**
     * 上传位置信息
     * @param lat
     * @param lon
     */
    private void locationInfo(String lat,String lon){
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.LOCATIO))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token",SharedPreferenceUtils.getInstance().getrefreshToken())
                .addParam("flag",SharedPreferenceUtils.getInstance().getFlag())
                .addParam("lat",lat)
                .addParam("lon",lon)
                .build()
                .postAsync(new ICallback<LocationResponse>() {
                    @Override
                    public void onSuccess(LocationResponse response) {
                        if(response.code==401001){
                            //freshToken();
                            //getNewVersion();
                            SharedPreferenceUtils.getInstance().setToken("");
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {

                    }
                });
    }

    /**
     * 获取最新版本号
     */
    private void getNewVersion(){
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.NEW_VERSION))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token",SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .getAsync(new ICallback<LocationResponse>() {
                    @Override
                    public void onSuccess(LocationResponse response) {
                        if(response.code==0){

                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {

                    }
                });
    }

    /**
     * 刷新token
     */
    private void freshToken(){
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.REFRESH_TOKEN))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token",SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .getAsync(new ICallback<LocationResponse>() {
                    @Override
                    public void onSuccess(LocationResponse response) {
                        Log.e("fred  刷新：",response.toString());
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {

                    }
                });
    }

    private void initView(){
        feed.setOnClickListener(this::onClick);
        tipSet.setOnClickListener(this::onClick);
        update.setOnClickListener(this::onClick);

        newRecord.setOnClickListener(this);
        newRecordTop.setOnClickListener(this::onClick);

        updateRecord.setOnClickListener(this::onClick);
        updateRecordTop.setOnClickListener(this::onClick);

        listRecord.setOnClickListener(this::onClick);
        listRecordTop.setOnClickListener(this::onClick);

        timingRecord.setOnClickListener(this::onClick);
        timingRecordTop.setOnClickListener(this::onClick);

    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent();
        switch (v.getId()){
            case R.id.feed://反馈
                intent.setClass(MainActivity.this, FeedActivity.class);
                startActivity(intent);
                break;
            case R.id.tip_set://提醒

                break;
            case R.id.update://更新

                break;
            case R.id.new_record://新任务
            case R.id.new_record_top:
                intent.setClass(MainActivity.this, RecordNewActivity.class);
                startActivity(intent);
                break;
            case R.id.update_record://上传更新
            case R.id.update_record_top:
                intent.setClass(MainActivity.this, UpdateListActivity.class);
                startActivity(intent);
                break;
            case R.id.list_record://工作记录
            case R.id.list_record_top:
                intent.setClass(MainActivity.this, RecordListActivity.class);
                startActivity(intent);
                break;
            case R.id.timing_record://配时查询
            case R.id.timing_record_top:
                intent.setClass(MainActivity.this, QueryMainActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void test(){
        Matisse.from(MainActivity.this)
                .choose(MimeType.ofImage())
                .countable(true)
                .maxSelectable(9)
                .gridExpectedSize(120)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new MyGlideEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            List<Uri> mSelected = Matisse.obtainResult(data);
            //String s = getPAth(mSelected.get(0));
            //Log.i("Path", s);
        }

    }
}