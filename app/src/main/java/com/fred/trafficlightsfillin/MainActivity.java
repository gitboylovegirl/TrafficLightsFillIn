package com.fred.trafficlightsfillin;

import android.Manifest;
import android.app.AppOpsManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.AppOpsManagerCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.fred.trafficlightsfillin.utils.LocationUtils;
import com.fred.trafficlightsfillin.utils.StatusBarUtils;
import com.fred.trafficlightsfillin.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 10;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.change_status)
    TextView changeStatus;
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

        initLocation();
    }

    /**
     * 定位权限
     */
    private void initLocation() {
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
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {//成功
                        //保存在本地
                        double lat = aMapLocation.getLatitude();
                        double lng = aMapLocation.getLongitude();
                        if (lat > 0 && lng > 0) {
                            Log.e("fred", "lat: " + lat + "  lng  " + lng);
                        }
                    } else {//失败
                        Log.i("fred", "Distance: 定位失败 :" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
                    }
                }
            }
        });
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }
}