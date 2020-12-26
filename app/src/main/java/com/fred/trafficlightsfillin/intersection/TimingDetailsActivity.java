package com.fred.trafficlightsfillin.intersection;

import android.os.Build;
import android.os.Bundle;
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
import com.fred.trafficlightsfillin.base.CustomLayoutManager;
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
import com.fred.trafficlightsfillin.utils.DialogUtils;
import com.fred.trafficlightsfillin.utils.SharedPreferenceUtils;
import com.fred.trafficlightsfillin.utils.StageDataUtil;
import com.fred.trafficlightsfillin.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    @BindView(R.id.cad_img_lable)
    TextView cadImgLable;
    @BindView(R.id.picture_list)
    RecyclerView pictureList;

    @BindView(R.id.weekday_title)
    TextView weekdayTitle;
    @BindView(R.id.noweekday_title)
    TextView noweekdayTitle;

    @BindView(R.id.last_peishi_time)
    TextView lastPeishiTime;
    @BindView(R.id.type_one)
    LinearLayout typeOne;
    @BindView(R.id.scrollView_two)
    HorizontalScrollView scrollViewTwo;
    @BindView(R.id.scrollView)
    NestedScrollView scrollView;
    @BindView(R.id.layout_hide_tab)
    RecyclerView layoutHideTab;
    @BindView(R.id.layout_hide_scrollview)
    HorizontalScrollView layoutHideScrollview;
    @BindView(R.id.scrollView_one)
    HorizontalScrollView scrollViewOne;
    List<ImageResponse.ImageBean> imageBeans=new ArrayList<>();

    private List<PeriodCaseListBean> weekdaysPeriodCaseDataList = new ArrayList<>();//工作日时间表
    private List<PeriodCaseListBean> noWeekDaysPeriodCaseDataList = new ArrayList<>();//周末时间表

    private List<PlanCaseListBean> weekdaysPlanCaseDataList = new ArrayList<>();//工作日配时表1
    private List<PlanCaseListBean> noWeekdaysPlanCaseDataList = new ArrayList<>();//周末配时表1
    private List<PlanCaseListBean> hideWeekdaysPlanCaseList = new ArrayList<>();

    private List<TimeCaseListBean> weekdaysTimeCaseDataList = new ArrayList<>();//工作日配时表1
    private List<TimeCaseListBean> noWeekdaysTimeCaseDataList = new ArrayList<>();//周末配时表1


    TimingDetailsActivity.PictureAdapter pictureAdapter;
    TimingDetailsActivity.TimeTableAdapter timeTableAdapter;
    long trafficLightId;
    TimingDetailsActivity.PlanCaseAdapter planCaseDataAdapter, hidePlanCaseDataAdapter;
    TimingDetailsActivity.TimeCaseAdapter timeCaseDataAdapter;


    boolean hasNoWeekDay = false;//是否区分工作日
    boolean isWeekday = true;
    int scrollviewHigh=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timing_details);
        ButterKnife.bind(this);

        trafficLightId = getIntent().getLongExtra("trafficLightId", 0);
        initView();

        initTrafficlighInfo();
        initPictrue();
        initTrafficlighPeishi();
    }

    /**
     * 加载基础布局
     */
    private void initView() {
        LinearLayoutManager layoutManager = new CustomLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        pictureList.setLayoutManager(layoutManager);
        pictureAdapter = new TimingDetailsActivity.PictureAdapter();
        pictureList.setAdapter(pictureAdapter);

        pictureAdapter.setOnItemClickListener((adapter, holder, itemView, index) -> {
            DialogUtils.showPictureDialog(TimingDetailsActivity.this,imageBeans , index,1, new DialogUtils.OnButtonClickListener() {

                @Override
                public void onPositiveButtonClick() {

                }

                @Override
                public void onNegativeButtonClick() {

                }

                @Override
                public void onChoiceItem(String str, int pos) {
                }
            });
        });

        timeList.setLayoutManager(new CustomLayoutManager(this));
        timeTableAdapter = new TimingDetailsActivity.TimeTableAdapter();
        timeList.setAdapter(timeTableAdapter);

        planCaseDataAdapter = new TimingDetailsActivity.PlanCaseAdapter();
        programme.setLayoutManager(new CustomLayoutManager(this));
        programme.setAdapter(planCaseDataAdapter);

        hidePlanCaseDataAdapter = new TimingDetailsActivity.PlanCaseAdapter();
        layoutHideTab.setLayoutManager(new CustomLayoutManager(this));
        layoutHideTab.setAdapter(hidePlanCaseDataAdapter);

        timeCaseDataAdapter = new TimingDetailsActivity.TimeCaseAdapter();
        timetable.setLayoutManager(new CustomLayoutManager(this));
        timetable.setAdapter(timeCaseDataAdapter);

        //监听
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                //Log.e("fred", "%%%%     " + scrollY);
                if (scrollY > 1190) {//滑动距离大于v_report_divider的底坐标
                    layoutHideScrollview.setVisibility(View.VISIBLE);
                } else {
                    layoutHideScrollview.setVisibility(View.GONE);
                }
                scrollviewHigh=scrollY;
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollViewOne.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                scrollViewTwo.scrollTo(scrollX, scrollY);
                layoutHideScrollview.scrollTo(scrollX, scrollY);
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layoutHideScrollview.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                scrollViewOne.scrollTo(scrollX, scrollY);
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollViewTwo.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                layoutHideScrollview.scrollTo(scrollX, scrollY);
                scrollViewOne.scrollTo(scrollX, scrollY);
                //scrollViewTwo.requestFocus();
            });
        }
        //工作日/不区分工作日
        weekdayTitle.setOnClickListener(view -> {
            isWeekday = true;
            weekdayTitle.setBackground(getResources().getDrawable(R.drawable.bg_color_blue_gray_stroke_main));
            noweekdayTitle.setBackground(getResources().getDrawable(R.drawable.bg_color_blue_gray_stroke));
            timeTableAdapter.bindData(true, weekdaysPeriodCaseDataList);
            planCaseDataAdapter.bindData(true, weekdaysPlanCaseDataList);
            timeCaseDataAdapter.bindData(true, weekdaysTimeCaseDataList);
        });
        //周日
        noweekdayTitle.setOnClickListener(view -> {
            isWeekday = false;
            noweekdayTitle.setBackground(getResources().getDrawable(R.drawable.bg_color_blue_gray_stroke_main));
            weekdayTitle.setBackground(getResources().getDrawable(R.drawable.bg_color_blue_gray_stroke));
            timeTableAdapter.bindData(true, noWeekDaysPeriodCaseDataList);
            planCaseDataAdapter.bindData(true, noWeekdaysPlanCaseDataList);
            timeCaseDataAdapter.bindData(true, noWeekdaysTimeCaseDataList);
        });
    }

    /**
     * 获取基础信息
     */
    private void initTrafficlighInfo() {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TRAFFICLIGH_DETAILS) + "/" + trafficLightId)
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh-token", SharedPreferenceUtils.getInstance().getrefreshToken())
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
                            if (response.date != null && !"".equals(response.date.trim()))
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
                .addHeader("refresh-token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .getAsync(new ICallback<ImageResponse>() {
                    @Override
                    public void onSuccess(ImageResponse response) {
                        if (response.code == 0) {

                            if(response.data == null || response.data.size() == 0){
                                return;
                            }
                            cadImgLable.setVisibility(View.VISIBLE);

                            imageBeans = response.data;
                            pictureAdapter.bindData(true, response.data);
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

            String pictureUrl = RequestApi.BASE_OFFICIAL_URL + RequestApi.DOWN_IMG + "/" + imageBean.path;

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
                .addHeader("refresh-token", SharedPreferenceUtils.getInstance().getrefreshToken())
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
                        Collections.sort(planCaseList, new Comparator<PlanCaseListBean>() {
                            @Override
                            public int compare(PlanCaseListBean o1, PlanCaseListBean o2) {
                                try{
                                    Integer i1 = Integer.parseInt(o1.getType());
                                    Integer i2 = Integer.parseInt(o2.getType());
                                    return i1 - i2;
                                }catch (Exception e){
                                    return 0;
                                }
                            }
                        });
                        List<TimeCaseListBean> timeCaseList = data.getTimeCaseList();

                        for (PlanCaseListBean planCaseListBean : planCaseList
                        ) {
                            if ("0".equals(planCaseListBean.getWorkday())) {//周末
                                if(!hasNoWeekDay && !"1".equals(planCaseListBean.getType()))
                                    hasNoWeekDay = true;//标记有区分工作
                                noWeekdaysPlanCaseDataList.add(planCaseListBean);
                            } else {
                                weekdaysPlanCaseDataList.add(planCaseListBean);
                                if ("1".equals(planCaseListBean.getType())) {
                                    hideWeekdaysPlanCaseList.add(planCaseListBean);
                                }
                            }
                        }

                        for (TimeCaseListBean timeCaseListBean : timeCaseList
                        ) {
                            if (hasNoWeekDay && "0".equals(timeCaseListBean.getWorkday())) {//周末
                                noWeekdaysTimeCaseDataList.add(timeCaseListBean);
                            } else {
                                weekdaysTimeCaseDataList.add(timeCaseListBean);
                            }
                        }

                        for (PeriodCaseListBean periodCaseListBean : periodCaseList
                        ) {
                            if (hasNoWeekDay && "0".equals(periodCaseListBean.getWorkday())) {//周末
                                noWeekDaysPeriodCaseDataList.add(periodCaseListBean);
                            } else {
                                weekdaysPeriodCaseDataList.add(periodCaseListBean);
                            }
                        }

                        if (!hasNoWeekDay) {
                            weekdayTitle.setText("配时信息");
                            weekdayTitle.setVisibility(View.VISIBLE);
                        } else {
                            weekdayTitle.setVisibility(View.VISIBLE);
                            noweekdayTitle.setVisibility(View.VISIBLE);
                        }


                        // Log.e("fred", weekdaysPeriodCaseList.size() + "  数量**");

                        if (timeTableAdapter == null) {
                            timeTableAdapter = new TimingDetailsActivity.TimeTableAdapter();
                        }

                        if (planCaseDataAdapter == null) {
                            planCaseDataAdapter = new TimingDetailsActivity.PlanCaseAdapter();
                        }

                        if (timeCaseDataAdapter == null) {
                            timeCaseDataAdapter = new TimingDetailsActivity.TimeCaseAdapter();
                        }

                        hidePlanCaseDataAdapter.bindData(true, hideWeekdaysPlanCaseList);
                        timeTableAdapter.bindData(true, weekdaysPeriodCaseDataList);
                        planCaseDataAdapter.bindData(true, weekdaysPlanCaseDataList);
                        timeCaseDataAdapter.bindData(true, weekdaysTimeCaseDataList);
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
            return R.layout.layout_time_detail_table_item;
        }

        @Override
        public void onBindHolder(BaseViewHolder holder, @Nullable PeriodCaseListBean periodCaseListBean, int index) {
            TextView startTime = holder.obtainView(R.id.start_time);
            TextView no = holder.obtainView(R.id.number);

            startTime.setText(periodCaseListBean.getStart());
            no.setText(periodCaseListBean.getTimeCaseNo());
        }
    }

    /**
     * 配时方案1 adapter
     */
    class PlanCaseAdapter extends BaseRecyclerAdapter<PlanCaseListBean> {

        @Override
        public int bindView(int viewType) {
            return R.layout.layout_timing_detail_table_item;
        }

        @Override
        public void onBindHolder(BaseViewHolder holder, @Nullable PlanCaseListBean planCaseListBean, int index) {
            LinearLayout typeOne = holder.obtainView(R.id.type_one);
            LinearLayout typeTwo = holder.obtainView(R.id.type_two);
            LinearLayout typeThree = holder.obtainView(R.id.type_three);

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
                ImageView ivOne = holder.obtainView(R.id.iv_one);
                if(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT1()) != null){
                    ivOne.setImageBitmap(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT1()));
                }
                ImageView ivTwo = holder.obtainView(R.id.iv_two);
                if(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT2()) != null){
                    ivTwo.setImageBitmap(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT2()));
                }
                ImageView ivthree = holder.obtainView(R.id.iv_three);
                if(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT3()) != null){
                    ivthree.setImageBitmap(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT3()));
                }
                ImageView ivFour = holder.obtainView(R.id.iv_four);
                if(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT4()) != null){
                    ivFour.setImageBitmap(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT4()));
                }
                ImageView ivFive = holder.obtainView(R.id.iv_five);
                if(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT5()) != null){
                    ivFive.setImageBitmap(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT5()));
                }
                ImageView ivSix = holder.obtainView(R.id.iv_six);
                if(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT6()) != null){
                    ivSix.setImageBitmap(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT6()));
                }
                ImageView ivSeven = holder.obtainView(R.id.iv_seven);
                if(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT7()) != null){
                    ivSeven.setImageBitmap(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT7()));
                }
                ImageView ivEight = holder.obtainView(R.id.iv_eight);
                if(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT8()) != null){
                    ivEight.setImageBitmap(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT8()));
                }
                ImageView ivNine = holder.obtainView(R.id.iv_nine);
                if(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT9()) != null){
                    ivNine.setImageBitmap(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT9()));
                }
                ImageView ivTen = holder.obtainView(R.id.iv_ten);
                if(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT10()) != null){
                    ivTen.setImageBitmap(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT10()));
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
                if (planCaseListBean.getType().equals("2")) {
                    titleId.setText("绿闪");
                } else if (planCaseListBean.getType().equals("3")) {
                    titleId.setText("黄灯");
                } else if (planCaseListBean.getType().equals("4")) {
                    titleId.setText("全红");
                } else if (planCaseListBean.getType().equals("5")) {
                    titleId.setText("红黄");
                } else if (planCaseListBean.getType().equals("6")) {
                    titleId.setText("最大绿");
                } else if (planCaseListBean.getType().equals("7")) {
                    titleId.setText("最小绿");
                }
            }
        }
    }

    class TimeCaseAdapter extends BaseRecyclerAdapter<TimeCaseListBean> {

        @Override
        public int bindView(int viewType) {
            return R.layout.layout_timing_detail_table_item;
        }

        @Override
        public void onBindHolder(BaseViewHolder holder, @Nullable TimeCaseListBean timeCaseListBean, int index) {
            LinearLayout typeOne = holder.obtainView(R.id.type_one);
            LinearLayout typeTwo = holder.obtainView(R.id.type_two);
            LinearLayout typeThree = holder.obtainView(R.id.type_three);

            typeOne.setVisibility(View.GONE);
            typeTwo.setVisibility(View.GONE);
            typeThree.setVisibility(View.VISIBLE);

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
        }
    }
}
