package com.fred.trafficlightsfillin;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.AppOpsManagerCompat;
import androidx.core.content.ContextCompat;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.fred.trafficlightsfillin.base.AppVerRespnse;
import com.fred.trafficlightsfillin.base.RequestApi;
import com.fred.trafficlightsfillin.feed.FeedActivity;
import com.fred.trafficlightsfillin.jiguang.JGReceiver;
import com.fred.trafficlightsfillin.login.AppUpdateResponse;
import com.fred.trafficlightsfillin.login.ChangePasswordActivity;
import com.fred.trafficlightsfillin.login.LocationResponse;
import com.fred.trafficlightsfillin.login.LoginActivity;
import com.fred.trafficlightsfillin.login.TokenResponse;
import com.fred.trafficlightsfillin.network.http.ProRequest;
import com.fred.trafficlightsfillin.network.http.response.ICallback;
import com.fred.trafficlightsfillin.query.QueryMainActivity;
import com.fred.trafficlightsfillin.record.RecordListActivity;
import com.fred.trafficlightsfillin.record.RecordNewActivity;
import com.fred.trafficlightsfillin.record.UpdateListActivity;
import com.fred.trafficlightsfillin.utils.CalendarReminderUtils;
import com.fred.trafficlightsfillin.utils.DialogUtils;
import com.fred.trafficlightsfillin.utils.DownloadUtils;
import com.fred.trafficlightsfillin.utils.LocationUtils;
import com.fred.trafficlightsfillin.utils.NotificationUtil;
import com.fred.trafficlightsfillin.utils.RoadDataUtil;
import com.fred.trafficlightsfillin.utils.SharedPreferenceUtils;
import com.fred.trafficlightsfillin.utils.StageDataUtil;
import com.fred.trafficlightsfillin.utils.StatusBarUtils;
import com.fred.trafficlightsfillin.utils.ToastUtil;
import com.zhihu.matisse.Matisse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 10;
    private static final int REQUEST_CODE_CHOOSE = 99;
    private static final String TAG ="MAIN" ;
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
    @BindView(R.id.login_out)
    TextView loginOut;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR};

    private static final int REQUEST_PERMISSION_CODE = 1;
    @BindView(R.id.change_status)
    Switch changeStatus;
    @BindView(R.id.change_password)
    TextView changePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //透明状态栏
        StatusBarUtils.setTransparent(this);
        //设置极光标签和别名
        setJiguangAlias();


        //Log.e("ferd  sha:  ",sHA1(this));
        initLocation();

        initView();
        getNewVersion(1);
        requestPermissions();

        /**
         * 加载基础数据
         *
         */
        StageDataUtil.init();
        RoadDataUtil.init();

        Intent intent = getIntent();
        String action = intent.getStringExtra("action");
        if(JGReceiver.FROMNOTICE.equals(action)){
            intent.setClass(MainActivity.this, RecordNewActivity.class);
            startActivity(intent);
        }
    }
    private void setJiguangAlias(){
        String phone = SharedPreferenceUtils.getInstance().getPhone();
        String engineerId = SharedPreferenceUtils.getInstance().getId();
        //Log.e("fred", "Phone:"+phone+";;EngineerId:"+engineerId);
        if(phone != null && !"".equals(phone) && engineerId != null && !"".equals(engineerId)){
            JPushInterface.setAlias(getApplicationContext(), Integer.parseInt(engineerId), phone);
            //Log.e("fred", "JPushInterface.setAlias:::::Phone:"+phone+";;EngineerId:"+engineerId);
        }
    }
    /**
     * 申请权限
     */
    public void requestPermissions() {
        if(!NotificationUtil.isNotifyEnabled(MainActivity.this)){

            DialogUtils.showCurrencyDialog(MainActivity.this, "开启通知，才能收到任务通知。", new DialogUtils.OnButtonClickListener() {
                @Override
                public void onPositiveButtonClick() {
                    NotificationUtil.openPermissionSetting(MainActivity.this);
                }

                @Override
                public void onNegativeButtonClick() {

                }

                @Override
                public void onChoiceItem(String str, int pos) {

                }
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (MainActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                //showToast("请同意读写权限，否则无法展示礼物");
                //如果没有写sd卡权限
                MainActivity.this.requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        100);
            }
            return;
        }
    }

    /**
     * 定位权限
     */
    private void initLocation() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED) {//未开启定位权限
            //开启定位权限,200是标识码
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
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
            double lat = 0.0;
            double lng = 0.0;
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {//成功
                    lat = aMapLocation.getLatitude();
                    lng = aMapLocation.getLongitude();
                } else {//失败
                    Log.i("fred", "Distance: 定位失败 :" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
                }
            }
            locationInfo(String.valueOf(lat), String.valueOf(lng));
        });
        mLocationClient.setLocationOption(mLocationOption);

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            runOnUiThread(() -> {
                mLocationClient.startLocation();
            });
        }, 0, 60, TimeUnit.SECONDS);
    }

    /**
     * 上传位置信息
     *
     * @param lat
     * @param lon
     */
    private void locationInfo(String lat, String lon) {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.LOCATIO))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh-token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .addParam("flag", SharedPreferenceUtils.getInstance().getFlag())
                .addParam("lat", lat)
                .addParam("lon", lon)
                .build()
                .postAsync(new ICallback<LocationResponse>() {
                    @Override
                    public void onSuccess(LocationResponse response) {
                        if (response.code == 401001) {
                            SharedPreferenceUtils.getInstance().setToken("");
                        } else if (response.code == 0) {

                            if (response.getData() == null || response.getData() <= 0) {//没有新任务不需要添加提醒
                                return;
                            }

                            boolean remindstate = SharedPreferenceUtils.getInstance().getRemindState();
                            if(!remindstate){//已关闭提醒
                                return;
                            }

                            long lastTime = SharedPreferenceUtils.getInstance().getLastRemindTime();
                            long currentTimeMillis = System.currentTimeMillis();

                            if(lastTime > currentTimeMillis){//如果最后提醒时间大于当前时间说明新任务日历提醒还没触发，不用添加新的
                                return;
                            }

                            long seTime = SharedPreferenceUtils.getInstance().getSeTime();
                            //计算下次提醒时间
                            long time = currentTimeMillis + (seTime * 60 * 60 * 1000);
                            //设置提醒
                            CalendarReminderUtils.addCalendarEvent(MainActivity.this, "配时中心提醒", "您有未完成的任务，请及时登陆完成", time, 1);
                            //记录最后提醒时间
                            SharedPreferenceUtils.getInstance().setLastRemindTime(time);
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {

                    }
                });
    }

    /**
     * 获取最新版本号
     * @param type 1 当前已是最新版本不需要提示  2 当前已是最新版本需要提示
     */
    private void getNewVersion(int type) {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.NEW_VERSION))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh-token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .getAsync(new ICallback<AppUpdateResponse>() {
                    @Override
                    public void onSuccess(AppUpdateResponse response) {
                        if (response.code == 0) {
                            int nowVersion =getVersionCode();
                            if(response.getData() != null && nowVersion < response.getData()){
                                //需要更新版本
                                DialogUtils.showCurrencyDialog(MainActivity.this, "检测到新版本，是否升级？", new DialogUtils.OnButtonClickListener() {
                                    @Override
                                    public void onPositiveButtonClick() {
                                        //获取配时信息
                                        getAppVersion(String.valueOf(response.getData()));
                                    }

                                    @Override
                                    public void onNegativeButtonClick() {

                                    }

                                    @Override
                                    public void onChoiceItem(String str, int pos) {

                                    }
                                });
                            }else {
                                if(type==2){
                                    ToastUtil.showMsg(MainActivity.this, "当前已是最新版本");
                                }
                            }
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {

                    }
                });
    }

    /**
     * 根据版本号查询版本信息
     */
    private void getAppVersion(String ver) {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.APP_VER)+"/"+ver)
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh-token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .getAsync(new ICallback<AppVerRespnse>() {
                    @Override
                    public void onSuccess(AppVerRespnse response) {
                        if (response.code == 0) {
                            String downUrl=RequestApi.getUrl(RequestApi.APP_DOWN)+"/"+response.data.getUrl();
                            ToastUtil.showMsg(MainActivity.this,"后台下载中！");
                            //测试url  https://d.douyucdn.cn/wsd-pkg-div/2020/06/29/0beadef548a24ce2e3a4904413c18587_feiyou01_6.2.1_20200629164350.apk
                            DownloadUtils.downLoadApk(MainActivity.this,downUrl, pos -> {
                                //拿到下载进度自定义后续操作  pos
                                Log.e("fred 下载进度",downUrl+pos);
                            });
                            //通知栏下载方式
                            //new DownloadUtils(MainActivity.this,"https://d.douyucdn.cn/wsd-pkg-div/2020/06/29/0beadef548a24ce2e3a4904413c18587_feiyou01_6.2.1_20200629164350.apk","配时中心");
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                        Log.e("fred 下载错误",errorMsg);
                    }
                });
    }


    private void initView() {
        feed.setOnClickListener(this::onClick);
        tipSet.setOnClickListener(this::onClick);
        update.setOnClickListener(this::onClick);
        if("1".equals(SharedPreferenceUtils.getInstance().getFlag())){
            status.setText("上班");
            changeStatus.setChecked(true);
        }else{
            status.setText("下班");
            changeStatus.setChecked(false);
        }
        changeStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    SharedPreferenceUtils.getInstance().setFlag("1");
                    status.setText("上班");
                }else{
                    SharedPreferenceUtils.getInstance().setFlag("0");
                    status.setText("下班");

                }
            }
        });

        newRecord.setOnClickListener(this);
        newRecordTop.setOnClickListener(this::onClick);

        updateRecord.setOnClickListener(this::onClick);
        updateRecordTop.setOnClickListener(this::onClick);

        listRecord.setOnClickListener(this::onClick);
        listRecordTop.setOnClickListener(this::onClick);

        timingRecord.setOnClickListener(this::onClick);
        timingRecordTop.setOnClickListener(this::onClick);

        loginOut.setOnClickListener(this::onClick);
        changePassword.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.feed://反馈
                intent.setClass(MainActivity.this, FeedActivity.class);
                startActivity(intent);
                break;
            case R.id.tip_set://提醒
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CALENDAR) != PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
                        return;
                    }
                }

                List<String> timeSet = new ArrayList<>();
                for (int i = 1; i < 25; i++) {
                    timeSet.add(i + "小时");
                }
                DialogUtils.showChoiceTitltDialog(MainActivity.this, timeSet, new DialogUtils.OnButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick() {

                    }

                    @Override
                    public void onNegativeButtonClick() {

                    }

                    @Override
                    public void onChoiceItem(String str, int pos) {
                        SharedPreferenceUtils.getInstance().setSetTime(pos + 1);
                    }
                });
                break;
            case R.id.update://更新
                getNewVersion(2);
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
            case R.id.login_out:
                intent.setClass(MainActivity.this, LoginActivity.class);
                SharedPreferenceUtils.getInstance().setToken(null);
                SharedPreferenceUtils.getInstance().setrefreshToken(null);
                startActivity(intent);
                finish();
                break;
            case R.id.change_password:
                intent.setClass(MainActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                break;
        }
    }

    private static boolean isExit = false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            exit();
            return false;
        }
        return super.onKeyDown(keyCode,event);
    }

    private void exit(){
        if(!isExit){
            isExit=true;
            Toast.makeText(getApplicationContext(),"再按一次退出",Toast.LENGTH_SHORT).show();
                    //利用handler延迟发送更改状态信息
                    handler.sendEmptyMessageDelayed(0,2000);
        }else{
            finish();
            System.exit(0);
        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            isExit=false;
        }
    };

    private int getVersionCode(){
        PackageManager pm = getPackageManager();
        int versionCode=1;
        try {
            PackageInfo pi = pm.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            versionCode = pi.versionCode;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Log.d(TAG,"longVersionCode:"+pi.getLongVersionCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (NoSuchMethodError e){
            e.printStackTrace();
        }
        return versionCode;
    }
}