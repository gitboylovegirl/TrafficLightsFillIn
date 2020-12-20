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
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.fred.trafficlightsfillin.MainActivity;
import com.fred.trafficlightsfillin.R;
import com.fred.trafficlightsfillin.base.BaseRecyclerAdapter;
import com.fred.trafficlightsfillin.base.BaseResponse;
import com.fred.trafficlightsfillin.base.BaseViewHolder;
import com.fred.trafficlightsfillin.base.MyGlideEngine;
import com.fred.trafficlightsfillin.base.RequestApi;
import com.fred.trafficlightsfillin.feed.FeedActivity;
import com.fred.trafficlightsfillin.intersection.bean.ImageResponse;
import com.fred.trafficlightsfillin.intersection.bean.PeriodCaseListBean;
import com.fred.trafficlightsfillin.intersection.bean.PlanCaseListBean;
import com.fred.trafficlightsfillin.intersection.bean.StageResponse;
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
import com.fred.trafficlightsfillin.utils.TimeUtils;
import com.fred.trafficlightsfillin.utils.ToastUtil;
import com.google.gson.Gson;
import com.ikovac.timepickerwithseconds.MyTimePickerDialog;
import com.ikovac.timepickerwithseconds.TimePicker;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

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
    @BindView(R.id.task_end)
    ImageView taskEnd;
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
    @BindView(R.id.state)
    TextView state;
    @BindView(R.id.type_one)
    LinearLayout typeOne;
    @BindView(R.id.better)
    EditText better;
    @BindView(R.id.time_list_add)
    TextView timeListAdd;
    @BindView(R.id.timetable_add)
    TextView timetableAdd;

    private View popuwindowView;
    private PopupWindow popupWindow;
    private TextView currentTextView;
    private int currentType;
    private int currentPos;
    private int currentChoosePos;
    EditText inputComment;

    private List<PeriodCaseListBean> weekdaysPeriodCaseList = new ArrayList<>();//工作日时间表
    private List<PeriodCaseListBean> weekendPeriodCaseList = new ArrayList<>();//周末时间表

    private List<PlanCaseListBean> weekdaysPlanCaseList = new ArrayList<>();//工作日配时表1
    private List<PlanCaseListBean> weekendPlanCaseList = new ArrayList<>();//周末配时表1

    private List<TimeCaseListBean> weekdaysTimeCaseList = new ArrayList<>();//工作日配时表1
    private List<TimeCaseListBean> weekendTimeCaseList = new ArrayList<>();//周末配时表1

    PictureAdapter pictureAdapter;
    TimeTableAdapter timeTableAdapter;
    String trafficLightId;
    String id;
    List<StageResponse.StageChanel> stageChanels;//配时表数据
    List<ImageResponse.ImageBean> imageBeans = new ArrayList<>();
    PlanCaseAdapter planCaseAdapter;
    TimeCaseAdapter timeCaseAdapter;

    boolean isWeekday = true;

    PeriodCaseListBean lastWeekdayPeriodBean;
    PeriodCaseListBean lastWeekendPeriodBean;

    TimeCaseListBean lastWeekdaysTimeBean;
    TimeCaseListBean lastWeekendTimeBean;
    boolean isShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timing_editor);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (TimingEditorActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                //如果没有写sd卡权限
                TimingEditorActivity.this.requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        100);
            }
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        pictureList.setLayoutManager(layoutManager);
        pictureAdapter = new PictureAdapter();
        pictureList.setAdapter(pictureAdapter);

        timeList.setLayoutManager(new LinearLayoutManager(this));
        timeTableAdapter = new TimeTableAdapter();
        timeList.setAdapter(timeTableAdapter);

        planCaseAdapter = new PlanCaseAdapter();
        programme.setLayoutManager(new LinearLayoutManager(this));
        programme.setAdapter(planCaseAdapter);

        timeCaseAdapter = new TimeCaseAdapter();
        timetable.setLayoutManager(new LinearLayoutManager(this));
        timetable.setAdapter(timeCaseAdapter);

        //工作日
        weekday.setOnClickListener(view -> {
            isWeekday = true;
            weekday.setBackground(getResources().getDrawable(R.drawable.bg_color_blue_gray_stroke_main));
            weekend.setBackground(getResources().getDrawable(R.drawable.bg_color_blue_gray_stroke));
            timeTableAdapter.bindData(true, weekdaysPeriodCaseList);
            planCaseAdapter.bindData(true, weekdaysPlanCaseList);
            timeCaseAdapter.bindData(true, weekdaysTimeCaseList);
        });
        //周日
        weekend.setOnClickListener(view -> {
            isWeekday = false;
            weekend.setBackground(getResources().getDrawable(R.drawable.bg_color_blue_gray_stroke_main));
            weekday.setBackground(getResources().getDrawable(R.drawable.bg_color_blue_gray_stroke));
            timeTableAdapter.bindData(true, weekendPeriodCaseList);
            planCaseAdapter.bindData(true, weekendPlanCaseList);
            timeCaseAdapter.bindData(true, weekendTimeCaseList);
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
            periodCaseList.addAll(weekdaysPeriodCaseList);
            periodCaseList.addAll(weekendPeriodCaseList);
            submitBean.setPeriodCaseList(periodCaseList);


            List<PlanCaseListBean> planCaseList = new ArrayList<>();
            planCaseList.addAll(weekdaysPlanCaseList);
            planCaseList.addAll(weekendPlanCaseList);
            submitBean.setPlanCaseList(planCaseList);

            List<TimeCaseListBean> timeCaseList = new ArrayList<>();
            timeCaseList.addAll(weekdaysTimeCaseList);
            timeCaseList.addAll(weekendTimeCaseList);
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
            if (isWeekday) {
                weekdaysPeriodCaseList.add(lastWeekdayPeriodBean);
                timeTableAdapter.bindData(true, weekdaysPeriodCaseList);
            } else {
                weekendPeriodCaseList.add(lastWeekendPeriodBean);
                timeTableAdapter.bindData(true, weekendPeriodCaseList);
            }
            lastWeekdayPeriodBean = weekdaysPeriodCaseList.get(weekdaysPeriodCaseList.size() - 1);
            lastWeekendPeriodBean = weekendPeriodCaseList.get(weekendPeriodCaseList.size() - 1);
            setTaskState();

        });

        /**
         * 配时表新增
         */
        timetableAdd.setOnClickListener(v -> {
//            for (int i = 0; i < weekdaysTimeCaseList.size(); i++) {
//                Log.e("fred  数据1",i+"   "+weekdaysTimeCaseList.get(i).getT1());
//            }
            if (isWeekday) {
                weekdaysTimeCaseList.add(lastWeekdaysTimeBean);
                timeCaseAdapter.bindData(true, weekdaysTimeCaseList);
            } else {
                weekendTimeCaseList.add(lastWeekendTimeBean);
                timeCaseAdapter.bindData(true, weekendTimeCaseList);
            }
            lastWeekdaysTimeBean = weekdaysTimeCaseList.get(weekdaysTimeCaseList.size() - 1);
            lastWeekendTimeBean = weekdaysTimeCaseList.get(weekendTimeCaseList.size() - 1);
            setTaskState();
            for (int i = 0; i < weekdaysTimeCaseList.size(); i++) {
                Log.e("fred  数据2",i+"   "+weekdaysTimeCaseList.get(i).getT1());
            }
        });

        endTime.setText(TimeUtils.time10(String.valueOf(System.currentTimeMillis())));

        //键盘显示监听
        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                Toast.makeText(TimingEditorActivity.this, "键盘显示 高度" + height, Toast.LENGTH_SHORT).show();

                if (isShow) {
                    showPopupCommnet(height);
                    inputComment.requestFocus();
                }
            }

            @Override
            public void keyBoardHide(int height) {
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
//                                state.setText("已完成");
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
                .maxSelectable(1)
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
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TRAFFICLIGH_PEISHI + "/61"))
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

                       // Log.e("fred", weekdaysPeriodCaseList.size() + "  数量**");

                        if (timeTableAdapter == null) {
                            timeTableAdapter = new TimeTableAdapter();
                        }

                        if (planCaseAdapter == null) {
                            planCaseAdapter = new PlanCaseAdapter();
                        }

                        if (timeCaseAdapter == null) {
                            timeCaseAdapter = new TimeCaseAdapter();
                        }

                        timeTableAdapter.bindData(true, weekdaysPeriodCaseList);
                        planCaseAdapter.bindData(true, weekdaysPlanCaseList);
                        timeCaseAdapter.bindData(true, weekdaysTimeCaseList);

                        lastWeekdayPeriodBean = weekdaysPeriodCaseList.get(weekdaysPeriodCaseList.size() - 1);
                        lastWeekendPeriodBean = weekendPeriodCaseList.get(weekendPeriodCaseList.size() - 1);
                        lastWeekdaysTimeBean = weekdaysTimeCaseList.get(weekdaysTimeCaseList.size() - 1);
                        lastWeekendTimeBean = weekdaysTimeCaseList.get(weekendTimeCaseList.size() - 1);
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
            TextView startTime = holder.obtainView(R.id.start_time);
            TextView no = holder.obtainView(R.id.number);
            TextView timetable_delete = holder.obtainView(R.id.timetable_delete);
            timetable_delete.setVisibility(View.VISIBLE);

            startTime.setText(periodCaseListBean.getStart());
            no.setText(periodCaseListBean.getTimeCaseNo());

            timetable_delete.setOnClickListener(v -> {
                lastWeekdayPeriodBean = weekdaysPeriodCaseList.get(weekdaysPeriodCaseList.size() - 1);
                lastWeekendPeriodBean = weekendPeriodCaseList.get(weekendPeriodCaseList.size() - 1);
                if (isWeekday) {
                    weekdaysPeriodCaseList.remove(index);
                    timeTableAdapter.bindData(true, weekdaysPeriodCaseList);
                } else {
                    weekendPeriodCaseList.remove(index);
                    timeTableAdapter.bindData(true, weekendPeriodCaseList);
                }
                setTaskState();
            });

            no.setOnClickListener(v -> {
                currentTextView = no;
                currentType=1;
                currentPos=index;
                currentChoosePos=2;
                showPopupCommnet(800);
            });

            startTime.setOnClickListener(v -> {
                MyTimePickerDialog mTimePicker = new MyTimePickerDialog(TimingEditorActivity.this, (view, hourOfDay, minute, seconds) -> {
                    // TODO Auto-generated method stub
                    String time=String.format("%02d", hourOfDay)+ ":" + String.format("%02d", minute) + ":" + String.format("%02d", seconds);
                    //Log.e("fred",time);
                    if(isWeekday){
                        weekdaysPeriodCaseList.get(currentPos).setStart(time);
                    }else {
                        weekendPeriodCaseList.get(currentPos).setStart(time);
                    }
                    lastWeekdayPeriodBean = weekdaysPeriodCaseList.get(weekdaysPeriodCaseList.size() - 1);
                    lastWeekendPeriodBean = weekendPeriodCaseList.get(weekendPeriodCaseList.size() - 1);
                    startTime.setText(time);
                },0,0, 0, true);
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
                for (int i = 0; i < weekdaysTimeCaseList.size(); i++) {
                    Log.e("fred  数据3",i+"   "+weekdaysTimeCaseList.get(i).getT1());
                }
                currentTextView.setText(editable.toString().trim());
                for (int i = 0; i < weekdaysTimeCaseList.size(); i++) {
                    Log.e("fred  数据4",i+"   "+weekdaysTimeCaseList.get(i).getT1());
                }
                if(isWeekday){
                    //工作日
                    if(currentType==1){
                        //配时方案
                        weekdaysPeriodCaseList.get(currentPos).setTimeCaseNo(editable.toString().trim());
                    }else if (currentType==2){
                        //配时表1
                        if(currentChoosePos==1){
                            weekdaysPlanCaseList.get(currentPos).setT1(editable.toString().trim());
                        }else if(currentChoosePos==2){
                            weekdaysPlanCaseList.get(currentPos).setT2(editable.toString().trim());
                        }else if(currentChoosePos==3){
                            weekdaysPlanCaseList.get(currentPos).setT3(editable.toString().trim());
                        }else if(currentChoosePos==4){
                            weekdaysPlanCaseList.get(currentPos).setT4(editable.toString().trim());
                        }else if(currentChoosePos==5){
                            weekdaysPlanCaseList.get(currentPos).setT5(editable.toString().trim());
                        }else if(currentChoosePos==6){
                            weekdaysPlanCaseList.get(currentPos).setT6(editable.toString().trim());
                        }else if(currentChoosePos==7){
                            weekdaysPlanCaseList.get(currentPos).setT7(editable.toString().trim());
                        }else if(currentChoosePos==8){
                            weekdaysPlanCaseList.get(currentPos).setT8(editable.toString().trim());
                        }else if(currentChoosePos==9){
                            weekdaysPlanCaseList.get(currentPos).setT9(editable.toString().trim());
                        }else if(currentChoosePos==10){
                            weekdaysPlanCaseList.get(currentPos).setT10(editable.toString().trim());
                        }
                    }else {
                        //配时表2
                        if(currentChoosePos==1){
                            weekdaysTimeCaseList.get(currentPos).setT1(editable.toString().trim());
                        }else if(currentChoosePos==2){
                            weekdaysTimeCaseList.get(currentPos).setT2(editable.toString().trim());
                        }else if(currentChoosePos==3){
                            weekdaysTimeCaseList.get(currentPos).setT3(editable.toString().trim());
                        }else if(currentChoosePos==4){
                            weekdaysTimeCaseList.get(currentPos).setT4(editable.toString().trim());
                        }else if(currentChoosePos==5){
                            weekdaysTimeCaseList.get(currentPos).setT5(editable.toString().trim());
                        }else if(currentChoosePos==6){
                            weekdaysTimeCaseList.get(currentPos).setT6(editable.toString().trim());
                        }else if(currentChoosePos==7){
                            weekdaysTimeCaseList.get(currentPos).setT7(editable.toString().trim());
                        }else if(currentChoosePos==8){
                            weekdaysTimeCaseList.get(currentPos).setT8(editable.toString().trim());
                        }else if(currentChoosePos==9){
                            weekdaysTimeCaseList.get(currentPos).setT9(editable.toString().trim());
                        }else if(currentChoosePos==10){
                            weekdaysTimeCaseList.get(currentPos).setT10(editable.toString().trim());
                        }
                    }
                }else {
                    //周末
                    if(currentType==1){
                        //配时方案
                        weekendPeriodCaseList.get(currentPos).setTimeCaseNo(editable.toString().trim());
                    }else if (currentType==2){
                        //配时表1
                        if(currentChoosePos==1){
                            weekendPlanCaseList.get(currentPos).setT1(editable.toString().trim());
                        }else if(currentChoosePos==2){
                            weekendPlanCaseList.get(currentPos).setT2(editable.toString().trim());
                        }else if(currentChoosePos==3){
                            weekendPlanCaseList.get(currentPos).setT3(editable.toString().trim());
                        }else if(currentChoosePos==4){
                            weekendPlanCaseList.get(currentPos).setT4(editable.toString().trim());
                        }else if(currentChoosePos==5){
                            weekendPlanCaseList.get(currentPos).setT5(editable.toString().trim());
                        }else if(currentChoosePos==6){
                            weekendPlanCaseList.get(currentPos).setT6(editable.toString().trim());
                        }else if(currentChoosePos==7){
                            weekendPlanCaseList.get(currentPos).setT7(editable.toString().trim());
                        }else if(currentChoosePos==8){
                            weekendPlanCaseList.get(currentPos).setT8(editable.toString().trim());
                        }else if(currentChoosePos==9){
                            weekendPlanCaseList.get(currentPos).setT9(editable.toString().trim());
                        }else if(currentChoosePos==10){
                            weekendPlanCaseList.get(currentPos).setT10(editable.toString().trim());
                        }
                    }else {
                        //配时表2
                        if(currentChoosePos==1){
                            weekendTimeCaseList.get(currentPos).setT1(editable.toString().trim());
                        }else if(currentChoosePos==2){
                            weekendTimeCaseList.get(currentPos).setT2(editable.toString().trim());
                        }else if(currentChoosePos==3){
                            weekendTimeCaseList.get(currentPos).setT3(editable.toString().trim());
                        }else if(currentChoosePos==4){
                            weekendTimeCaseList.get(currentPos).setT4(editable.toString().trim());
                        }else if(currentChoosePos==5){
                            weekendTimeCaseList.get(currentPos).setT5(editable.toString().trim());
                        }else if(currentChoosePos==6){
                            weekendTimeCaseList.get(currentPos).setT6(editable.toString().trim());
                        }else if(currentChoosePos==7){
                            weekendTimeCaseList.get(currentPos).setT7(editable.toString().trim());
                        }else if(currentChoosePos==8){
                            weekendTimeCaseList.get(currentPos).setT8(editable.toString().trim());
                        }else if(currentChoosePos==9){
                            weekendTimeCaseList.get(currentPos).setT9(editable.toString().trim());
                        }else if(currentChoosePos==10){
                            weekendTimeCaseList.get(currentPos).setT10(editable.toString().trim());
                        }
                    }
                }
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

            programme_one.setOnClickListener(v -> {
                currentTextView=programme_one;
                currentType=2;
                currentPos=index;
                currentChoosePos=1;
                showPopupCommnet(800);
            });

            programme_two.setOnClickListener(v -> {
                currentTextView=programme_two;
                currentType=2;
                currentPos=index;
                currentChoosePos=2;
                showPopupCommnet(800);
            });

            programme_three.setOnClickListener(v -> {
                currentTextView=programme_three;
                currentType=2;
                currentPos=index;
                currentChoosePos=3;
                showPopupCommnet(800);
            });

            programme_four.setOnClickListener(v -> {
                currentTextView=programme_four;
                currentType=2;
                currentPos=index;
                currentChoosePos=4;
                showPopupCommnet(800);
            });

            programme_five.setOnClickListener(v -> {
                currentTextView=programme_five;
                currentType=2;
                currentPos=index;
                currentChoosePos=5;
                showPopupCommnet(800);
            });

            programme_six.setOnClickListener(v -> {
                currentTextView=programme_six;
                currentType=2;
                currentPos=index;
                currentChoosePos=6;
                showPopupCommnet(800);
            });

            programme_seven.setOnClickListener(v -> {
                currentTextView=programme_seven;
                currentType=2;
                currentPos=index;
                currentChoosePos=7;
                showPopupCommnet(800);
            });

            programme_eight.setOnClickListener(v -> {
                currentTextView=programme_eight;
                currentType=2;
                currentPos=index;
                currentChoosePos=8;
                showPopupCommnet(800);
            });

            programme_nine.setOnClickListener(v -> {
                currentTextView=programme_nine;
                currentType=2;
                currentPos=index;
                currentChoosePos=9;
                showPopupCommnet(800);
            });

            programme_ten.setOnClickListener(v -> {
                currentTextView=programme_ten;
                currentType=2;
                currentPos=index;
                currentChoosePos=10;
                showPopupCommnet(800);
            });

            if ("1".equals(planCaseListBean.getType())) {
                typeOne.setVisibility(View.GONE);
                typeTwo.setVisibility(View.VISIBLE);
                typeThree.setVisibility(View.GONE);

                for (int i = 0; i < stageChanels.size(); i++) {
                    //Log.e("fred", stageChanels.get(i).image);
                    if (stageChanels.get(i).no.equals(planCaseListBean.getT1())) {
                        //String str2=str.replace(" ", "")
                        String[] split = stageChanels.get(i).image.split(",");
                        String base64 = split[1];
                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivOne.setImageBitmap(decodedByte);
                    }
                    if (stageChanels.get(i).no.equals(planCaseListBean.getT2())) {
                        String[] split = stageChanels.get(i).image.split(",");
                        String base64 = split[1];
                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivTwo.setImageBitmap(decodedByte);
                    }
                    if (stageChanels.get(i).no.equals(planCaseListBean.getT3())) {
                        String[] split = stageChanels.get(i).image.split(",");
                        String base64 = split[1];
                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivthree.setImageBitmap(decodedByte);
                    }
                    if (stageChanels.get(i).no.equals(planCaseListBean.getT4())) {
                        String[] split = stageChanels.get(i).image.split(",");
                        String base64 = split[1];
                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivFour.setImageBitmap(decodedByte);
                    }
                    if (stageChanels.get(i).no.equals(planCaseListBean.getT5())) {
                        String[] split = stageChanels.get(i).image.split(",");
                        String base64 = split[1];
                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivFive.setImageBitmap(decodedByte);
                    }
                    if (stageChanels.get(i).no.equals(planCaseListBean.getT6())) {
                        String[] split = stageChanels.get(i).image.split(",");
                        String base64 = split[1];
                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivSix.setImageBitmap(decodedByte);
                    }
                    if (stageChanels.get(i).no.equals(planCaseListBean.getT7())) {
                        String[] split = stageChanels.get(i).image.split(",");
                        String base64 = split[1];
                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivSeven.setImageBitmap(decodedByte);
                    }
                    if (stageChanels.get(i).no.equals(planCaseListBean.getT8())) {
                        String[] split = stageChanels.get(i).image.split(",");
                        String base64 = split[1];
                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivEight.setImageBitmap(decodedByte);
                    }
                    if (stageChanels.get(i).no.equals(planCaseListBean.getT9())) {
                        String[] split = stageChanels.get(i).image.split(",");
                        String base64 = split[1];
                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivNine.setImageBitmap(decodedByte);
                    }
                    if (stageChanels.get(i).no.equals(planCaseListBean.getT10())) {
                        String[] split = stageChanels.get(i).image.split(",");
                        String base64 = split[1];
                        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivTen.setImageBitmap(decodedByte);
                    }
                }

                ivOne.setOnClickListener(v -> {
                    DialogUtils.showTimingChoiceDialog(TimingEditorActivity.this, stageChanels, new DialogUtils.OnButtonClickListener() {
                        @Override
                        public void onPositiveButtonClick() {

                        }

                        @Override
                        public void onNegativeButtonClick() {

                        }

                        @Override
                        public void onChoiceItem(String str, int pos) {
                            if (isWeekday) {
                                weekdaysPlanCaseList.get(index).setT1(str);
                            } else {
                                weekendPlanCaseList.get(index).setT1(str);
                            }
                            planCaseAdapter.notifyDataSetChanged();
                            setTaskState();
                        }
                    });
                });
                ivTwo.setOnClickListener(v -> {
                    DialogUtils.showTimingChoiceDialog(TimingEditorActivity.this, stageChanels, new DialogUtils.OnButtonClickListener() {
                        @Override
                        public void onPositiveButtonClick() {

                        }

                        @Override
                        public void onNegativeButtonClick() {

                        }

                        @Override
                        public void onChoiceItem(String str, int pos) {
                            if (isWeekday) {
                                weekdaysPlanCaseList.get(index).setT2(str);
                            } else {
                                weekendPlanCaseList.get(index).setT2(str);
                            }
                            planCaseAdapter.notifyDataSetChanged();
                            setTaskState();
                        }
                    });
                });

                ivthree.setOnClickListener(v -> {
                    DialogUtils.showTimingChoiceDialog(TimingEditorActivity.this, stageChanels, new DialogUtils.OnButtonClickListener() {
                        @Override
                        public void onPositiveButtonClick() {

                        }

                        @Override
                        public void onNegativeButtonClick() {

                        }

                        @Override
                        public void onChoiceItem(String str, int pos) {
                            if (isWeekday) {
                                weekdaysPlanCaseList.get(index).setT3(str);
                            } else {
                                weekendPlanCaseList.get(index).setT3(str);
                            }
                            planCaseAdapter.notifyDataSetChanged();
                            setTaskState();
                        }
                    });
                });

                ivFour.setOnClickListener(v -> {
                    DialogUtils.showTimingChoiceDialog(TimingEditorActivity.this, stageChanels, new DialogUtils.OnButtonClickListener() {
                        @Override
                        public void onPositiveButtonClick() {

                        }

                        @Override
                        public void onNegativeButtonClick() {

                        }

                        @Override
                        public void onChoiceItem(String str, int pos) {
                            if (isWeekday) {
                                weekdaysPlanCaseList.get(index).setT4(str);
                            } else {
                                weekendPlanCaseList.get(index).setT4(str);
                            }
                            planCaseAdapter.notifyDataSetChanged();
                            setTaskState();
                        }
                    });
                });

                ivFive.setOnClickListener(v -> {
                    DialogUtils.showTimingChoiceDialog(TimingEditorActivity.this, stageChanels, new DialogUtils.OnButtonClickListener() {
                        @Override
                        public void onPositiveButtonClick() {

                        }

                        @Override
                        public void onNegativeButtonClick() {

                        }

                        @Override
                        public void onChoiceItem(String str, int pos) {
                            if (isWeekday) {
                                weekdaysPlanCaseList.get(index).setT5(str);
                            } else {
                                weekendPlanCaseList.get(index).setT5(str);
                            }
                            planCaseAdapter.notifyDataSetChanged();
                            setTaskState();
                        }
                    });
                });

                ivSix.setOnClickListener(v -> {
                    DialogUtils.showTimingChoiceDialog(TimingEditorActivity.this, stageChanels, new DialogUtils.OnButtonClickListener() {
                        @Override
                        public void onPositiveButtonClick() {

                        }

                        @Override
                        public void onNegativeButtonClick() {

                        }

                        @Override
                        public void onChoiceItem(String str, int pos) {
                            if (isWeekday) {
                                weekdaysPlanCaseList.get(index).setT6(str);
                            } else {
                                weekendPlanCaseList.get(index).setT6(str);
                            }
                            planCaseAdapter.notifyDataSetChanged();
                            setTaskState();
                        }
                    });
                });

                ivSeven.setOnClickListener(v -> {
                    DialogUtils.showTimingChoiceDialog(TimingEditorActivity.this, stageChanels, new DialogUtils.OnButtonClickListener() {
                        @Override
                        public void onPositiveButtonClick() {

                        }

                        @Override
                        public void onNegativeButtonClick() {

                        }

                        @Override
                        public void onChoiceItem(String str, int pos) {
                            if (isWeekday) {
                                weekdaysPlanCaseList.get(index).setT7(str);
                            } else {
                                weekendPlanCaseList.get(index).setT7(str);
                            }
                            planCaseAdapter.notifyDataSetChanged();
                            setTaskState();
                        }
                    });
                });

                ivEight.setOnClickListener(v -> {
                    DialogUtils.showTimingChoiceDialog(TimingEditorActivity.this, stageChanels, new DialogUtils.OnButtonClickListener() {
                        @Override
                        public void onPositiveButtonClick() {

                        }

                        @Override
                        public void onNegativeButtonClick() {

                        }

                        @Override
                        public void onChoiceItem(String str, int pos) {
                            if (isWeekday) {
                                weekdaysPlanCaseList.get(index).setT8(str);
                            } else {
                                weekendPlanCaseList.get(index).setT8(str);
                            }
                            planCaseAdapter.notifyDataSetChanged();
                            setTaskState();
                        }
                    });
                });

                ivNine.setOnClickListener(v -> {
                    DialogUtils.showTimingChoiceDialog(TimingEditorActivity.this, stageChanels, new DialogUtils.OnButtonClickListener() {
                        @Override
                        public void onPositiveButtonClick() {

                        }

                        @Override
                        public void onNegativeButtonClick() {

                        }

                        @Override
                        public void onChoiceItem(String str, int pos) {
                            if (isWeekday) {
                                weekdaysPlanCaseList.get(index).setT9(str);
                            } else {
                                weekendPlanCaseList.get(index).setT9(str);
                            }
                            planCaseAdapter.notifyDataSetChanged();
                            setTaskState();
                        }
                    });
                });

                ivTen.setOnClickListener(v -> {
                    DialogUtils.showTimingChoiceDialog(TimingEditorActivity.this, stageChanels, new DialogUtils.OnButtonClickListener() {
                        @Override
                        public void onPositiveButtonClick() {

                        }

                        @Override
                        public void onNegativeButtonClick() {

                        }

                        @Override
                        public void onChoiceItem(String str, int pos) {
                            if (isWeekday) {
                                weekdaysPlanCaseList.get(index).setT10(str);
                            } else {
                                weekendPlanCaseList.get(index).setT10(str);
                            }
                            planCaseAdapter.notifyDataSetChanged();
                            setTaskState();
                        }
                    });
                });

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

            TextView tvDelete = holder.obtainView(R.id.tv_delete);
            tvDelete.setVisibility(View.VISIBLE);

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

            programme_one.setOnClickListener(v -> {
               currentTextView=programme_one;
               currentType=3;
               currentPos=index;
               currentChoosePos=1;
                showPopupCommnet(800);
            });

            programme_two.setOnClickListener(v -> {
                currentTextView=programme_two;
                currentType=3;
                currentPos=index;
                currentChoosePos=2;
                showPopupCommnet(800);
            });

            programme_three.setOnClickListener(v -> {
                currentTextView=programme_three;
                currentType=3;
                currentPos=index;
                currentChoosePos=3;
                showPopupCommnet(800);
            });

            programme_four.setOnClickListener(v -> {
                currentTextView=programme_four;
                currentType=3;
                currentPos=index;
                currentChoosePos=4;
                showPopupCommnet(800);
            });

            programme_five.setOnClickListener(v -> {
                currentTextView=programme_five;
                currentType=3;
                currentPos=index;
                currentChoosePos=5;
                showPopupCommnet(800);
            });

            programme_six.setOnClickListener(v -> {
                currentTextView=programme_six;
                currentType=3;
                currentPos=index;
                currentChoosePos=6;
                showPopupCommnet(800);
            });

            programme_seven.setOnClickListener(v -> {
                currentTextView=programme_seven;
                currentType=3;
                currentPos=index;
                currentChoosePos=7;
                showPopupCommnet(800);
            });

            programme_eight.setOnClickListener(v -> {
                currentTextView=programme_eight;
                currentType=3;
                currentPos=index;
                currentChoosePos=8;
                showPopupCommnet(800);
            });

            programme_nine.setOnClickListener(v -> {
                currentTextView=programme_nine;
                currentType=3;
                currentPos=index;
                currentChoosePos=9;
                showPopupCommnet(800);
            });

            programme_ten.setOnClickListener(v -> {
                currentTextView=programme_ten;
                currentType=3;
                currentPos=index;
                currentChoosePos=10;
                showPopupCommnet(800);
            });

            //删除
            tvDelete.setOnClickListener(v -> {
                lastWeekdaysTimeBean = weekdaysTimeCaseList.get(weekdaysTimeCaseList.size() - 1);
                lastWeekendTimeBean = weekendTimeCaseList.get(weekendTimeCaseList.size() - 1);
                if (isWeekday) {
                    weekdaysTimeCaseList.remove(index);
                    timeCaseAdapter.bindData(true, weekdaysTimeCaseList);
                } else {
                    weekendTimeCaseList.remove(index);
                    timeCaseAdapter.bindData(true, weekendTimeCaseList);
                }
                setTaskState();
            });
        }
    }

    private void setTaskState() {
        taskStatus.setText("调整后");
    }
}
