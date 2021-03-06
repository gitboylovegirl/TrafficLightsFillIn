package com.fred.trafficlightsfillin.feed;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.fred.trafficlightsfillin.intersection.bean.ImageResponse;
import com.fred.trafficlightsfillin.network.http.ProRequest;
import com.fred.trafficlightsfillin.network.http.response.ICallback;
import com.fred.trafficlightsfillin.query.QueryMainActivity;
import com.fred.trafficlightsfillin.query.TaskUpdateActivity;
import com.fred.trafficlightsfillin.query.bean.RoadResponse;
import com.fred.trafficlightsfillin.utils.DialogUtils;
import com.fred.trafficlightsfillin.utils.GetImagePath;
import com.fred.trafficlightsfillin.utils.RoadDataUtil;
import com.fred.trafficlightsfillin.utils.SharedPreferenceUtils;
import com.fred.trafficlightsfillin.utils.TimeUtils;
import com.fred.trafficlightsfillin.utils.ToastUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class FeedActivity extends AppCompatActivity {

    @BindView(R.id.road_name)
    EditText roadName;
    @BindView(R.id.type)
    TextView type;
    @BindView(R.id.signal_type)
    TextView signalType;
    @BindView(R.id.tv_feed)
    EditText tvFeed;
    @BindView(R.id.picture_list)
    RecyclerView pictureList;
    @BindView(R.id.feed_set)
    TextView feedSet;
    @BindView(R.id.feed_person)
    TextView feedPerson;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.submit)
    TextView submit;
    PictureAdapter pictureAdapter;
    private Long trafficLightId = 0L;
    List<ImageResponse.ImageBean> imageBeans = new ArrayList<>();
    List<RoadResponse.RoadChannel> roadChannels;

    private static final int REQUEST_CODE_CHOOSE = 99;

    @BindView(R.id.area)
    TextView area;

    PopupWindow popupWindow;
    RoadPlaceadapter roadPlaceadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        ButterKnife.bind(this);

        roadChannels = RoadDataUtil.getDatatList();
        initView();
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        pictureList.setLayoutManager(layoutManager);
        pictureAdapter = new PictureAdapter();
        pictureList.setAdapter(pictureAdapter);
        imageBeans.add(0, new ImageResponse.ImageBean());
        pictureAdapter.bindData(true,imageBeans);

        pictureAdapter.setOnItemClickListener((adapter, holder, itemView, index) -> {
            DialogUtils.showPictureDialog(FeedActivity.this, imageBeans, index,2, new DialogUtils.OnButtonClickListener() {

                @Override
                public void onPositiveButtonClick() {

                }

                @Override
                public void onNegativeButtonClick() {

                }

                @Override
                public void onChoiceItem(String str, int pos) {
                    imageBeans.remove(pos);
                    pictureAdapter.bindData(true, imageBeans);
                    pictureAdapter.notifyDataSetChanged();
                }
            });
        });
        feedSet.setText("反馈车组："+SharedPreferenceUtils.getInstance().getTeamName());
        feedPerson.setText("反馈人："+SharedPreferenceUtils.getInstance().getName());
        phone.setText("电话："+SharedPreferenceUtils.getInstance().getPhone());

        roadPlaceadapter=new RoadPlaceadapter();

        time.setText(TimeUtils.time10(String.valueOf(System.currentTimeMillis())));

        submit.setOnClickListener(v -> {
            feedSubmit();
        });

        roadName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(popupWindow!=null&&popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                if(TextUtils.isEmpty(s.toString().trim())){
                    return;
                }

                if(roadChannels == null || roadChannels.size() == 0){
                    return;
                }
                List<RoadResponse.RoadChannel> roadData= new ArrayList<>();
                for (int i = 0; i < roadChannels.size(); i++) {
                    if(roadChannels.get(i).getRoadPlace().contains(s.toString().trim())&&roadChannels.get(i).getRoadPlace()!=null){
                        roadData.add(roadChannels.get(i));
                    }
                }

                roadPlaceadapter.bindData(true, roadData);
                roadPlaceadapter.setOnItemClickListener((adapter, holder, itemView, index) -> {
                    roadName.setText(roadData.get(index).getRoadPlace());
                    type.setText(roadData.get(index).getModelNo());
                    signalType.setText(roadData.get(index).getModelType());
                    area.setText(roadData.get(index).getArea());
                    trafficLightId = roadData.get(index).getId();
                    popupWindow.dismiss();
                });
                showPopupWindow(roadName);
                roadName.setFocusable(true);

            }
        });
    }


    private void showPopupWindow(View view) {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.layout_popupwindow, null);

        RecyclerView content=contentView.findViewById(R.id.content);

        content.setLayoutManager(new LinearLayoutManager(FeedActivity.this));
        content.setAdapter(roadPlaceadapter);

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

    class RoadPlaceadapter extends BaseRecyclerAdapter<RoadResponse.RoadChannel>{

        @Override
        public int bindView(int viewType) {
            return R.layout.layout_road_place_item;
        }

        @Override
        public void onBindHolder(BaseViewHolder holder, @Nullable RoadResponse.RoadChannel s, int index) {
            TextView tv_place=holder.obtainView(R.id.tv_place);

            tv_place.setText(s.getRoadPlace());
        }
    }

    /**
     * 上报
     */
    private void feedSubmit() {
        String content = tvFeed.getText() == null ? "" : tvFeed.getText().toString().trim();
        if("".equals(content) || content.length() < 20){
            ToastUtil.showMsg(FeedActivity.this, "反馈内容描述必须大于20个字！");
            return;
        }
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.FEED_ADD))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh-token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .addParam("desc",content)
                .addParam("trafficLightId", trafficLightId+"")
                .build()
                .postAsync(new ICallback<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        if (response.code==0){
                            ToastUtil.showMsg(FeedActivity.this, "反馈成功");
                            finish();
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
            ImageView iv_delete=holder.obtainView(R.id.iv_delete);

            if (index == imageBeans.size()-1) {
                rlMore.setVisibility(View.VISIBLE);
                rlPicture.setVisibility(View.GONE);
            } else {
                rlMore.setVisibility(View.GONE);
                rlPicture.setVisibility(View.VISIBLE);
                iv_delete.setVisibility(View.VISIBLE);

                //删除
                iv_delete.setOnClickListener(v -> {
                    imageBeans.remove(index);
                    pictureAdapter.bindData(true, imageBeans);
                    pictureAdapter.notifyDataSetChanged();
                });

                String pictureUrl = RequestApi.BASE_OFFICIAL_URL + RequestApi.DOWN_IMG + "/" + imageBean.path;
                if (imageBean.getUri() != null && !TextUtils.isEmpty(imageBean.getUri().toString())) {
                    String filePath = GetImagePath.getPath(FeedActivity.this, imageBean.getUri());
                    Bitmap bm = GetImagePath.displayImage(FeedActivity.this, filePath);
                    if (bm == null) {
                        ToastUtil.showShort(FeedActivity.this, "获取图片失败");
                        return;
                    }
                    picture.setImageBitmap(bm);
                } else {
                    GlideUrl glideUrl = new GlideUrl(pictureUrl, new LazyHeaders.Builder()
                            .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                            .build());

                    Glide.with(FeedActivity.this)
                            .load(glideUrl)
                            .into(picture);
                }
            }

            //点击添加图片
            rlMore.setOnClickListener(v -> {
                if(trafficLightId <= 0){
                    ToastUtil.showShort(FeedActivity.this, "请先选择路口！");
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (FeedActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                        //如果没有写sd卡权限
                        FeedActivity.this.requestPermissions(
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                100);
                    }else {
                        choicePicture();
                    }
                }else{
                    choicePicture();
                }
            });
        }
    }

    private void choicePicture() {
        // .choose(MimeType.ofImage())
        Matisse.from(FeedActivity.this)
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
            for (int i = 0; i < mSelected.size(); i++) {
                Log.i("图片", mSelected.get(i).getPath());
                List<String> updateData=new ArrayList<>();
                updateData.add(getFilePathFromUri(FeedActivity.this, mSelected.get(i)));
                uploadPicture(updateData);
                ImageResponse.ImageBean imageBean = new ImageResponse.ImageBean();
                imageBean.setUri(mSelected.get(i));
                imageBeans.add(0, imageBean);
            }
            pictureAdapter.bindData(true, imageBeans);
            pictureAdapter.notifyDataSetChanged();
        }

    }

    //上传图片
    private void uploadPicture(List<String> data) {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.UP_IMG) + "/" + trafficLightId)
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh-token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .addUploadFiles(data)
                .build()
                .uploadFiles(new ICallback<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        if (response.code == 0) {
                            ToastUtil.showMsg(FeedActivity.this, "图片上传成功");
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                        ToastUtil.showMsg(FeedActivity.this, "图片上传失败");
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
}
