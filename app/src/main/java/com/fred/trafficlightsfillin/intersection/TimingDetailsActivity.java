package com.fred.trafficlightsfillin.intersection;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fred.trafficlightsfillin.R;
import com.fred.trafficlightsfillin.base.BaseRecyclerAdapter;
import com.fred.trafficlightsfillin.base.BaseViewHolder;
import com.fred.trafficlightsfillin.base.RequestApi;
import com.fred.trafficlightsfillin.intersection.bean.ImageResponse;
import com.fred.trafficlightsfillin.intersection.bean.StageResponse;
import com.fred.trafficlightsfillin.intersection.bean.TimingDetailsResponse;
import com.fred.trafficlightsfillin.intersection.bean.TrafficlighResonse;
import com.fred.trafficlightsfillin.network.http.ProRequest;
import com.fred.trafficlightsfillin.network.http.response.ICallback;
import com.fred.trafficlightsfillin.record.bean.TaskDetailsChannel;
import com.fred.trafficlightsfillin.utils.SharedPreferenceUtils;
import com.fred.trafficlightsfillin.utils.TimeUtils;
import com.fred.trafficlightsfillin.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimingDetailsActivity extends AppCompatActivity {

    @BindView(R.id.number)
    TextView number;
    @BindView(R.id.road_name)
    TextView roadName;
    @BindView(R.id.area)
    TextView area;
    @BindView(R.id.road_position)
    TextView roadPosition;
    @BindView(R.id.road_type)
    TextView roadType;
    @BindView(R.id.model_type)
    TextView modelType;
    @BindView(R.id.model_number)
    TextView modelNumber;
    @BindView(R.id.programme)
    RecyclerView programme;
    @BindView(R.id.timetable)
    RecyclerView timetable;
    @BindView(R.id.time_list)
    RecyclerView timeList;
    @BindView(R.id.picture_list)
    RecyclerView pictureList;
    @BindView(R.id.better)
    TextView better;
    @BindView(R.id.task_end)
    ImageView taskEnd;
    @BindView(R.id.end_time)
    TextView endTime;
    @BindView(R.id.submit)
    TextView submit;
    @BindView(R.id.state)
    TextView state;

    @BindView(R.id.week_title)
    LinearLayout weekTitle;
    @BindView(R.id.task_status)
    TextView taskStatus;
    @BindView(R.id.weekday)
    TextView weekday;
    @BindView(R.id.weekend)
    TextView weekend;

    private List<TimingDetailsResponse.DataBean.PeriodCaseListBean> weekdaysPeriodCaseList = new ArrayList<>();//工作日时间表
    private List<TimingDetailsResponse.DataBean.PeriodCaseListBean> weekendPeriodCaseList = new ArrayList<>();//周末时间表

    private List<TimingDetailsResponse.DataBean.PlanCaseListBean> weekdaysPlanCaseList = new ArrayList<>();//工作日配时表1
    private List<TimingDetailsResponse.DataBean.PlanCaseListBean> weekendPlanCaseList = new ArrayList<>();//周末配时表1

    private List<TimingDetailsResponse.DataBean.TimeCaseListBean> weekdaysTimeCaseList = new ArrayList<>();//工作日配时表1
    private List<TimingDetailsResponse.DataBean.TimeCaseListBean> weekendTimeCaseList = new ArrayList<>();//周末配时表1

    PictureAdapter pictureAdapter;
    TimeTableAdapter timeTableAdapter;
    String trafficLightId;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timing_details);
        ButterKnife.bind(this);

        trafficLightId = getIntent().getStringExtra("trafficLightId");
        id = getIntent().getStringExtra("id");
        initView();
        initStage();
    }

    /**
     * 加载基础布局
     */
    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        pictureList.setLayoutManager(layoutManager);
        pictureAdapter = new PictureAdapter();
        pictureList.setAdapter(pictureAdapter);

        timeList.setLayoutManager(new LinearLayoutManager(this));
        timeTableAdapter = new TimeTableAdapter();
        timeList.setAdapter(timeTableAdapter);

        //工作日
        weekday.setOnClickListener(view -> {
            weekday.setBackground(getResources().getDrawable(R.drawable.bg_color_blue_stroke));
            weekend.setBackground(getResources().getDrawable(R.drawable.bg_color_blue_gray_stroke));
            timeTableAdapter.bindData(true,weekdaysPeriodCaseList);
        });
        //周日
        weekend.setOnClickListener(view -> {
            weekend.setBackground(getResources().getDrawable(R.drawable.bg_color_blue_stroke));
            weekday.setBackground(getResources().getDrawable(R.drawable.bg_color_blue_gray_stroke));
            timeTableAdapter.bindData(true,weekendPeriodCaseList);
        });
    }

    /**
     * 获取次序表数据
     */
    private void initStage() {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.STAGE_LIST))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .addParam("pageSize", "100")
                .addParam("pageNum", "1")
                .build()
                .postAsync(new ICallback<StageResponse>() {
                    @Override
                    public void onSuccess(StageResponse response) {
                        if (response.code == 0) {
                            ToastUtil.showMsg(TimingDetailsActivity.this, response.msg + response.data.size());
//                            String base64 = ""
//                            byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
//                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                            imageView.setImageBitmap(decodedByte);
                            initTrafficlighInfo();
                            initTaskInfo();
                            initPictrue();
                            initTrafficlighPeishi();
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                    }
                });
    }

    /**
     * 获取基础信息
     */
    private void initTrafficlighInfo() {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TRAFFICLIGH_DETAILS) + "/" + id)
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .postAsync(new ICallback<TrafficlighResonse>() {
                    @Override
                    public void onSuccess(TrafficlighResonse response) {
                        if (response.code == 0) {
                            number.setText("编号：" + response.no);
                            roadName.setText(response.roadPlace);
                            area.setText(response.area);
                            roadPosition.setText(response.location);
                            roadType.setText(response.roadPlaceType);
                            modelType.setText(response.modelType);
                            modelNumber.setText(response.modelNo);
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                    }
                });
    }

    /**
     * 获取现场图片
     */
    private void initPictrue() {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TRAFFICLIGH_IMAGES) + "/" + id)
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .postAsync(new ICallback<ImageResponse>() {
                    @Override
                    public void onSuccess(ImageResponse response) {
                        if (response.code == 0) {
                            pictureAdapter.bindData(true, response.data);
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                    }
                });
    }

    /**
     * 获取任务详情
     */
    private void initTaskInfo() {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TASK_DETAILS) + "/" + id)
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .getAsyncTwo(new ICallback<TaskDetailsChannel>() {
                    @Override
                    public void onSuccess(TaskDetailsChannel response) {
                        Log.e("fred  新数据：", response.toString());
                        if (response.data != null) {
                            TaskDetailsChannel.TaskDetails taskDetails = response.data;
                            if ("0".equals(taskDetails.state)) {
                                state.setText("后台取消");
                            } else if ("1".equals(taskDetails.state)) {
                                state.setText("未接单");
                            } else if ("2".equals(taskDetails.state)) {
                                state.setText("未完成");
                            } else if ("3".equals(taskDetails.state)) {
                                state.setText("已完成");
                            } else if ("4".equals(taskDetails.state)) {
                                state.setText("完成已上传");
                            }

                            better.setText(taskDetails.cause);
                            endTime.setText(TimeUtils.time10(taskDetails.updateTime));
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                    }
                });
    }

    class PictureAdapter extends BaseRecyclerAdapter<ImageResponse.ImageBean> {

        @Override
        public int bindView(int viewType) {
            return R.layout.layout_picture_item;
        }

        @Override
        public void onBindHolder(BaseViewHolder holder, @Nullable ImageResponse.ImageBean imageBean, int index) {
            //ImageView add = holder.obtainView(R.id.iv_add);
            //ImageView delete=holder.obtainView(R.id.iv_delete);
            ImageView picture = holder.obtainView(R.id.iv_picture);

            String pictureUrl = RequestApi.DOWN_IMG + "/" + imageBean.path;
            Glide.with(TimingDetailsActivity.this).load(pictureUrl).into(picture);
        }
    }

    /**
     * 获取详细配时信息
     */
    private void initTrafficlighPeishi() {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TRAFFICLIGH_PEISHI + "/" + getIntent().getStringExtra("id")))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .postAsync(new ICallback<TimingDetailsResponse>() {
                    @Override
                    public void onSuccess(TimingDetailsResponse response) {
                        if (response.getCode() != 0 || response.getData() == null) {
                            return;
                        }
                        TimingDetailsResponse.DataBean data = response.getData();
                        List<TimingDetailsResponse.DataBean.PeriodCaseListBean> periodCaseList = data.getPeriodCaseList();
                        List<TimingDetailsResponse.DataBean.PlanCaseListBean> planCaseList = data.getPlanCaseList();
                        List<TimingDetailsResponse.DataBean.TimeCaseListBean> timeCaseList = data.getTimeCaseList();

                        for (int i = 0; i < periodCaseList.size(); i++) {
                            if ("0".equals(periodCaseList.get(i).getWorkday())) {//周末
                                weekendPeriodCaseList.add(periodCaseList.get(i));
                            } else {
                                weekdaysPeriodCaseList.add(periodCaseList.get(i));
                            }
                        }

                        if (weekendPeriodCaseList == null || weekendPeriodCaseList.size() == 0) {
                            weekTitle.setVisibility(View.VISIBLE);
                        } else {
                            weekTitle.setVisibility(View.VISIBLE);
                        }

                        for (int i = 0; i < planCaseList.size(); i++) {
                            if ("0".equals(planCaseList.get(i).getWorkday())) {//周末
                                weekendPlanCaseList.add(planCaseList.get(i));
                            } else {
                                weekdaysPlanCaseList.add(planCaseList.get(i));
                            }
                        }

                        for (int i = 0; i < timeCaseList.size(); i++) {
                            if ("0".equals(timeCaseList.get(i).getWorkday())) {//周末
                                weekendTimeCaseList.add(timeCaseList.get(i));
                            } else {
                                weekdaysTimeCaseList.add(timeCaseList.get(i));
                            }
                        }

                        timeTableAdapter.bindData(true, weekdaysPeriodCaseList);
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                    }
                });
    }


    class TimeTableAdapter extends BaseRecyclerAdapter<TimingDetailsResponse.DataBean.PeriodCaseListBean> {

        @Override
        public int bindView(int viewType) {
            return R.layout.layout_time_table_item;
        }

        @Override
        public void onBindHolder(BaseViewHolder holder, @Nullable TimingDetailsResponse.DataBean.PeriodCaseListBean periodCaseListBean, int index) {
            TextView startTime = holder.obtainView(R.id.start_time);
            TextView no = holder.obtainView(R.id.number);

            startTime.setText(periodCaseListBean.getStart());
            no.setText(periodCaseListBean.getTimeCaseNo());
        }
    }
}
