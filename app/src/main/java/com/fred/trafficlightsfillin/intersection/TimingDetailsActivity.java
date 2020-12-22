package com.fred.trafficlightsfillin.intersection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.fred.trafficlightsfillin.R;
import com.fred.trafficlightsfillin.base.BaseRecyclerAdapter;
import com.fred.trafficlightsfillin.base.BaseViewHolder;
import com.fred.trafficlightsfillin.base.RequestApi;
import com.fred.trafficlightsfillin.intersection.bean.ImageResponse;
import com.fred.trafficlightsfillin.intersection.bean.PeriodCaseListBean;
import com.fred.trafficlightsfillin.intersection.bean.PlanCaseListBean;
import com.fred.trafficlightsfillin.intersection.bean.StageResponse;
import com.fred.trafficlightsfillin.intersection.bean.TimeCaseListBean;
import com.fred.trafficlightsfillin.intersection.bean.TimingDetailsResponse;
import com.fred.trafficlightsfillin.intersection.bean.TrafficlighResonse;
import com.fred.trafficlightsfillin.network.http.ProRequest;
import com.fred.trafficlightsfillin.network.http.response.ICallback;
import com.fred.trafficlightsfillin.utils.SharedPreferenceUtils;
import com.fred.trafficlightsfillin.utils.TimeUtils;

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

    @BindView(R.id.week_title)
    LinearLayout weekTitle;
    @BindView(R.id.task_status)
    TextView taskStatus;
    @BindView(R.id.weekday)
    TextView weekday;
    @BindView(R.id.weekend)
    TextView weekend;

    @BindView(R.id.last_peishi_time)
    TextView lastPeishiTime;

    @BindView(R.id.scrollView)
    NestedScrollView scrollView;
    @BindView(R.id.layout_hide_tab)
    RecyclerView layoutHideTab;
    @BindView(R.id.layout_hide_scrollview)
    HorizontalScrollView layoutHideScrollview;
    @BindView(R.id.scrollView_one)
    HorizontalScrollView scrollViewOne;
    @BindView(R.id.scrollView_two)
    HorizontalScrollView scrollViewTwo;


    private List<PeriodCaseListBean> weekdaysPeriodCaseList = new ArrayList<>();//工作日时间表
    private List<PeriodCaseListBean> weekendPeriodCaseList = new ArrayList<>();//周末时间表

    private List<PlanCaseListBean> weekdaysPlanCaseList = new ArrayList<>();//工作日配时表1
    private List<PlanCaseListBean> weekendPlanCaseList = new ArrayList<>();//周末配时表1

    private List<TimeCaseListBean> weekdaysTimeCaseList = new ArrayList<>();//工作日配时表1
    private List<TimeCaseListBean> weekendTimeCaseList = new ArrayList<>();//周末配时表1

    PictureAdapter pictureAdapter;
    TimeTableAdapter timeTableAdapter;
    PlanCaseAdapter planCaseAdapter,hideplanCaseAdapter;
    TimeCaseAdapter timeCaseAdapter;
    long trafficLightId;
    List<StageResponse.StageChanel> stageChanels;//配时表数据

    boolean isWeekday = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timing_details);
        ButterKnife.bind(this);

        trafficLightId = getIntent().getLongExtra("trafficLightId", 0);
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

        planCaseAdapter=new PlanCaseAdapter();
        programme.setLayoutManager(new LinearLayoutManager(this));
        programme.setAdapter(planCaseAdapter);

        hideplanCaseAdapter = new PlanCaseAdapter();
        layoutHideTab.setLayoutManager(new LinearLayoutManager(this));
        layoutHideTab.setAdapter(hideplanCaseAdapter);

        timeCaseAdapter=new TimeCaseAdapter();
        timetable.setLayoutManager(new LinearLayoutManager(this));
        timetable.setAdapter(timeCaseAdapter);

        timeTableAdapter.bindData(true, weekdaysPeriodCaseList);
        planCaseAdapter.bindData(true,weekdaysPlanCaseList);
        timeCaseAdapter.bindData(true,weekdaysTimeCaseList);


        //监听
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                Log.e("fred", "%%%%     " + scrollY);
                if (scrollY > 1180) {//滑动距离大于v_report_divider的底坐标
                    layoutHideScrollview.setVisibility(View.VISIBLE);
                } else {
                    layoutHideScrollview.setVisibility(View.GONE);
                }
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollViewOne.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                layoutHideScrollview.scrollTo(scrollX, scrollY);
                scrollViewTwo.scrollTo(scrollX, scrollY);
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layoutHideScrollview.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                scrollViewOne.scrollTo(scrollX, scrollY);
                scrollViewTwo.scrollTo(scrollX, scrollY);
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollViewTwo.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                layoutHideScrollview.scrollTo(scrollX, scrollY);
                scrollViewOne.scrollTo(scrollX, scrollY);
            });
        }
        //工作日
        weekday.setOnClickListener(view -> {
            isWeekday = true;
            weekday.setBackground(getResources().getDrawable(R.drawable.bg_color_blue_gray_stroke_main));
            weekend.setBackground(getResources().getDrawable(R.drawable.bg_color_blue_gray_stroke));
            timeTableAdapter.bindData(true, weekdaysPeriodCaseList);
            planCaseAdapter.bindData(true,weekdaysPlanCaseList);
            timeCaseAdapter.bindData(true,weekdaysTimeCaseList);
            update();
        });
        //周日
        weekend.setOnClickListener(view -> {
            isWeekday = false;
            weekend.setBackground(getResources().getDrawable(R.drawable.bg_color_blue_gray_stroke_main));
            weekday.setBackground(getResources().getDrawable(R.drawable.bg_color_blue_gray_stroke));
            timeTableAdapter.bindData(true, weekendPeriodCaseList);
            planCaseAdapter.bindData(true,weekendPlanCaseList);
            timeCaseAdapter.bindData(true,weekendTimeCaseList);
            update();
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
                            stageChanels = response.data;
//                            String base64 = ""
//                            byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
//                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                            imageView.setImageBitmap(decodedByte);
                            initTrafficlighInfo();
                            //initTaskInfo();
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
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TRAFFICLIGH_DETAILS) + "/" + trafficLightId)
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .getAsync(new ICallback<TrafficlighResonse>() {
                    @Override
                    public void onSuccess(TrafficlighResonse trafficlighResonse) {
                        if (trafficlighResonse.code == 0) {
                            TrafficlighResonse.TrafficlightChannel response = trafficlighResonse.data;
                            number.setText("编号：" + response.no);
                            roadName.setText(response.roadPlace);
                            area.setText(response.area);
                            roadPosition.setText(response.location);
                            roadType.setText(response.roadPlaceType);
                            modelType.setText(response.modelType);
                            modelNumber.setText(response.modelNo);
                            if(response.date != null && !"".equals(response.date.trim()))
                                lastPeishiTime.setText(TimeUtils.time7(response.date));
                            else
                                lastPeishiTime.setText("暂无最后配时时间");
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
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TRAFFICLIGH_IMAGES) + "/" + trafficLightId)
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .getAsync(new ICallback<ImageResponse>() {
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
    /*private void initTaskInfo() {
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
                                state.setText("配时表未更新");
                            } else if ("4".equals(taskDetails.state)) {
                                state.setText("完成已上传");
                            }

                            if(TextUtils.isEmpty(taskDetails.cause)){
                                better.setText("无");
                            }else {
                                better.setText(taskDetails.cause);
                            }
                            endTime.setText(TimeUtils.time10(taskDetails.updateTime));
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                    }
                });
    }*/

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

            String pictureUrl =RequestApi.BASE_OFFICIAL_URL+RequestApi.DOWN_IMG + "/" + imageBean.path;
            Log.e("pictureUrl",pictureUrl);

//            //Authorization 请求头信息
//            LazyHeaders headers=  new LazyHeaders.Builder()
//                    //.addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
//                    .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
//                    .build();
//            //url 要加载的图片的地址，imageView 显示图片的ImageView
//            Glide.with(TimingDetailsActivity.this).load(new GlideUrl(pictureUrl, headers)).into(picture);

            GlideUrl glideUrl = new GlideUrl(pictureUrl, new LazyHeaders.Builder()
                    .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                    .build());

            Glide.with(TimingDetailsActivity.this)
                    .load(glideUrl)
                    .into(picture);
        }
    }

    /**
     * 获取详细配时信息
     */
    private void initTrafficlighPeishi() {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TRAFFICLIGH_PEISHI + "/" + trafficLightId))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .getAsync(new ICallback<TimingDetailsResponse>() {
                    @Override
                    public void onSuccess(TimingDetailsResponse response) {
                        if (response.getCode() != 0 || response.getData() == null) {
                            return;
                        }
                        TimingDetailsResponse.DataBean data = response.getData();
                        List<PeriodCaseListBean> periodCaseList = data.getPeriodCaseList();
                        List<PlanCaseListBean> planCaseList = data.getPlanCaseList();
                        List<TimeCaseListBean> timeCaseList = data.getTimeCaseList();

                        for (int i = 0; i < periodCaseList.size(); i++) {
                            if ("0".equals(periodCaseList.get(i).getWorkday())) {//周末
                                weekendPeriodCaseList.add(periodCaseList.get(i));
                            } else {
                                weekdaysPeriodCaseList.add(periodCaseList.get(i));
                            }
                        }

                        if (weekendPeriodCaseList == null || weekendPeriodCaseList.size() == 0) {
                            weekTitle.setVisibility(View.GONE);
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

                        update();
                        timeTableAdapter.bindData(true, weekdaysPeriodCaseList);
                        planCaseAdapter.bindData(true,weekdaysPlanCaseList);
                        timeCaseAdapter.bindData(true,weekdaysTimeCaseList);
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                    }
                });
    }


    /**
     * 时间表adapter
     */
    class TimeTableAdapter extends BaseRecyclerAdapter<PeriodCaseListBean> {

        @Override
        public int bindView(int viewType) {
            return R.layout.layout_time_table_item;
        }

        @Override
        public void onBindHolder(BaseViewHolder holder, @Nullable PeriodCaseListBean periodCaseListBean, int index) {
            TextView startTime = holder.obtainView(R.id.start_time);
            TextView no = holder.obtainView(R.id.number);

            startTime.setText(periodCaseListBean.getStart());
            no.setText(periodCaseListBean.getTimeCaseNo());
        }
    }
    private void update() {
        if (isWeekday) {
            List<PlanCaseListBean> hideWeekdaysPlanCaseList = new ArrayList<>();
            for (int i = 0; i < weekdaysPlanCaseList.size(); i++) {
                if ("1".equals(weekdaysPlanCaseList.get(i).getType())) {
                    hideWeekdaysPlanCaseList.add(weekdaysPlanCaseList.get(i));
                }
            }
            hideplanCaseAdapter.bindData(true, hideWeekdaysPlanCaseList);
        } else {
            List<PlanCaseListBean> hideWeekdaysPlanCaseList = new ArrayList<>();
            for (int i = 0; i < weekendPlanCaseList.size(); i++) {
                if ("1".equals(weekendPlanCaseList.get(i).getType())) {
                    hideWeekdaysPlanCaseList.add(weekendPlanCaseList.get(i));
                }
            }
            hideplanCaseAdapter.bindData(true, hideWeekdaysPlanCaseList);
        }
    }
    /**
     * 配时方案1 adapter
     */
    class PlanCaseAdapter extends BaseRecyclerAdapter<PlanCaseListBean> {

        @Override
        public int bindView(int viewType) {
            return R.layout.layout_timing_table_item;
        }

        @Override
        public void onBindHolder(BaseViewHolder holder, @Nullable PlanCaseListBean planCaseListBean, int index) {
            LinearLayout typeOne = holder.obtainView(R.id.type_one);
            LinearLayout typeTwo = holder.obtainView(R.id.type_two);
            LinearLayout typeThree = holder.obtainView(R.id.type_three);

            ImageView ivOne = holder.obtainView(R.id.iv_one);
            ImageView ivTwo = holder.obtainView(R.id.iv_two);
            ImageView ivthree = holder.obtainView(R.id.iv_three);
            ImageView ivFour = holder.obtainView(R.id.iv_four);
            ImageView ivFive = holder.obtainView(R.id.iv_five);
            ImageView ivSix = holder.obtainView(R.id.iv_six);
            ImageView ivSeven = holder.obtainView(R.id.iv_seven);
            ImageView ivEight = holder.obtainView(R.id.iv_eight);
            ImageView ivNine = holder.obtainView(R.id.iv_nine);
            ImageView ivTen = holder.obtainView(R.id.iv_ten);

            TextView titleId = holder.obtainView(R.id.title_id);
            TextView programme_one = holder.obtainView(R.id.programme_one);
            TextView programme_two = holder.obtainView(R.id.programme_two);
            TextView programme_three = holder.obtainView(R.id.programme_three);
            TextView programme_four = holder.obtainView(R.id.programme_four);
            TextView programme_five = holder.obtainView(R.id.programme_five);
            TextView programme_six = holder.obtainView(R.id.programme_six);
            TextView programme_seven = holder.obtainView(R.id.programme_seven);
            TextView programme_eight = holder.obtainView(R.id.programme_eight);
            TextView programme_nine = holder.obtainView(R.id.programme_nine);
            TextView programme_ten = holder.obtainView(R.id.programme_ten);

           if ("1".equals(planCaseListBean.getType())) {
                typeOne.setVisibility(View.GONE);
                typeTwo.setVisibility(View.VISIBLE);
                typeThree.setVisibility(View.GONE);
                for (int i = 0; i < stageChanels.size(); i++) {
                    Log.e("fred",stageChanels.get(i).image);
                    if (stageChanels.get(i).no.equals(planCaseListBean.getT1())) {
                        //String str2=str.replace(" ", "")
                        String[] split = stageChanels.get(i).image.split(",");
                        String base64=split[1];
                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivOne.setImageBitmap(decodedByte);
                    }
                    if (stageChanels.get(i).no.equals(planCaseListBean.getT2())) {
                        String[] split = stageChanels.get(i).image.split(",");
                        String base64=split[1];
                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivTwo.setImageBitmap(decodedByte);
                    }
                    if (stageChanels.get(i).no.equals(planCaseListBean.getT3())) {
                        String[] split = stageChanels.get(i).image.split(",");
                        String base64=split[1];
                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivthree.setImageBitmap(decodedByte);
                    }
                    if (stageChanels.get(i).no.equals(planCaseListBean.getT4())) {
                        String[] split = stageChanels.get(i).image.split(",");
                        String base64=split[1];
                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivFour.setImageBitmap(decodedByte);
                    }
                    if (stageChanels.get(i).no.equals(planCaseListBean.getT5())) {
                        String[] split = stageChanels.get(i).image.split(",");
                        String base64=split[1];
                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivFive.setImageBitmap(decodedByte);
                    }
                    if (stageChanels.get(i).no.equals(planCaseListBean.getT6())) {
                        String[] split = stageChanels.get(i).image.split(",");
                        String base64=split[1];
                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivSix.setImageBitmap(decodedByte);
                    }
                    if (stageChanels.get(i).no.equals(planCaseListBean.getT7())) {
                        String[] split = stageChanels.get(i).image.split(",");
                        String base64=split[1];
                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivSeven.setImageBitmap(decodedByte);
                    }
                    if (stageChanels.get(i).no.equals(planCaseListBean.getT8())) {
                        String[] split = stageChanels.get(i).image.split(",");
                        String base64=split[1];
                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivEight.setImageBitmap(decodedByte);
                    }
                    if (stageChanels.get(i).no.equals(planCaseListBean.getT9())) {
                        String[] split = stageChanels.get(i).image.split(",");
                        String base64=split[1];
                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivNine.setImageBitmap(decodedByte);
                    }
                    if (stageChanels.get(i).no.equals(planCaseListBean.getT10())) {
                        String[] split = stageChanels.get(i).image.split(",");
                        String base64=split[1];
                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivTen.setImageBitmap(decodedByte);
                    }
                }
            } else {
                typeOne.setVisibility(View.GONE);
                typeTwo.setVisibility(View.GONE);
                typeThree.setVisibility(View.VISIBLE);

                programme_one.setText(planCaseListBean.getT1());
                programme_two.setText(planCaseListBean.getT2());
                programme_three.setText(planCaseListBean.getT3());
                programme_four.setText(planCaseListBean.getT4());
                programme_five.setText(planCaseListBean.getT5());
                programme_six.setText(planCaseListBean.getT6());
                programme_seven.setText(planCaseListBean.getT7());
                programme_eight.setText(planCaseListBean.getT8());
                programme_nine.setText(planCaseListBean.getT9());
                programme_ten.setText(planCaseListBean.getT10());
                //类型（1 阶段 2绿闪 3黄灯 4全红 5红黄 6 最大绿 7 最小绿
                if(planCaseListBean.getType().equals("2")){
                    titleId.setText("绿闪");
                }else if(planCaseListBean.getType().equals("3")){
                    titleId.setText("黄灯");
                }else if(planCaseListBean.getType().equals("4")){
                    titleId.setText("全红");
                }else if(planCaseListBean.getType().equals("5")){
                    titleId.setText("红黄");
                }else if(planCaseListBean.getType().equals("6")){
                    titleId.setText("最大绿");
                }else if(planCaseListBean.getType().equals("7")){
                    titleId.setText("最小绿");
                }
            }
        }
    }

    class TimeCaseAdapter extends BaseRecyclerAdapter<TimeCaseListBean> {

        @Override
        public int bindView(int viewType) {
            return R.layout.layout_timing_table_item;
        }

        @Override
        public void onBindHolder(BaseViewHolder holder, @Nullable TimeCaseListBean timeCaseListBean, int index) {
            LinearLayout typeOne = holder.obtainView(R.id.type_one);
            LinearLayout typeTwo = holder.obtainView(R.id.type_two);
            LinearLayout typeThree = holder.obtainView(R.id.type_three);

            ImageView ivOne = holder.obtainView(R.id.iv_one);
            ImageView ivTwo = holder.obtainView(R.id.iv_two);
            ImageView ivthree = holder.obtainView(R.id.iv_three);
            ImageView ivFour = holder.obtainView(R.id.iv_four);
            ImageView ivFive = holder.obtainView(R.id.iv_five);
            ImageView ivSix = holder.obtainView(R.id.iv_six);
            ImageView ivSeven = holder.obtainView(R.id.iv_seven);
            ImageView ivEight = holder.obtainView(R.id.iv_eight);
            ImageView ivNine = holder.obtainView(R.id.iv_nine);
            ImageView ivTen = holder.obtainView(R.id.iv_ten);

            TextView titleId = holder.obtainView(R.id.title_id);
            TextView programme_one = holder.obtainView(R.id.programme_one);
            TextView programme_two = holder.obtainView(R.id.programme_two);
            TextView programme_three = holder.obtainView(R.id.programme_three);
            TextView programme_four = holder.obtainView(R.id.programme_four);
            TextView programme_five = holder.obtainView(R.id.programme_five);
            TextView programme_six = holder.obtainView(R.id.programme_six);
            TextView programme_seven = holder.obtainView(R.id.programme_seven);
            TextView programme_eight = holder.obtainView(R.id.programme_eight);
            TextView programme_nine = holder.obtainView(R.id.programme_nine);
            TextView programme_ten = holder.obtainView(R.id.programme_ten);

//            if (index == 0) {
//                typeOne.setVisibility(View.VISIBLE);
//                typeTwo.setVisibility(View.GONE);
//                typeThree.setVisibility(View.GONE);
//            } else {
                typeOne.setVisibility(View.GONE);
                typeTwo.setVisibility(View.GONE);
                typeThree.setVisibility(View.VISIBLE);

                programme_one.setText(timeCaseListBean.getT1());
                programme_two.setText(timeCaseListBean.getT2());
                programme_three.setText(timeCaseListBean.getT3());
                programme_four.setText(timeCaseListBean.getT4());
                programme_five.setText(timeCaseListBean.getT5());
                programme_six.setText(timeCaseListBean.getT6());
                programme_seven.setText(timeCaseListBean.getT7());
                programme_eight.setText(timeCaseListBean.getT8());
                programme_nine.setText(timeCaseListBean.getT9());
                programme_ten.setText(timeCaseListBean.getT10());

                titleId.setText(timeCaseListBean.getNo());
            //}
        }
    }
}
