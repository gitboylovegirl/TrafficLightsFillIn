package com.fred.trafficlightsfillin.intersection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import com.fred.trafficlightsfillin.base.BaseResponse;
import com.fred.trafficlightsfillin.base.BaseViewHolder;
import com.fred.trafficlightsfillin.base.CustomHorizontalScrollView;
import com.fred.trafficlightsfillin.base.CustomLayoutManager;
import com.fred.trafficlightsfillin.base.CustomScrollView;
import com.fred.trafficlightsfillin.base.MyGlideEngine;
import com.fred.trafficlightsfillin.base.RequestApi;
import com.fred.trafficlightsfillin.intersection.bean.ImageResponse;
import com.fred.trafficlightsfillin.intersection.bean.PeriodCaseListBean;
import com.fred.trafficlightsfillin.intersection.bean.PlanCaseListBean;
import com.fred.trafficlightsfillin.intersection.bean.SubmitBean;
import com.fred.trafficlightsfillin.intersection.bean.TaskResultResponse;
import com.fred.trafficlightsfillin.intersection.bean.TimeCaseListBean;
import com.fred.trafficlightsfillin.intersection.bean.TimingDetailsResponse;
import com.fred.trafficlightsfillin.intersection.bean.TrafficlighResonse;
import com.fred.trafficlightsfillin.network.http.ProRequest;
import com.fred.trafficlightsfillin.network.http.response.ICallback;
import com.fred.trafficlightsfillin.record.bean.TaskDetailsChannel;
import com.fred.trafficlightsfillin.utils.DialogUtils;
import com.fred.trafficlightsfillin.utils.GetImagePath;
import com.fred.trafficlightsfillin.utils.SharedPreferenceUtils;
import com.fred.trafficlightsfillin.utils.SoftKeyBoardListener;
import com.fred.trafficlightsfillin.utils.StageDataUtil;
import com.fred.trafficlightsfillin.utils.TimeUtils;
import com.fred.trafficlightsfillin.utils.ToastUtil;
import com.google.gson.Gson;
import com.ikovac.timepickerwithseconds.MyTimePickerDialog;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * 配时编辑
 */
