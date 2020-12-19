package com.fred.trafficlightsfillin.record;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fred.trafficlightsfillin.R;
import com.fred.trafficlightsfillin.base.BaseResponse;
import com.fred.trafficlightsfillin.base.RequestApi;
import com.fred.trafficlightsfillin.intersection.bean.TrafficlighResonse;
import com.fred.trafficlightsfillin.network.http.ProRequest;
import com.fred.trafficlightsfillin.network.http.response.ICallback;
import com.fred.trafficlightsfillin.record.bean.EngigeerResponse;
import com.fred.trafficlightsfillin.record.bean.TaskDetailsChannel;
import com.fred.trafficlightsfillin.utils.SharedPreferenceUtils;
import com.fred.trafficlightsfillin.utils.ToastUtil;

import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordNewDetailsActivity extends AppCompatActivity {

    @BindView(R.id.road_name)
    TextView roadName;
    @BindView(R.id.road_place)
    TextView roadPlace;
    @BindView(R.id.model_one)
    TextView modelOne;
    @BindView(R.id.model_two)
    TextView modelTwo;
    @BindView(R.id.team)
    TextView team;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.task)
    TextView task;
    @BindView(R.id.sec)
    TextView sec;
    @BindView(R.id.receiving)
    TextView receiving;
    String id;
    @BindView(R.id.go_to)
    TextView goTo;
    @BindView(R.id.finish)
    TextView finish;
    @BindView(R.id.bottom_view)
    LinearLayout bottomView;

    TaskDetailsChannel.TaskDetails taskDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_new_details);
        ButterKnife.bind(this);

        initData();

        receiving.setOnClickListener(v -> {
            submit();
        });

        String state = getIntent().getStringExtra("state");
       if ("1".equals(state)) {
            //"未接单
            bottomView.setVisibility(View.GONE);
            receiving.setVisibility(View.VISIBLE);
        } else if ("2".equals(state)) {
            //未完成
            bottomView.setVisibility(View.VISIBLE);
            receiving.setVisibility(View.GONE);
        }
        //点击完成
        finish.setOnClickListener(v -> {
             changeState();
        });

       goTo.setOnClickListener(v -> {
           initTrafficlighInfo();
       });
    }

    private void showWindow(TrafficlighResonse.TrafficlightChannel response){
        View mapSheetView = LayoutInflater.from(this).inflate(R.layout.map_navagation_sheet, null);

        TextView navigation_btn=mapSheetView.findViewById(R.id.navigation_btn);
        TextView baidu_btn=mapSheetView.findViewById(R.id.baidu_btn);
        TextView gaode_btn=mapSheetView.findViewById(R.id.gaode_btn);
        TextView tencent_btn=mapSheetView.findViewById(R.id.tencent_btn);

        PopupWindow mBottomSheetPop = new PopupWindow(this);
        mBottomSheetPop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mBottomSheetPop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mBottomSheetPop.setContentView(mapSheetView);
        mBottomSheetPop.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mBottomSheetPop.setOutsideTouchable(true);
        mBottomSheetPop.setFocusable(true);
        mBottomSheetPop.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);


        navigation_btn.setOnClickListener(v -> {
            if (mBottomSheetPop != null) {
                mBottomSheetPop.dismiss();
            }
        });

        baidu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAvilible(RecordNewDetailsActivity.this, "com.baidu.BaiduMap")) {//传入指定应用包名
                    try {
                        Intent intent = Intent.getIntent("intent://map/direction?" +
                                "destination=latlng:" + response.lat + "," + response.lon + "|name:"+roadName.getText().toString().trim()+    //终点
                                "&mode=driving&" +     //导航路线方式
                                "&src=appname#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                        startActivity(intent); //启动调用
                    } catch (URISyntaxException e) {
                        Log.e("intent", e.getMessage());
                    }
                } else {//未安装
                    //market为路径，id为包名
                    //显示手机上所有的market商店
                    Toast.makeText(RecordNewDetailsActivity.this, "您尚未安装百度地图", Toast.LENGTH_LONG).show();
                    Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (intent.resolveActivity(getPackageManager()) != null){
                        startActivity(intent);
                    }
                }
                mBottomSheetPop.dismiss();

            }
        });

        gaode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAvilible(RecordNewDetailsActivity.this, "com.autonavi.minimap")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);

                    //将功能Scheme以URI的方式传入data
                    Uri uri = Uri.parse("androidamap://navi?sourceApplication=appname&poiname="+roadName.getText().toString().trim()+"&lat=" + response.lat+ "&lon=" + response.lon + "&dev=1&style=2");
                    intent.setData(uri);

                    //启动该页面即可
                    startActivity(intent);
                } else {
                    Toast.makeText(RecordNewDetailsActivity.this, "您尚未安装高德地图", Toast.LENGTH_LONG).show();
                    Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (intent.resolveActivity(getPackageManager()) != null){
                        startActivity(intent);
                    }
                }
                mBottomSheetPop.dismiss();
            }
        });

        tencent_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_DEFAULT);

                //将功能Scheme以URI的方式传入data
                Uri uri = Uri.parse("qqmap://map/routeplan?type=drive&to="+roadName.getText().toString().trim()+"&tocoord=" + response.lat + "," + response.lon);
                intent.setData(uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    //启动该页面即可
                    startActivity(intent);
                } else {
                    Toast.makeText(RecordNewDetailsActivity.this, "您尚未安装腾讯地图", Toast.LENGTH_LONG).show();
                }
                mBottomSheetPop.dismiss();
            }
        });

    }

    public static boolean isAvilible(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(packageName);
        if (i == null) {
            return false;
        }
        return true;
    }

    private void initData() {
        id = getIntent().getStringExtra("id");
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TASK_DETAILS) + "/" + id)
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .getAsyncTwo(new ICallback<TaskDetailsChannel>() {
                    @Override
                    public void onSuccess(TaskDetailsChannel response) {
                        Log.e("fred  新数据：", response.toString());
                        if (response.data != null) {
                            taskDetails = response.data;
                            roadName.setText(taskDetails.roadPlace);
                            modelOne.setText(taskDetails.modelNo);
                            modelTwo.setText(taskDetails.modelType);
                            task.setText(taskDetails.source);
                            sec.setText(taskDetails.desc);
                            roadPlace.setText(taskDetails.area);

                            initEngigeerInfo();
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                    }
                });
    }

    /**
     * 接单
     */
    private void submit() {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TASK_STATE))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .addParam("taskId", id)
                .addParam("state", "2")
                .build()
                .putAsync(new ICallback<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        if(response.code==0){
                            bottomView.setVisibility(View.GONE);
                            receiving.setVisibility(View.VISIBLE);
                        }
                        ToastUtil.showMsg(RecordNewDetailsActivity.this, response.msg);
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                    }
                });
    }

    /**
     * 完成
     */
    private void changeState() {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TASK_STATE))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .addParam("taskId", id)
                .addParam("state", "3")
                .build()
                .putAsync(new ICallback<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        if(response.code==0){
                            bottomView.setVisibility(View.GONE);
                            receiving.setVisibility(View.VISIBLE);
                        }
                        ToastUtil.showMsg(RecordNewDetailsActivity.this, response.msg);
                        finish();
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                    }
                });
    }

    /**
     * 获取路口基础信息
     */
    private void initTrafficlighInfo() {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TRAFFICLIGH_DETAILS) + "/" + taskDetails.getTrafficLightId())
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .getAsync(new ICallback<TrafficlighResonse>() {
                    @Override
                    public void onSuccess(TrafficlighResonse trafficlighResonse) {
                        if (trafficlighResonse.code == 0) {
                            TrafficlighResonse.TrafficlightChannel response = trafficlighResonse.data;
                            showWindow(response);
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                    }
                });
    }


    private void initEngigeerInfo() {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.ENGINEER_INFO) + "/" + taskDetails.getEngineerId())
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .getAsync(new ICallback<EngigeerResponse>() {
                    @Override
                    public void onSuccess(EngigeerResponse engigeerResponse) {
                        if (engigeerResponse.code == 0) {
                            team.setText(engigeerResponse.data.teamName);
                            name.setText(engigeerResponse.data.name);
                            phone.setText(engigeerResponse.data.phone);
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                    }
                });
    }
}
