package com.fred.trafficlightsfillin.query;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fred.trafficlightsfillin.R;
import com.fred.trafficlightsfillin.base.BaseRecyclerAdapter;
import com.fred.trafficlightsfillin.base.BaseViewHolder;
import com.fred.trafficlightsfillin.base.RequestApi;
import com.fred.trafficlightsfillin.login.ChangePasswordActivity;
import com.fred.trafficlightsfillin.login.LoginResponse;
import com.fred.trafficlightsfillin.network.http.ProRequest;
import com.fred.trafficlightsfillin.network.http.response.ICallback;
import com.fred.trafficlightsfillin.query.bean.RoadResponse;
import com.fred.trafficlightsfillin.query.bean.TeamListResponse;
import com.fred.trafficlightsfillin.record.RecordNewActivity;
import com.fred.trafficlightsfillin.record.bean.NewRecordChannel;
import com.fred.trafficlightsfillin.record.bean.NewRecordResponse;
import com.fred.trafficlightsfillin.utils.SharedPreferenceUtils;
import com.fred.trafficlightsfillin.utils.TimeUtils;
import com.fred.trafficlightsfillin.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QueryMainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.start_time)
    TextView startTime;
    @BindView(R.id.end_ime)
    TextView endIme;
    @BindView(R.id.team)
    TextView team;
    @BindView(R.id.from)
    TextView from;
    @BindView(R.id.query_one)
    TextView queryOne;
    @BindView(R.id.query_two)
    TextView queryTwo;
    @BindView(R.id.content)
    RecyclerView content;

    List<NewRecordChannel> list = new ArrayList<>();
    int page = 1;
    NewRecordAdapter recordAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_main);
        ButterKnife.bind(this);

        initView();
        getRoadData();
        getTeamList();
        getTaskList();
    }

    private void initView() {
        queryOne.setOnClickListener(this);
        queryTwo.setOnClickListener(this);
        startTime.setOnClickListener(this::onClick);
        endIme.setOnClickListener(this::onClick);

        recordAdapter = new NewRecordAdapter();
        content.setLayoutManager(new LinearLayoutManager(QueryMainActivity.this));
        content.setAdapter(recordAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.query_one:

                break;
            case R.id.query_two:

                break;
            case R.id.start_time:
                showDatePickerDialog(QueryMainActivity.this,1);
                break;
            case R.id.end_ime:
                showDatePickerDialog(QueryMainActivity.this,2);
                break;
        }
    }

    /**
     * 日期选择
     * @param activity
     */
    public void showDatePickerDialog(Activity activity,int type) {
        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
        // 绑定监听器(How the parent is notified that the date is set.)
        Calendar calendar=Calendar.getInstance();
        new DatePickerDialog(activity, (view, year, monthOfYear, dayOfMonth) -> {
            // 此处得到选择的时间，可以进行你想要的操作
            Log.e("fred","您选择了：" + year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日");
            String time=year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日";
            if(type==1){
                startTime.setText(time);
            }else {
                endIme.setText(time);
            }
        }
                // 设置初始日期
                , calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * 获取路口数据
     */
    private void getRoadData(){
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.ROAD_PLACE))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token",SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .getAsync(new ICallback<RoadResponse>() {
                    @Override
                    public void onSuccess(RoadResponse response) {

                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {

                    }
                });
    }

    /**
     * 获取班组列表
     */
    private void getTeamList(){
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TEAM_LIST))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token",SharedPreferenceUtils.getInstance().getrefreshToken())
                .addParam("pageNum","1")
                .addParam("pageSize","50")
                .build()
                .postAsync(new ICallback<TeamListResponse>() {
                    @Override
                    public void onSuccess(TeamListResponse response) {

                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {

                    }
                });
    }

    /**
     * 任务来源
     */
    private void getTaskList(){
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TASK_SOURCE))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token",SharedPreferenceUtils.getInstance().getrefreshToken())
                .addParam("pageNum","1")
                .addParam("pageSize","50")
                .build()
                .postAsync(new ICallback<TeamListResponse>() {
                    @Override
                    public void onSuccess(TeamListResponse response) {

                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {

                    }
                });
    }

    /**
     * 请求数据
     */
    private void initData() {
        Log.e("fred","  开始请求新数据：");
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TASK_PAGE))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .addParam("pageNum", String.valueOf(page))
                .addParam("pageSize", "20")
                .addParam("teamName",team.getText().toString().trim())
                .addParam("source",from.getText().toString().trim())
                .addParam("startTime","")
                .addParam("endTime","")
                .build()
                .postAsync(new ICallback<NewRecordResponse>() {
                    @Override
                    public void onSuccess(NewRecordResponse response) {
                        Log.e("fred  新数据：", response.toString());
                        if (page == 1) {
                            list.clear();
                            list.addAll(response.data.list);
                            recordAdapter.bindData(true, list);
                        }else {
                            list.addAll(response.data.list);
                            recordAdapter.bindData(false, list);
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                    }
                });
    }

    class NewRecordAdapter extends BaseRecyclerAdapter<NewRecordChannel> {

        @Override
        public int bindView(int viewType) {
            return R.layout.layout_new_record_item;
        }

        @Override
        public void onBindHolder(BaseViewHolder holder, @Nullable NewRecordChannel newRecordChannel, int index) {
            TextView road_name = holder.obtainView(R.id.road_name);
            TextView task_status = holder.obtainView(R.id.task_status);
            TextView task_from = holder.obtainView(R.id.task_from);
            TextView time = holder.obtainView(R.id.time);

            time.setText(TimeUtils.time7(String.valueOf(newRecordChannel.createTime)));
            task_from.setText("来源：" + newRecordChannel.source);
            road_name.setText(newRecordChannel.roadPlace);
            task_status.setText("未接单");
            task_status.setTextColor(Color.parseColor("#FF8631"));
        }
    }
}
