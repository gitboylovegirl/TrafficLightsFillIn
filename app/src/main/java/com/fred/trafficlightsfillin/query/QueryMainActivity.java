package com.fred.trafficlightsfillin.query;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fred.trafficlightsfillin.R;
import com.fred.trafficlightsfillin.base.BaseRecyclerAdapter;
import com.fred.trafficlightsfillin.base.BaseViewHolder;
import com.fred.trafficlightsfillin.base.RequestApi;
import com.fred.trafficlightsfillin.intersection.TimingDetailsActivity;
import com.fred.trafficlightsfillin.network.http.ProRequest;
import com.fred.trafficlightsfillin.network.http.response.ICallback;
import com.fred.trafficlightsfillin.query.bean.RoadResponse;
import com.fred.trafficlightsfillin.query.bean.RoadTypeResponse;
import com.fred.trafficlightsfillin.query.bean.TeamListResponse;
import com.fred.trafficlightsfillin.record.bean.NewRecordChannel;
import com.fred.trafficlightsfillin.record.bean.NewRecordResponse;
import com.fred.trafficlightsfillin.utils.DialogUtils;
import com.fred.trafficlightsfillin.utils.SearchAdapter;
import com.fred.trafficlightsfillin.utils.SharedPreferenceUtils;
import com.fred.trafficlightsfillin.utils.TimeUtils;

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
    List<RoadResponse.RoadChannel> roadChannels = new ArrayList<>();
    List<TeamListResponse.TeamListChannel> teamListChannels = new ArrayList<>();
    List<TeamListResponse.TeamListChannel> taskListChannels = new ArrayList<>();

    List<String> roadTypeData = new ArrayList<>();
    int page = 1;
    NewRecordAdapter recordAdapter;
    @BindView(R.id.road_place)
    TextView roadPlace;
    @BindView(R.id.road_type)
    TextView roadType;
    @BindView(R.id.empty_view)
    TextView emptyView;

    int queryType=1;

    private LinearLayout empty;
    private EditText search;

    PopupWindow popupWindow;
    RoadPlaceadapter roadPlaceadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_main);
        ButterKnife.bind(this);

        initView();
        getRoadData();
        getTeamList();
        getTaskList();
        getRoadTypeList();

        roadPlaceadapter=new RoadPlaceadapter();

        search = findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                List<String> roadPlaces = new ArrayList<>();
                List<RoadResponse.RoadChannel> roadData= new ArrayList<>();
                if(!TextUtils.isEmpty(s.toString().trim())){
                    roadPlaces.clear();
                    for (int i = 0; i < roadChannels.size(); i++) {
                        if(roadChannels.get(i).getRoadPlace().contains(s.toString().trim())&&roadChannels.get(i).getRoadPlace()!=null){
                            roadPlaces.add(roadChannels.get(i).getRoadPlace());
                            roadData.add(roadChannels.get(i));
                        }
                    }
                    Log.e("fred",roadPlaces.size()+"  路口数据");
                    if (roadPlaces!=null&&roadPlaces.size()>0){
                        if(popupWindow!=null&&popupWindow.isShowing()){
                            popupWindow.dismiss();
                        }
                        showPopupWindow(search,roadData);
                        roadPlaceadapter.bindData(true,roadPlaces);
                        search.setFocusable(true);
                    }else {
                        if(popupWindow!=null&&popupWindow.isShowing()){
                            popupWindow.dismiss();
                        }
                    }
                }else {
                    if(popupWindow!=null&&popupWindow.isShowing()){
                        popupWindow.dismiss();
                    }
                }
            }
        });
    }

    private void showPopupWindow(View view,List<RoadResponse.RoadChannel> data) {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.layout_popupwindow, null);

        RecyclerView content=contentView.findViewById(R.id.content);

        content.setLayoutManager(new LinearLayoutManager(QueryMainActivity.this));
        content.setAdapter(roadPlaceadapter);

        roadPlaceadapter.setOnItemClickListener((adapter, holder, itemView, index) -> {
            search.setText(data.get(index).getRoadPlace());
            search.setSelection(data.get(index).getRoadPlace().length());
            roadType.setText(data.get(index).getRoadPlaceType());
            popupWindow.dismiss();
        });

        popupWindow = new PopupWindow(contentView,  LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //popupWindow.setTouchable(true);
        popupWindow.setFocusable(false);
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_color_white_4dp));

        // 设置好参数之后再show
        popupWindow.showAsDropDown(view);
    }

    class RoadPlaceadapter extends BaseRecyclerAdapter<String>{

        @Override
        public int bindView(int viewType) {
            return R.layout.layout_road_place_item;
        }

        @Override
        public void onBindHolder(BaseViewHolder holder, @Nullable String s, int index) {
            TextView tv_place=holder.obtainView(R.id.tv_place);

            tv_place.setText(s);
        }
    }

    private void initView() {
        queryOne.setOnClickListener(this);
        queryTwo.setOnClickListener(this);
        startTime.setOnClickListener(this::onClick);
        endIme.setOnClickListener(this::onClick);
        from.setOnClickListener(this::onClick);
        roadPlace.setOnClickListener(this);
        roadType.setOnClickListener(this);
        team.setOnClickListener(this::onClick);

        recordAdapter = new NewRecordAdapter();
        content.setLayoutManager(new LinearLayoutManager(QueryMainActivity.this));
        content.setAdapter(recordAdapter);

        long timeMillis = System.currentTimeMillis();
        startTime.setText(TimeUtils.time9(String.valueOf(timeMillis)));
        endIme.setText(TimeUtils.time9(String.valueOf(timeMillis)));

        recordAdapter.setOnItemClickListener((adapter, holder, itemView, index) -> {
            if (queryType==1){
                Intent intent=new Intent(QueryMainActivity.this,TaskDetailsActivity.class);
                intent.putExtra("id",list.get(index).id);
                intent.putExtra("trafficLightId",list.get(index).trafficLightId);
                startActivity(intent);
            }else {
                Intent intent=new Intent(QueryMainActivity.this, TimingDetailsActivity.class);
                intent.putExtra("id",list.get(index).id);
                intent.putExtra("trafficLightId",list.get(index).trafficLightId);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.query_one:
                queryType=1;
                page=1;
                initData();
                break;
            case R.id.query_two:
                queryType=2;
                page=1;
                initTimingData();
                break;
            case R.id.start_time:
                showDatePickerDialog(QueryMainActivity.this, 1);
                break;
            case R.id.end_ime:
                showDatePickerDialog(QueryMainActivity.this, 2);
                break;
            case R.id.road_type:
//                DialogUtils.showChoiceDialog(QueryMainActivity.this, roadTypeData, new DialogUtils.OnButtonClickListener() {
//                    @Override
//                    public void onPositiveButtonClick() {
//
//                    }
//
//                    @Override
//                    public void onNegativeButtonClick() {
//
//                    }
//
//                    @Override
//                    public void onChoiceItem(String str, int pos) {
//                        roadType.setText(str);
//                    }
//                });
                break;
            case R.id.road_place:
                getRoadData();
                List<String> roadPlaces = new ArrayList<>();
                for (int i = 0; i < roadChannels.size(); i++) {
                    roadPlaces.add(roadChannels.get(i).getRoadPlace());
                }
                DialogUtils.showChoiceDialog(QueryMainActivity.this, roadPlaces, new DialogUtils.OnButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick() {

                    }

                    @Override
                    public void onNegativeButtonClick() {

                    }

                    @Override
                    public void onChoiceItem(String str, int pos) {
                        roadPlace.setText(str);
                        //roadType.setText(roadChannels.get(pos)
                    }
                });
                break;
            case R.id.team:
                getTeamList();
                List<String> teamList = new ArrayList<>();
                for (int i = 0; i < teamListChannels.size(); i++) {
                    teamList.add(teamListChannels.get(i).name);
                }
                DialogUtils.showChoiceDialog(QueryMainActivity.this, teamList, new DialogUtils.OnButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick() {

                    }

                    @Override
                    public void onNegativeButtonClick() {

                    }

                    @Override
                    public void onChoiceItem(String str, int pos) {
                        team.setText(str);
                    }
                });
                break;
            case R.id.from:
                List<String> taskList = new ArrayList<>();
                for (int i = 0; i < taskListChannels.size(); i++) {
                    taskList.add(taskListChannels.get(i).source);
                }
                DialogUtils.showChoiceDialog(QueryMainActivity.this, taskList, new DialogUtils.OnButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick() {

                    }

                    @Override
                    public void onNegativeButtonClick() {

                    }

                    @Override
                    public void onChoiceItem(String str, int pos) {
                        from.setText(str);
                    }
                });
                getTaskList();
                break;
        }
    }

    /**
     * 日期选择
     *
     * @param activity
     */
    public void showDatePickerDialog(Activity activity, int type) {
        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
        // 绑定监听器(How the parent is notified that the date is set.)
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(activity, (view, year, monthOfYear, dayOfMonth) -> {
            // 此处得到选择的时间，可以进行你想要的操作
            String time = year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日";
            if (type == 1) {
                startTime.setText(time);
            } else {
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
    private void getRoadData() {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.ROAD_PLACE) + "/all")
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .getAsync(new ICallback<RoadResponse>() {
                    @Override
                    public void onSuccess(RoadResponse response) {
                        if (response.data != null) {
                            roadChannels.clear();
                            roadChannels.addAll(response.data);
                            //roadPlace.setText(roadChannels.get(0).getRoadPlace());
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {

                    }
                });
    }

    /**
     * 获取班组列表
     */
    private void getTeamList() {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TEAM_LIST))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .addParam("pageNum", "1")
                .addParam("pageSize", "50")
                .build()
                .postAsync(new ICallback<TeamListResponse>() {
                    @Override
                    public void onSuccess(TeamListResponse response) {
                        if (response.data != null) {
                            teamListChannels.clear();
                            teamListChannels.addAll(response.data);
                            //team.setText(teamListChannels.get(0).name);
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {

                    }
                });
    }

    /**
     * 任务来源
     */
    private void getTaskList() {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TASK_SOURCE))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .addParam("pageNum", "1")
                .addParam("pageSize", "50")
                .build()
                .postAsync(new ICallback<TeamListResponse>() {
                    @Override
                    public void onSuccess(TeamListResponse response) {
                        if (response.data != null) {
                            taskListChannels.clear();
                            taskListChannels.addAll(response.data);
                            //from.setText(taskListChannels.get(0).source);
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {

                    }
                });
    }

    /**
     * 路口类型
     */
    private void getRoadTypeList() {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.ROAD_TYPE))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .getAsync(new ICallback<RoadTypeResponse>() {
                    @Override
                    public void onSuccess(RoadTypeResponse response) {
                        if (response.data != null) {
                            roadTypeData.clear();
                            roadTypeData.addAll(response.data);
                            //from.setText(roadTypeData.get(0));
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {

                    }
                });
    }

    /**
     * 查任务
     */
    private void initData() {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TASK_PAGE))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .addParam("pageNum", String.valueOf(page))
                .addParam("pageSize", "50")
                .addParam("teamName", team.getText().toString().trim())
                .addParam("source", from.getText().toString().trim())
                .addParam("startTime", TimeUtils.time8(startTime.getText().toString().trim()))
                .addParam("endTime", TimeUtils.time8(endIme.getText().toString().trim()))
                .build()
                .postAsync(new ICallback<NewRecordResponse>() {
                    @Override
                    public void onSuccess(NewRecordResponse response) {
                        if (page == 1) {
                            list.clear();
                            list.addAll(response.data.list);
                            recordAdapter.bindData(true, list);
                        } else {
                            list.addAll(response.data.list);
                            recordAdapter.bindData(false, list);
                        }

                        if(list.isEmpty()){
                            emptyView.setVisibility(View.VISIBLE);
                            content.setVisibility(View.GONE);
                        }else {
                            emptyView.setVisibility(View.GONE);
                            content.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                    }
                });
    }

    /**
     * 查配时表
     */
    private void initTimingData() {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TASK_PAGE))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .addParam("pageNum", String.valueOf(page))
                .addParam("pageSize", "50")
                .addParam("teamName", team.getText().toString().trim())
                .addParam("source", from.getText().toString().trim())
                .addParam("startTime", TimeUtils.time8(startTime.getText().toString().trim()))
                .addParam("endTime", TimeUtils.time8(endIme.getText().toString().trim()))
                .addParam("state","4")
                .build()
                .postAsync(new ICallback<NewRecordResponse>() {
                    @Override
                    public void onSuccess(NewRecordResponse response) {
                        if (page == 1) {
                            list.clear();
                            list.addAll(response.data.list);
                            recordAdapter.bindData(true, list);
                        } else {
                            list.addAll(response.data.list);
                            recordAdapter.bindData(false, list);
                        }

                        if(list.isEmpty()){
                            emptyView.setVisibility(View.VISIBLE);
                            content.setVisibility(View.GONE);
                        }else {
                            emptyView.setVisibility(View.GONE);
                            content.setVisibility(View.VISIBLE);
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