public class TimingEditorActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CHOOSE = 99;
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
    @BindView(R.id.end_time)
    TextView endTime;
    @BindView(R.id.submit)
    TextView submit;
    @BindView(R.id.weekday)
    TextView weekday;
    @BindView(R.id.weekend)
    TextView weekend;
    @BindView(R.id.week_title)
    LinearLayout weekTitle;
    @BindView(R.id.task_status)
    TextView taskStatus;
    @BindView(R.id.type_one)
    LinearLayout typeOne;
    @BindView(R.id.better)
    EditText better;
    @BindView(R.id.time_list_add)
    TextView timeListAdd;
    @BindView(R.id.timetable_add)
    TextView timetableAdd;
    @BindView(R.id.scrollView)
    CustomScrollView scrollView;
    @BindView(R.id.layout_hide_tab)
    RecyclerView layoutHideTab;
    @BindView(R.id.layout_hide_scrollview)
    CustomHorizontalScrollView layoutHideScrollview;
    @BindView(R.id.scrollView_one)
    CustomHorizontalScrollView scrollViewOne;
    @BindView(R.id.scrollView_two)
    CustomHorizontalScrollView scrollViewTwo;

    private View popuwindowView;
    private PopupWindow popupWindow;
    private EditText currentEditView;

    EditText inputComment;

    private List<PeriodCaseListBean> weekdaysPeriodCaseDataList = new ArrayList<>();//工作日时间表
    private List<PeriodCaseListBean> noWeekDaysPeriodCaseDataList = new ArrayList<>();//周末时间表


    private List<PlanCaseListBean> weekdaysPlanCaseDataList = new ArrayList<>();//工作日配时表1
    private List<PlanCaseListBean> noWeekdaysPlanCaseDataList = new ArrayList<>();//周末配时表1
    private List<PlanCaseListBean> hideWeekdaysPlanCaseList = new ArrayList<>();

    private List<TimeCaseListBean> weekdaysTimeCaseDataList = new ArrayList<>();//工作日配时表1
    private List<TimeCaseListBean> noWeekdaysTimeCaseDataList = new ArrayList<>();//周末配时表1

    PictureAdapter pictureAdapter;
    TimeTableAdapter timeTableAdapter;
    String trafficLightId;
    String id;
    List<ImageResponse.ImageBean> imageBeans = new ArrayList<>();
    PlanCaseAdapter planCaseDataAdapter, hidePlanCaseDataAdapter;
    TimeCaseAdapter timeCaseDataAdapter;

    boolean hasNoWeekDay = false;//是否区分工作日
    boolean isWeekday = true;
    boolean isShow = false;

    boolean isFocus=false;
    int keyHigh=831;
    int scrollviewHigh=0;

    private boolean editT1 = true;
    private boolean editT2 = true;
    private boolean editT3 = true;
    private boolean editT4 = true;
    private boolean editT5 = true;
    private boolean editT6 = true;
    private boolean editT7 = true;
    private boolean editT8 = true;
    private boolean editT9 = true;
    private boolean editT10 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timing_editor);
        ButterKnife.bind(this);

        trafficLightId = getIntent().getStringExtra("trafficLightId");
        id = getIntent().getStringExtra("id");
        initView();

        initTrafficlighInfo();
        initTaskInfo();
        initPictrue();
        initTrafficlighPeishi();
    }

    /**
     * 加载基础布局
     */
    private void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (TimingEditorActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                //如果没有写sd卡权限
                TimingEditorActivity.this.requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        100);
            }
        }
        LinearLayoutManager layoutManager = new CustomLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        pictureList.setLayoutManager(layoutManager);
        pictureAdapter = new PictureAdapter();
        pictureList.setAdapter(pictureAdapter);

        timeList.setLayoutManager(new CustomLayoutManager(this));
        timeTableAdapter = new TimeTableAdapter();
        timeList.setAdapter(timeTableAdapter);

        planCaseDataAdapter = new PlanCaseAdapter();
        programme.setLayoutManager(new CustomLayoutManager(this));
        programme.setAdapter(planCaseDataAdapter);

        hidePlanCaseDataAdapter = new PlanCaseAdapter();
        layoutHideTab.setLayoutManager(new CustomLayoutManager(this));
        layoutHideTab.setAdapter(hidePlanCaseDataAdapter);

        timeCaseDataAdapter = new TimeCaseAdapter();
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
        weekday.setOnClickListener(view -> {
            isWeekday = true;
            weekday.setBackground(getResources().getDrawable(R.drawable.bg_color_blue_gray_stroke_main));
            weekend.setBackground(getResources().getDrawable(R.drawable.bg_color_blue_gray_stroke));
            timeTableAdapter.bindData(true, weekdaysPeriodCaseDataList);
            planCaseDataAdapter.bindData(true, weekdaysPlanCaseDataList);
            timeCaseDataAdapter.bindData(true, weekdaysTimeCaseDataList);
        });
        //周日
        weekend.setOnClickListener(view -> {
            isWeekday = false;
            weekend.setBackground(getResources().getDrawable(R.drawable.bg_color_blue_gray_stroke_main));
            weekday.setBackground(getResources().getDrawable(R.drawable.bg_color_blue_gray_stroke));
            timeTableAdapter.bindData(true, noWeekDaysPeriodCaseDataList);
            planCaseDataAdapter.bindData(true, noWeekdaysPlanCaseDataList);
            timeCaseDataAdapter.bindData(true, noWeekdaysTimeCaseDataList);
        });

        /**
         * 点击提交
         */
        submit.setOnClickListener(v -> {
            SubmitBean submitBean = new SubmitBean();
            submitBean.setDate(Long.parseLong(TimeUtils.time11(endTime.getText().toString())));
            submitBean.setRemark(better.getText().toString());
            submitBean.setTaskId(Integer.parseInt(id));

            List<PeriodCaseListBean> periodCaseList = new ArrayList<>();
            if(weekdaysPeriodCaseDataList != null && weekdaysPeriodCaseDataList.size() > 0)
                periodCaseList.addAll(weekdaysPeriodCaseDataList);
            if(noWeekDaysPeriodCaseDataList != null && noWeekDaysPeriodCaseDataList.size() > 0)
                periodCaseList.addAll(noWeekDaysPeriodCaseDataList);
            submitBean.setPeriodCaseList(periodCaseList);


            List<PlanCaseListBean> planCaseList = new ArrayList<>();
            if(weekdaysPlanCaseDataList != null && weekdaysPlanCaseDataList.size() > 0)
                planCaseList.addAll(weekdaysPlanCaseDataList);
            if(noWeekdaysPlanCaseDataList != null && noWeekdaysPlanCaseDataList.size() > 0)
                planCaseList.addAll(noWeekdaysPlanCaseDataList);
            submitBean.setPlanCaseList(planCaseList);

            List<TimeCaseListBean> timeCaseList = new ArrayList<>();
            if(weekdaysTimeCaseDataList != null && weekdaysTimeCaseDataList.size() > 0)
                timeCaseList.addAll(weekdaysTimeCaseDataList);
            if(noWeekdaysTimeCaseDataList != null && noWeekdaysTimeCaseDataList.size() > 0)
                timeCaseList.addAll(noWeekdaysTimeCaseDataList);
            submitBean.setTimeCaseList(timeCaseList);

            Gson gson = new Gson();
            DialogUtils.showCurrencyDialog(this, "是否确认上传？", new DialogUtils.OnButtonClickListener() {
                @Override
                public void onPositiveButtonClick() {
                    submitTaskResult(gson.toJson(submitBean));
                }

                @Override
                public void onNegativeButtonClick() {

                }

                @Override
                public void onChoiceItem(String str, int pos) {

                }
            });
        });

        better.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 1) {
                    taskStatus.setText("调整后");
                }
            }
        });

        //时间表新增
        timeListAdd.setOnClickListener(v -> {
            Gson gson = new Gson();
            List<PeriodCaseListBean> addPeriodCaseList = new ArrayList<>();
            if (isWeekday) {
                PeriodCaseListBean newBean = weekdaysPeriodCaseDataList != null && weekdaysPeriodCaseDataList.size() > 0 ? weekdaysPeriodCaseDataList.get(weekdaysPeriodCaseDataList.size() -1) : new PeriodCaseListBean();
                if(newBean == null){
                    newBean = new PeriodCaseListBean();
                    if(hasNoWeekDay)
                        newBean.setWorkday("1");//工作日
                    else
                        newBean.setWorkday("-1");//不区分工作日
                }else{
                    newBean = gson.fromJson(gson.toJson(newBean), PeriodCaseListBean.class);
                }
                addPeriodCaseList.add(newBean);
                timeTableAdapter.bindData(false, addPeriodCaseList);
                weekdaysPeriodCaseDataList.addAll(addPeriodCaseList);
            } else {
                PeriodCaseListBean newBean = noWeekDaysPeriodCaseDataList != null && noWeekDaysPeriodCaseDataList.size() > 0 ? noWeekDaysPeriodCaseDataList.get(noWeekDaysPeriodCaseDataList.size() -1) : new PeriodCaseListBean();
                if(newBean == null){
                    newBean = new PeriodCaseListBean();
                    newBean.setWorkday("0");//周六日
                }else{
                    newBean = gson.fromJson(gson.toJson(newBean), PeriodCaseListBean.class);
                }
                addPeriodCaseList.add(newBean);
                timeTableAdapter.bindData(false, addPeriodCaseList);
                noWeekDaysPeriodCaseDataList.addAll(addPeriodCaseList);
            }
            setTaskState();

        });

        /**
         * 配时表新增
         */
        timetableAdd.setOnClickListener(v -> {
            Gson gson = new Gson();
            List<TimeCaseListBean> addTimeCaseList = new ArrayList<>();
            if (isWeekday) {
                TimeCaseListBean newBean = weekdaysTimeCaseDataList != null && weekdaysTimeCaseDataList.size() > 0 ? weekdaysTimeCaseDataList.get(weekdaysTimeCaseDataList.size() -1) : new TimeCaseListBean();
                if(newBean == null){
                    newBean = new TimeCaseListBean();
                    if(hasNoWeekDay)
                        newBean.setWorkday("1");//工作日
                    else
                        newBean.setWorkday("-1");//不区分工作日
                }else{
                    newBean = gson.fromJson(gson.toJson(newBean), TimeCaseListBean.class);
                }
                addTimeCaseList.add(newBean);
                timeCaseDataAdapter.bindData(false, addTimeCaseList);
                weekdaysTimeCaseDataList.addAll(addTimeCaseList);
            } else {
                TimeCaseListBean newBean = noWeekdaysTimeCaseDataList != null && noWeekdaysTimeCaseDataList.size() > 0 ? noWeekdaysTimeCaseDataList.get(noWeekdaysTimeCaseDataList.size() -1) : new TimeCaseListBean();
                if(newBean == null){
                    newBean = new TimeCaseListBean();
                    newBean.setWorkday("0");//周六日
                }else{
                    newBean = gson.fromJson(gson.toJson(newBean), TimeCaseListBean.class);
                }
                addTimeCaseList.add(newBean);
                timeCaseDataAdapter.bindData(false, addTimeCaseList);
                noWeekdaysTimeCaseDataList.addAll(addTimeCaseList);
            }
            setTaskState();
        });

        endTime.setText(TimeUtils.time10(String.valueOf(System.currentTimeMillis())));

        //键盘显示监听
        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                keyHigh=height;
                isFocus=true;
                if (isShow) {
                    showPopupCommnet(height);
                    inputComment.requestFocus();
                }

                currentEditView.setBackgroundResource(R.color.select_bg_color);
                currentEditView.setTextColor(Color.parseColor("#F70909"));
                currentEditView.setText("");
            }

            @Override
            public void keyBoardHide(int height) {
                isFocus=false;
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        });
    }

    /**
     * 上传任务结果
     */
    private void submitTaskResult(String json) {
        Log.e("json", json.toString());
        RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TASK_RESULT))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .setBody(body)
                .build()
                .postBodyAsync(new ICallback<TaskResultResponse>() {
                    @Override
                    public void onSuccess(TaskResultResponse response) {
                        if (response.code == 0) {
                            finish();
                        }
                        ToastUtil.showMsg(TimingEditorActivity.this, response.msg);
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
                            //Log.e("fred", response.toString());
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
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TRAFFICLIGH_IMAGES) + "/" + trafficLightId)
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .getAsync(new ICallback<ImageResponse>() {
                    @Override
                    public void onSuccess(ImageResponse response) {
                        if (response.code == 0) {
                            imageBeans = response.data;
                            imageBeans.add(imageBeans.size(), new ImageResponse.ImageBean());
                            //Log.e("fred", imageBeans.size() + "  数量");
                            if (pictureAdapter == null) {
                                pictureAdapter = new PictureAdapter();
                            }
                            pictureAdapter.bindData(true, imageBeans);
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
                        //Log.e("fred  新数据：", response.toString());
                        if (response.data != null) {
                            TaskDetailsChannel.TaskDetails taskDetails = response.data;
//                            if ("0".equals(taskDetails.state)) {
//                                state.setText("后台取消");
//                            } else if ("1".equals(taskDetails.state)) {
//                                state.setText("未接单");
//                            } else if ("2".equals(taskDetails.state)) {
//                                state.setText("未完成");
//                            } else if ("3".equals(taskDetails.state)) {
//                                state.setText("配时表未更新");
//                            } else if ("4".equals(taskDetails.state)) {
//                                state.setText("完成已上传");
//                            }

                            if (TextUtils.isEmpty(taskDetails.cause)) {
                                better.setText("无");
                            } else {
                                better.setText(taskDetails.cause);
                            }
                            //endTime.setText(TimeUtils.time10(taskDetails.updateTime));
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
            RelativeLayout rlPicture = holder.obtainView(R.id.rl_picture);
            RelativeLayout rlMore = holder.obtainView(R.id.rl_more);
            ImageView picture = holder.obtainView(R.id.iv_picture);
            ImageView iv_delete = holder.obtainView(R.id.iv_delete);

            if (index == imageBeans.size() - 1) {
                rlMore.setVisibility(View.VISIBLE);
                rlPicture.setVisibility(View.GONE);
            } else {
                rlMore.setVisibility(View.GONE);
                rlPicture.setVisibility(View.VISIBLE);
                iv_delete.setVisibility(View.VISIBLE);

                //删除
                iv_delete.setOnClickListener(v -> {
                    delPicture(imageBeans.get(index).id);
                    imageBeans.remove(index);
                    pictureAdapter.bindData(true, imageBeans);
                    pictureAdapter.notifyDataSetChanged();
                });

                String pictureUrl = RequestApi.BASE_OFFICIAL_URL + RequestApi.DOWN_IMG + "/" + imageBean.path;
                if (imageBean.getUri() != null && !TextUtils.isEmpty(imageBean.getUri().toString())) {
                    String filePath = GetImagePath.getPath(TimingEditorActivity.this, imageBean.getUri());
                    Bitmap bm = GetImagePath.displayImage(TimingEditorActivity.this, filePath);
                    if (bm == null) {
                        ToastUtil.showShort(TimingEditorActivity.this, "获取图片失败");
                        return;
                    }
                    picture.setImageBitmap(bm);
                } else {
                    GlideUrl glideUrl = new GlideUrl(pictureUrl, new LazyHeaders.Builder()
                            .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                            .build());

                    Glide.with(TimingEditorActivity.this)
                            .load(glideUrl)
                            .into(picture);
                }
            }

            //点击添加图片
            rlMore.setOnClickListener(v -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (TimingEditorActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                        //如果没有写sd卡权限
                        TimingEditorActivity.this.requestPermissions(
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                100);
                    } else {
                        choicePicture();
                    }
                } else {
                    choicePicture();
                }
            });
        }
    }

    private void choicePicture() {
        // .choose(MimeType.ofImage())
        Matisse.from(TimingEditorActivity.this)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.WEBP))
                .countable(true)
                .maxSelectable(9)
                .gridExpectedSize(400)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new MyGlideEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    /**
     * 图片返回结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            List<Uri> mSelected = Matisse.obtainResult(data);
            List<String> fileData = new ArrayList<>();
            for (int i = 0; i < mSelected.size(); i++) {
                Log.i("图片", mSelected.get(i).getPath());
                fileData.add(getFilePathFromUri(TimingEditorActivity.this, mSelected.get(i)));
                ImageResponse.ImageBean imageBean = new ImageResponse.ImageBean();
                imageBean.setUri(mSelected.get(i));
                imageBeans.add(0, imageBean);
            }
            pictureAdapter.bindData(true, imageBeans);
            pictureAdapter.notifyDataSetChanged();
            uploadPicture(fileData);
        }

    }

    //上传图片
    private void uploadPicture(List<String> data) {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.UP_IMG) + "/" + trafficLightId)
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .addUploadFiles(data)
                .build()
                .uploadFiles(new ICallback<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        if (response.code == 0) {
                            initPictrue();
                            ToastUtil.showMsg(TimingEditorActivity.this, "图片上传成功");
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                        ToastUtil.showMsg(TimingEditorActivity.this, "图片上传失败");
                    }
                });
    }

    //删除图片
    private void delPicture(String imgID) {
        if (TextUtils.isEmpty(imgID)) {
            return;
        }
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.DEL_IMG) + "/" + imgID)
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .deleteAsync(new ICallback<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        if (response.code == 0) {
                            initPictrue();
                            ToastUtil.showMsg(TimingEditorActivity.this, "图片删除成功");
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                        ToastUtil.showMsg(TimingEditorActivity.this, "图片删除失败");
                    }
                });
    }

    public static String getFilePathFromUri(Context context, Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns
                    .DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
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
                            weekTitle.setVisibility(View.GONE);
                        } else {
                            weekTitle.setVisibility(View.VISIBLE);
                        }


                        // Log.e("fred", weekdaysPeriodCaseList.size() + "  数量**");

                        if (timeTableAdapter == null) {
                            timeTableAdapter = new TimeTableAdapter();
                        }

                        if (planCaseDataAdapter == null) {
                            planCaseDataAdapter = new PlanCaseAdapter();
                        }

                        if (timeCaseDataAdapter == null) {
                            timeCaseDataAdapter = new TimeCaseAdapter();
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


    class TimeTableAdapter extends BaseRecyclerAdapter<PeriodCaseListBean> {

        @Override
        public int bindView(int viewType) {
            return R.layout.layout_time_table_item;
        }

        @Override
        public void onBindHolder(BaseViewHolder holder, @Nullable PeriodCaseListBean periodCaseListBean, int index) {
            holder.setIsRecyclable(false);//不使用复用 防止数据多时 复用时  多个item中的EditText填写的数据一样
            TextView startTime = holder.obtainView(R.id.start_time);
            EditText no = holder.obtainView(R.id.number);
            ImageView timetable_delete = holder.obtainView(R.id.timetable_delete);
            timetable_delete.setVisibility(View.VISIBLE);

            startTime.setText(periodCaseListBean.getStart());
            no.setText(periodCaseListBean.getTimeCaseNo());
            no.setOnFocusChangeListener(new EditViewOnFocusChangeListener(new EditViewChangeCallBack(){

                @Override
                public void setValue(String value) {
                    periodCaseListBean.setTimeCaseNo(value);
                }
            }));

            //no.addTextChangedListener(new MyTextWatcher());

            timetable_delete.setOnClickListener(v -> {
                if (isWeekday) {
                    weekdaysPeriodCaseDataList.remove(index);
                    timeTableAdapter.bindData(true, weekdaysPeriodCaseDataList);
                } else {
                    noWeekDaysPeriodCaseDataList.remove(index);
                    timeTableAdapter.bindData(true, noWeekDaysPeriodCaseDataList);
                }
                setTaskState();
            });

            //no.setOnTouchListener(new EditViewOnTouchListener(currentListType, index, 0));

            startTime.setOnClickListener(v -> {
                TextView startTimeTextView = (TextView) v;
                String startTimeStr = startTimeTextView.getText() == null ? "" : startTimeTextView.getText().toString().trim();
                String[] arr = startTimeStr.split(":");
                int hour = 0;
                int min = 0;
                int sec = 0;
                if(arr.length == 3){
                    try{
                        hour = Integer.parseInt(arr[0]);
                        min = Integer.parseInt(arr[1]);
                        sec = Integer.parseInt(arr[2]);
                    }catch (Exception e){}
                }
                MyTimePickerDialog mTimePicker = new MyTimePickerDialog(TimingEditorActivity.this, (view, hourOfDay, minute, seconds) -> {
                    String time = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", seconds);
                    periodCaseListBean.setStart(time);
                    startTime.setText(time);
                    startTime.setTextColor(Color.parseColor("#ff2d51"));
                }, hour, min, sec, true);
                mTimePicker.show();
            });
        }
    }

    /**
     * 显示键盘
     *
     * @param et 输入焦点
     */
    public void showInput(final EditText et) {
        et.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {
                               InputMethodManager inputManager =
                                       (InputMethodManager) inputComment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(inputComment, 0);
                           }

                       },
                200);
    }

    @SuppressLint("WrongConstant")
    private void showPopupCommnet(int height) {
        popuwindowView = LayoutInflater.from(TimingEditorActivity.this).inflate(
                R.layout.layout_tv_bottom_view, null);
        inputComment = (EditText) popuwindowView.findViewById(R.id.number_tv);
        //inputComment.requestFocus();//请求焦点
        showInput(inputComment);
        popupWindow = new PopupWindow(popuwindowView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor((v, event) -> false);
        popupWindow.setFocusable(true);
        // 设置点击窗口外边窗口消失
        popupWindow.setOutsideTouchable(true);

        int[] location = new int[2];
        currentEditView.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int heightPixels = outMetrics.heightPixels;//屏幕高度

        //距离下边界的高度
        int  bottom= heightPixels - y;
        if(keyHigh>bottom){
            //向上滑动
            scrollView.scrollTo(x,scrollviewHigh+(keyHigh-bottom)+150);
        }
        //输入变化监听
        inputComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentEditView.setText(editable.toString().trim());
                //currentEditView.setTextColor(Color.parseColor("#ff2d51"));
                currentEditView.setBackgroundColor(Color.parseColor("#EFEFEF"));

                setTaskState();
            }
        });

        popupWindow.showAtLocation(popuwindowView, Gravity.BOTTOM, 0, 0);
        ColorDrawable cd = new ColorDrawable(0x000000);
        popupWindow.setBackgroundDrawable(cd);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 1.0f;

        getWindow().setAttributes(params);
        // 设置popWindow的显示和消失动画
        popupWindow.setAnimationStyle(R.style.BottomDialogStyle);
        popupWindow.update();
        popupWindow.setOnDismissListener(() -> {
            //隐藏软键盘
            //Toast.makeText(this, "软键盘隐藏" + height, Toast.LENGTH_SHORT).show();
            //liveRoomText.clearFocus();
            hideKeyBoard(this);
            isShow = false;
        });
    }

    /**
     * 隐藏软键盘
     *
     * @param activity
     */
    public static void hideKeyBoard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getAttributes() != null) {
            if (activity.getCurrentFocus() != null && activity.getCurrentFocus().getWindowToken() != null) {
                ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity
                        .getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
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

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindHolder(BaseViewHolder holder, @Nullable PlanCaseListBean planCaseListBean, int index) {
            holder.setIsRecyclable(false);//不使用复用 防止数据多时 复用时  多个item中的EditText填写的数据一样
            LinearLayout typeOne = holder.obtainView(R.id.type_one);
            LinearLayout typeTwo = holder.obtainView(R.id.type_two);
            LinearLayout typeThree = holder.obtainView(R.id.type_three);

            TextView titleId = holder.obtainView(R.id.title_id);
            titleId.setVisibility(View.VISIBLE);
            EditText timeCaseNo = holder.obtainView(R.id.time_case_no);
            timeCaseNo.setVisibility(View.GONE);
            EditText programme_one = holder.obtainView(R.id.programme_one);
            programme_one.setOnFocusChangeListener(new EditViewOnFocusChangeListener(new EditViewChangeCallBack() {
                @Override
                public void setValue(String value) {
                    planCaseListBean.setT1(value);
                }
            }));
            //programme_one.addTextChangedListener(new MyTextWatcher());
            //programme_one.setOnTouchListener(new EditViewOnTouchListener(currentListType, index, 1));

            EditText programme_two = holder.obtainView(R.id.programme_two);
            programme_two.setOnFocusChangeListener(new EditViewOnFocusChangeListener(new EditViewChangeCallBack() {
                @Override
                public void setValue(String value) {
                    planCaseListBean.setT2(value);
                }
            }));
            //programme_two.addTextChangedListener(new MyTextWatcher());
            //programme_two.setOnTouchListener(new EditViewOnTouchListener(currentListType, index, 2));

            EditText programme_three = holder.obtainView(R.id.programme_three);
            programme_three.setOnFocusChangeListener(new EditViewOnFocusChangeListener(new EditViewChangeCallBack() {
                @Override
                public void setValue(String value) {
                    planCaseListBean.setT3(value);
                }
            }));
            //programme_three.addTextChangedListener(new MyTextWatcher());
            //programme_three.setOnTouchListener(new EditViewOnTouchListener(currentListType, index, 3));

            EditText programme_four = holder.obtainView(R.id.programme_four);
            programme_four.setOnFocusChangeListener(new EditViewOnFocusChangeListener(new EditViewChangeCallBack() {
                @Override
                public void setValue(String value) {
                    planCaseListBean.setT4(value);
                }
            }));
            //programme_four.addTextChangedListener(new MyTextWatcher());
            //programme_four.setOnTouchListener(new EditViewOnTouchListener(currentListType, index, 4));

            EditText programme_five = holder.obtainView(R.id.programme_five);
            programme_five.setOnFocusChangeListener(new EditViewOnFocusChangeListener(new EditViewChangeCallBack() {
                @Override
                public void setValue(String value) {
                    planCaseListBean.setT5(value);
                }
            }));
            //programme_five.addTextChangedListener(new MyTextWatcher());
            //programme_five.setOnTouchListener(new EditViewOnTouchListener(currentListType, index, 5));

            EditText programme_six = holder.obtainView(R.id.programme_six);
            programme_six.setOnFocusChangeListener(new EditViewOnFocusChangeListener(new EditViewChangeCallBack() {
                @Override
                public void setValue(String value) {
                    planCaseListBean.setT6(value);
                }
            }));
            //programme_six.addTextChangedListener(new MyTextWatcher());
            //programme_six.setOnTouchListener(new EditViewOnTouchListener(currentListType, index, 6));

            EditText programme_seven = holder.obtainView(R.id.programme_seven);
            programme_seven.setOnFocusChangeListener(new EditViewOnFocusChangeListener(new EditViewChangeCallBack() {
                @Override
                public void setValue(String value) {
                    planCaseListBean.setT7(value);
                }
            }));
            //programme_seven.addTextChangedListener(new MyTextWatcher());
            //programme_seven.setOnTouchListener(new EditViewOnTouchListener(currentListType, index, 7));

            EditText programme_eight = holder.obtainView(R.id.programme_eight);
            programme_eight.setOnFocusChangeListener(new EditViewOnFocusChangeListener(new EditViewChangeCallBack() {
                @Override
                public void setValue(String value) {
                    planCaseListBean.setT8(value);
                }
            }));
            //programme_eight.addTextChangedListener(new MyTextWatcher());
            //programme_eight.setOnTouchListener(new EditViewOnTouchListener(currentListType, index, 8));

            EditText programme_nine = holder.obtainView(R.id.programme_nine);
            programme_nine.setOnFocusChangeListener(new EditViewOnFocusChangeListener(new EditViewChangeCallBack() {
                @Override
                public void setValue(String value) {
                    planCaseListBean.setT9(value);
                }
            }));
            //programme_nine.addTextChangedListener(new MyTextWatcher());
            //programme_nine.setOnTouchListener(new EditViewOnTouchListener(currentListType, index, 9));

            EditText programme_ten = holder.obtainView(R.id.programme_ten);
            programme_ten.setOnFocusChangeListener(new EditViewOnFocusChangeListener(new EditViewChangeCallBack() {
                @Override
                public void setValue(String value) {
                    planCaseListBean.setT10(value);
                }
            }));
            //programme_ten.addTextChangedListener(new MyTextWatcher());
            //programme_ten.setOnTouchListener(new EditViewOnTouchListener(currentListType, index, 10));

            if ("1".equals(planCaseListBean.getType())) {
                typeOne.setVisibility(View.GONE);
                typeTwo.setVisibility(View.VISIBLE);
                typeThree.setVisibility(View.GONE);

                ImageView ivOne = holder.obtainView(R.id.iv_one);
                if(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT1()) != null){
                    editT1 = true;
                    ivOne.setImageBitmap(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT1()));
                }else{
                    editT1 = false;
                }
                ImageView ivTwo = holder.obtainView(R.id.iv_two);
                if(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT2()) != null){
                    editT2 = true;
                    ivTwo.setImageBitmap(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT2()));
                }else{
                    editT2 = false;
                }
                ImageView ivthree = holder.obtainView(R.id.iv_three);
                if(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT3()) != null){
                    editT3 = true;
                    ivthree.setImageBitmap(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT3()));
                }else{
                    editT3 = false;
                }
                ImageView ivFour = holder.obtainView(R.id.iv_four);
                if(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT4()) != null){
                    editT4 = true;
                    ivFour.setImageBitmap(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT4()));
                }else{
                    editT4 = false;
                }
                ImageView ivFive = holder.obtainView(R.id.iv_five);
                if(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT5()) != null){
                    editT5 = true;
                    ivFive.setImageBitmap(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT5()));
                }else{
                    editT5 = false;
                }
                ImageView ivSix = holder.obtainView(R.id.iv_six);
                if(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT6()) != null){
                    editT6 = true;
                    ivSix.setImageBitmap(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT6()));
                }else{
                    editT6 = false;
                }
                ImageView ivSeven = holder.obtainView(R.id.iv_seven);
                if(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT7()) != null){
                    editT7 = true;
                    ivSeven.setImageBitmap(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT7()));
                }else{
                    editT7 = false;
                }
                ImageView ivEight = holder.obtainView(R.id.iv_eight);
                if(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT8()) != null){
                    editT8 = true;
                    ivEight.setImageBitmap(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT8()));
                }else{
                    editT8 = false;
                }
                ImageView ivNine = holder.obtainView(R.id.iv_nine);
                if(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT9()) != null){
                    editT9 = true;
                    ivNine.setImageBitmap(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT9()));
                }else{
                    editT9 = false;
                }
                ImageView ivTen = holder.obtainView(R.id.iv_ten);
                if(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT10()) != null){
                    editT10 = true;
                    ivTen.setImageBitmap(StageDataUtil.getBitMapByStageNo(planCaseListBean.getT10()));
                }else{
                    editT10 = false;
                }


                ivOne.setOnClickListener(v -> {
                    DialogUtils.showTimingChoiceDialog(TimingEditorActivity.this, StageDataUtil.getAllStage(), new StageImgOnButtonClickListener(index, 1));
                });
                ivTwo.setOnClickListener(v -> {
                    DialogUtils.showTimingChoiceDialog(TimingEditorActivity.this, StageDataUtil.getAllStage(), new StageImgOnButtonClickListener(index, 2));
                });
                ivthree.setOnClickListener(v -> {
                    DialogUtils.showTimingChoiceDialog(TimingEditorActivity.this, StageDataUtil.getAllStage(), new StageImgOnButtonClickListener(index, 3));
                });
                ivFour.setOnClickListener(v -> {
                    DialogUtils.showTimingChoiceDialog(TimingEditorActivity.this, StageDataUtil.getAllStage(), new StageImgOnButtonClickListener(index, 4));
                });
                ivFive.setOnClickListener(v -> {
                    DialogUtils.showTimingChoiceDialog(TimingEditorActivity.this, StageDataUtil.getAllStage(), new StageImgOnButtonClickListener(index, 5));
                });
                ivSix.setOnClickListener(v -> {
                    DialogUtils.showTimingChoiceDialog(TimingEditorActivity.this, StageDataUtil.getAllStage(), new StageImgOnButtonClickListener(index, 6));
                });
                ivSeven.setOnClickListener(v -> {
                    DialogUtils.showTimingChoiceDialog(TimingEditorActivity.this, StageDataUtil.getAllStage(), new StageImgOnButtonClickListener(index, 7));
                });
                ivEight.setOnClickListener(v -> {
                    DialogUtils.showTimingChoiceDialog(TimingEditorActivity.this, StageDataUtil.getAllStage(), new StageImgOnButtonClickListener(index, 8));
                });
                ivNine.setOnClickListener(v -> {
                    DialogUtils.showTimingChoiceDialog(TimingEditorActivity.this, StageDataUtil.getAllStage(), new StageImgOnButtonClickListener(index, 9));
                });
                ivTen.setOnClickListener(v -> {
                    DialogUtils.showTimingChoiceDialog(TimingEditorActivity.this, StageDataUtil.getAllStage(), new StageImgOnButtonClickListener(index, 10));
                });


            } else {
                typeOne.setVisibility(View.GONE);
                typeTwo.setVisibility(View.GONE);
                typeThree.setVisibility(View.VISIBLE);

                programme_one.setText(planCaseListBean.getT1());
                if(!editT1){
                    programme_one.setEnabled(false);
                    programme_one.setTextColor(Color.parseColor("#6C6C6C"));
                }
                programme_two.setText(planCaseListBean.getT2());
                if(!editT2){
                    programme_two.setEnabled(false);
                    programme_two.setTextColor(Color.parseColor("#6C6C6C"));
                }
                programme_three.setText(planCaseListBean.getT3());
                if(!editT3){
                    programme_three.setEnabled(false);
                    programme_three.setTextColor(Color.parseColor("#6C6C6C"));
                }
                programme_four.setText(planCaseListBean.getT4());
                if(!editT4){
                    programme_four.setEnabled(false);
                    programme_four.setTextColor(Color.parseColor("#6C6C6C"));
                }
                programme_five.setText(planCaseListBean.getT5());
                if(!editT5){
                    programme_five.setEnabled(false);
                    programme_five.setTextColor(Color.parseColor("#6C6C6C"));
                }
                programme_six.setText(planCaseListBean.getT6());
                if(!editT6){
                    programme_six.setEnabled(false);
                    programme_six.setTextColor(Color.parseColor("#6C6C6C"));
                }
                programme_seven.setText(planCaseListBean.getT7());
                if(!editT7){
                    programme_seven.setEnabled(false);
                    programme_seven.setTextColor(Color.parseColor("#6C6C6C"));
                }
                programme_eight.setText(planCaseListBean.getT8());
                if(!editT8){
                    programme_eight.setEnabled(false);
                    programme_eight.setTextColor(Color.parseColor("#6C6C6C"));
                }
                programme_nine.setText(planCaseListBean.getT9());
                if(!editT9){
                    programme_nine.setEnabled(false);
                    programme_nine.setTextColor(Color.parseColor("#6C6C6C"));
                }
                programme_ten.setText(planCaseListBean.getT10());
                if(!editT10){
                    programme_ten.setEnabled(false);
                    programme_ten.setTextColor(Color.parseColor("#6C6C6C"));
                }
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
            return R.layout.layout_timing_table_item;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindHolder(BaseViewHolder holder, @Nullable TimeCaseListBean timeCaseListBean, int index) {
            holder.setIsRecyclable(false);//不使用复用 防止数据多时 复用时  多个item中的EditText填写的数据一样
            LinearLayout typeOne = holder.obtainView(R.id.type_one);
            LinearLayout typeTwo = holder.obtainView(R.id.type_two);
            LinearLayout typeThree = holder.obtainView(R.id.type_three);
            typeOne.setVisibility(View.GONE);
            typeTwo.setVisibility(View.GONE);
            typeThree.setVisibility(View.VISIBLE);

            TextView titleId = holder.obtainView(R.id.title_id);
            titleId.setVisibility(View.GONE);

            EditText timeCaseNo = holder.obtainView(R.id.time_case_no);
            timeCaseNo.setVisibility(View.VISIBLE);
            timeCaseNo.setOnFocusChangeListener(new EditViewOnFocusChangeListener(new EditViewChangeCallBack() {
                @Override
                public void setValue(String value) {
                    timeCaseListBean.setNo(value);
                }
            }));
            //timeCaseNo.addTextChangedListener(new MyTextWatcher());

            EditText programme_one = holder.obtainView(R.id.programme_one);
            programme_one.setOnFocusChangeListener(new EditViewOnFocusChangeListener(new EditViewChangeCallBack() {
                @Override
                public void setValue(String value) {
                    timeCaseListBean.setT1(value);
                }
            }));
            //programme_one.addTextChangedListener(new MyTextWatcher());

            EditText programme_two = holder.obtainView(R.id.programme_two);
            programme_two.setOnFocusChangeListener(new EditViewOnFocusChangeListener(new EditViewChangeCallBack() {
                @Override
                public void setValue(String value) {
                    timeCaseListBean.setT2(value);
                }
            }));
            //programme_two.addTextChangedListener(new MyTextWatcher());

            EditText programme_three = holder.obtainView(R.id.programme_three);
            programme_three.setOnFocusChangeListener(new EditViewOnFocusChangeListener(new EditViewChangeCallBack() {
                @Override
                public void setValue(String value) {
                    timeCaseListBean.setT3(value);
                }
            }));
            //programme_three.addTextChangedListener(new MyTextWatcher());

            EditText programme_four = holder.obtainView(R.id.programme_four);
            programme_four.setOnFocusChangeListener(new EditViewOnFocusChangeListener(new EditViewChangeCallBack() {
                @Override
                public void setValue(String value) {
                    timeCaseListBean.setT4(value);
                }
            }));
            //programme_four.addTextChangedListener(new MyTextWatcher());

            EditText programme_five = holder.obtainView(R.id.programme_five);
            programme_five.setOnFocusChangeListener(new EditViewOnFocusChangeListener(new EditViewChangeCallBack() {
                @Override
                public void setValue(String value) {
                    timeCaseListBean.setT5(value);
                }
            }));
            //programme_five.addTextChangedListener(new MyTextWatcher());

            EditText programme_six = holder.obtainView(R.id.programme_six);
            programme_six.setOnFocusChangeListener(new EditViewOnFocusChangeListener(new EditViewChangeCallBack() {
                @Override
                public void setValue(String value) {
                    timeCaseListBean.setT6(value);
                }
            }));
            //programme_six.addTextChangedListener(new MyTextWatcher());

            EditText programme_seven = holder.obtainView(R.id.programme_seven);
            programme_seven.setOnFocusChangeListener(new EditViewOnFocusChangeListener(new EditViewChangeCallBack() {
                @Override
                public void setValue(String value) {
                    timeCaseListBean.setT7(value);
                }
            }));
            //programme_seven.addTextChangedListener(new MyTextWatcher());

            EditText programme_eight = holder.obtainView(R.id.programme_eight);
            programme_eight.setOnFocusChangeListener(new EditViewOnFocusChangeListener(new EditViewChangeCallBack() {
                @Override
                public void setValue(String value) {
                    timeCaseListBean.setT8(value);
                }
            }));
            //programme_eight.addTextChangedListener(new MyTextWatcher());

            EditText programme_nine = holder.obtainView(R.id.programme_nine);
            programme_nine.setOnFocusChangeListener(new EditViewOnFocusChangeListener(new EditViewChangeCallBack() {
                @Override
                public void setValue(String value) {
                    timeCaseListBean.setT9(value);
                }
            }));
            //programme_nine.addTextChangedListener(new MyTextWatcher());

            EditText programme_ten = holder.obtainView(R.id.programme_ten);
            programme_ten.setOnFocusChangeListener(new EditViewOnFocusChangeListener(new EditViewChangeCallBack() {
                @Override
                public void setValue(String value) {
                    timeCaseListBean.setT10(value);
                }
            }));
            //programme_ten.addTextChangedListener(new MyTextWatcher());

            ImageView tvDelete = holder.obtainView(R.id.tv_delete);
            tvDelete.setVisibility(View.VISIBLE);

            programme_one.setText(timeCaseListBean.getT1());
            if(!editT1){
                programme_one.setEnabled(false);
                programme_one.setTextColor(Color.parseColor("#6C6C6C"));
            }
            programme_two.setText(timeCaseListBean.getT2());
            if(!editT2){
                programme_two.setEnabled(false);
                programme_two.setTextColor(Color.parseColor("#6C6C6C"));
            }
            programme_three.setText(timeCaseListBean.getT3());
            if(!editT3){
                programme_three.setEnabled(false);
                programme_three.setTextColor(Color.parseColor("#6C6C6C"));
            }
            programme_four.setText(timeCaseListBean.getT4());
            if(!editT4){
                programme_four.setEnabled(false);
                programme_four.setTextColor(Color.parseColor("#6C6C6C"));
            }
            programme_five.setText(timeCaseListBean.getT5());
            if(!editT5){
                programme_five.setEnabled(false);
                programme_five.setTextColor(Color.parseColor("#6C6C6C"));
            }
            programme_six.setText(timeCaseListBean.getT6());
            if(!editT6){
                programme_six.setEnabled(false);
                programme_six.setTextColor(Color.parseColor("#6C6C6C"));
            }
            programme_seven.setText(timeCaseListBean.getT7());
            if(!editT7){
                programme_seven.setEnabled(false);
                programme_seven.setTextColor(Color.parseColor("#6C6C6C"));
            }
            programme_eight.setText(timeCaseListBean.getT8());
            if(!editT8){
                programme_eight.setEnabled(false);
                programme_eight.setTextColor(Color.parseColor("#6C6C6C"));
            }
            programme_nine.setText(timeCaseListBean.getT9());
            if(!editT9){
                programme_nine.setEnabled(false);
                programme_nine.setTextColor(Color.parseColor("#6C6C6C"));
            }
            programme_ten.setText(timeCaseListBean.getT10());
            if(!editT10){
                programme_ten.setEnabled(false);
                programme_ten.setTextColor(Color.parseColor("#6C6C6C"));
            }

            timeCaseNo.setText(timeCaseListBean.getNo());
            /*timeCaseNo.setOnTouchListener(new EditViewOnTouchListener(currentListType, index, 0));
            programme_one.setOnTouchListener(new EditViewOnTouchListener(currentListType, index, 1));
            programme_two.setOnTouchListener(new EditViewOnTouchListener(currentListType, index, 2));
            programme_three.setOnTouchListener(new EditViewOnTouchListener(currentListType, index, 3));
            programme_four.setOnTouchListener(new EditViewOnTouchListener(currentListType, index, 4));
            programme_five.setOnTouchListener(new EditViewOnTouchListener(currentListType, index, 5));
            programme_six.setOnTouchListener(new EditViewOnTouchListener(currentListType, index, 6));
            programme_seven.setOnTouchListener(new EditViewOnTouchListener(currentListType, index, 7));
            programme_eight.setOnTouchListener(new EditViewOnTouchListener(currentListType, index, 8));
            programme_nine.setOnTouchListener(new EditViewOnTouchListener(currentListType, index, 9));
            programme_ten.setOnTouchListener(new EditViewOnTouchListener(currentListType, index, 10));*/


            //删除
            tvDelete.setOnClickListener(v -> {
                if (isWeekday) {
                    weekdaysTimeCaseDataList.remove(index);
                    timeCaseDataAdapter.bindData(true, weekdaysTimeCaseDataList);
                } else {
                    noWeekdaysTimeCaseDataList.remove(index);
                    timeCaseDataAdapter.bindData(true, noWeekdaysTimeCaseDataList);
                }
                setTaskState();
            });
        }
    }

    private void setTaskState() {
        taskStatus.setText("调整后");
    }

    class StageImgOnButtonClickListener implements DialogUtils.OnButtonClickListener{

        private int index;
        private int pos;

        public StageImgOnButtonClickListener(int index, int pos) {
            this.index = index;
            this.pos = pos;
        }

        @Override
        public void onPositiveButtonClick() {

        }

        @Override
        public void onNegativeButtonClick() {

        }

        @Override
        public void onChoiceItem(String str, int pos) {

            PlanCaseListBean planCaseListBean = weekdaysPlanCaseDataList.get(index);
            PlanCaseListBean noWeekDaysplanCaseListBean = noWeekdaysPlanCaseDataList.get(index);
            if(planCaseListBean == null){
                return;
            }
            if(str == null || "0".equals(str)){
                clearData(pos);
            }else {
                switch (pos){
                    case 1:
                        editT1 = true;
                        planCaseListBean.setT1(str);
                        if(noWeekDaysplanCaseListBean != null){
                            noWeekDaysplanCaseListBean.setT1(str);
                        }
                        break;
                    case 2:
                        editT2 = true;
                        planCaseListBean.setT2(str);
                        if(noWeekDaysplanCaseListBean != null){
                            noWeekDaysplanCaseListBean.setT2(str);
                        }
                        break;
                    case 3:
                        editT3 = true;
                        planCaseListBean.setT3(str);
                        if(noWeekDaysplanCaseListBean != null){
                            noWeekDaysplanCaseListBean.setT3(str);
                        }
                        break;
                    case 4:
                        editT4 = true;
                        planCaseListBean.setT4(str);
                        if(noWeekDaysplanCaseListBean != null){
                            noWeekDaysplanCaseListBean.setT4(str);
                        }
                        break;
                    case 5:
                        editT5 = true;
                        planCaseListBean.setT5(str);
                        if(noWeekDaysplanCaseListBean != null){
                            noWeekDaysplanCaseListBean.setT5(str);
                        }
                        break;
                    case 6:
                        editT6 = true;
                        planCaseListBean.setT6(str);
                        if(noWeekDaysplanCaseListBean != null){
                            noWeekDaysplanCaseListBean.setT6(str);
                        }
                        break;
                    case 7:
                        editT7 = true;
                        planCaseListBean.setT7(str);
                        if(noWeekDaysplanCaseListBean != null){
                            noWeekDaysplanCaseListBean.setT7(str);
                        }
                        break;
                    case 8:
                        editT8 = true;
                        planCaseListBean.setT8(str);
                        if(noWeekDaysplanCaseListBean != null){
                            noWeekDaysplanCaseListBean.setT8(str);
                        }
                        break;
                    case 9:
                        editT9 = true;
                        planCaseListBean.setT9(str);
                        if(noWeekDaysplanCaseListBean != null){
                            noWeekDaysplanCaseListBean.setT9(str);
                        }
                        break;
                    case 10:
                        editT10 = true;
                        planCaseListBean.setT10(str);

                        if(noWeekDaysplanCaseListBean != null){
                            noWeekDaysplanCaseListBean.setT10(str);
                        }
                        break;
                }
            }

            hidePlanCaseDataAdapter.notifyDataSetChanged();
            planCaseDataAdapter.notifyDataSetChanged();
            timeCaseDataAdapter.notifyDataSetChanged();
            setTaskState();
        }
    }

    public void clearData(int pos){
        switch (pos){
            case 1:
                editT1 = false;
                break;
            case 2:
                editT2 = false;
                break;
            case 3:
                editT3 = false;
                break;
            case 4:
                editT4 = false;
                break;
            case 5:
                editT5 = false;
                break;
            case 6:
                editT6 = false;
                break;
            case 7:
                editT7 = false;
                break;
            case 8:
                editT8 = false;
                break;
            case 9:
                editT9 = false;
                break;
            case 10:
                editT10 = false;
                break;
        }
        clearPlanCaseForAttr(hideWeekdaysPlanCaseList,pos);
        clearPlanCaseForAttr(noWeekdaysPlanCaseDataList,pos);
        clearPlanCaseForAttr(weekdaysPlanCaseDataList,pos);
        clearTimeCaseForAttr(weekdaysTimeCaseDataList,pos);
        clearTimeCaseForAttr(noWeekdaysTimeCaseDataList,pos);
        hidePlanCaseDataAdapter.notifyDataSetChanged();
        planCaseDataAdapter.notifyDataSetChanged();
        timeCaseDataAdapter.notifyDataSetChanged();

    }


    public void clearPlanCaseForAttr(List<PlanCaseListBean> planCaseListBeanList, int pos){
        for (PlanCaseListBean planCaseListBean : planCaseListBeanList
        ) {
            switch (pos) {
                case 1:
                    planCaseListBean.setT1("0");
                    break;
                case 2:
                    planCaseListBean.setT2("0");
                    break;
                case 3:
                    planCaseListBean.setT3("0");
                    break;
                case 4:
                    planCaseListBean.setT4("0");
                    break;
                case 5:
                    planCaseListBean.setT5("0");
                    break;
                case 6:
                    planCaseListBean.setT6("0");
                    break;
                case 7:
                    planCaseListBean.setT7("0");
                    break;
                case 8:
                    planCaseListBean.setT8("0");
                    break;
                case 9:
                    planCaseListBean.setT9("0");
                    break;
                case 10:
                    planCaseListBean.setT10("0");
                    break;
            }
        }
    }


    public void clearTimeCaseForAttr(List<TimeCaseListBean> timeCaseListBeanList, int pos){
        for (TimeCaseListBean timeCaseListBean : timeCaseListBeanList
        ) {
            switch (pos) {
                case 1:
                    timeCaseListBean.setT1("0");
                    break;
                case 2:
                    timeCaseListBean.setT2("0");
                    break;
                case 3:
                    timeCaseListBean.setT3("0");
                    break;
                case 4:
                    timeCaseListBean.setT4("0");
                    break;
                case 5:
                    timeCaseListBean.setT5("0");
                    break;
                case 6:
                    timeCaseListBean.setT6("0");
                    break;
                case 7:
                    timeCaseListBean.setT7("0");
                    break;
                case 8:
                    timeCaseListBean.setT8("0");
                    break;
                case 9:
                    timeCaseListBean.setT9("0");
                    break;
                case 10:
                    timeCaseListBean.setT10("0");
                    break;
            }
        }
    }

    class EditViewOnFocusChangeListener implements View.OnFocusChangeListener{

        private EditViewChangeCallBack callBack;

        public EditViewOnFocusChangeListener(EditViewChangeCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        public void onFocusChange(View view, boolean b) {
            EditText editText = ((EditText) view);
            if(!b){
                String value = editText.getText().toString().trim();
                if(TextUtils.isEmpty(value)){
                    value = "0";
                    editText.setText(value);
                }
                view.setBackgroundColor(Color.parseColor("#EFEFEF"));
                ((EditText)view).setTextColor(Color.parseColor("#ff2a4997"));
                callBack.setValue(value);
            }else {
                currentEditView=editText;
            }
        }
    }

    interface EditViewChangeCallBack{

        public void setValue(String value);
    }


    /*class MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //setContent(currentEditView);
            //setContentTest(s.toString());
        }
    }*/
/*

    class EditViewOnTouchListener implements View.OnTouchListener{

        private int listType; //1是 0不是 -1不区分工作日
        private int listIndex; //正在编辑的索引
        private int listDataPos; //正在编辑的阶段配时

        public EditViewOnTouchListener(int listType, int listIndex, int listDataPos) {
            this.listType = listType;
            this.listIndex = listIndex;
            this.listDataPos = listDataPos;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            currentListType = listType;
            currentListIndex = listIndex;
            currentListDataPos = listDataPos;
            return false;
        }
    }*/

}
