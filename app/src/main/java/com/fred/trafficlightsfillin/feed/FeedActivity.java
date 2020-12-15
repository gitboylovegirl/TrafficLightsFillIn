package com.fred.trafficlightsfillin.feed;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.fred.trafficlightsfillin.R;
import com.fred.trafficlightsfillin.base.BaseRecyclerAdapter;
import com.fred.trafficlightsfillin.base.BaseResponse;
import com.fred.trafficlightsfillin.base.BaseViewHolder;
import com.fred.trafficlightsfillin.base.MyGlideEngine;
import com.fred.trafficlightsfillin.base.RequestApi;
import com.fred.trafficlightsfillin.intersection.bean.ImageResponse;
import com.fred.trafficlightsfillin.network.http.ProRequest;
import com.fred.trafficlightsfillin.network.http.response.ICallback;
import com.fred.trafficlightsfillin.query.bean.RoadResponse;
import com.fred.trafficlightsfillin.utils.DialogUtils;
import com.fred.trafficlightsfillin.utils.GetImagePath;
import com.fred.trafficlightsfillin.utils.SharedPreferenceUtils;
import com.fred.trafficlightsfillin.utils.TimeUtils;
import com.fred.trafficlightsfillin.utils.ToastUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedActivity extends AppCompatActivity {

    @BindView(R.id.road_name)
    TextView roadName;
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
    List<ImageResponse.ImageBean> imageBeans = new ArrayList<>();
    List<RoadResponse.RoadChannel> roadChannels = new ArrayList<>();

    private static final int REQUEST_CODE_CHOOSE = 99;

    int currentPosition;
    @BindView(R.id.area)
    TextView area;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        ButterKnife.bind(this);

        getRoadData();
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

        feedSet.setText("反馈车组："+SharedPreferenceUtils.getInstance().getTeamName());
        feedPerson.setText("反馈人："+SharedPreferenceUtils.getInstance().getName());
        phone.setText("电话："+SharedPreferenceUtils.getInstance().getPhone());

        time.setText(TimeUtils.time10(String.valueOf(System.currentTimeMillis())));

        submit.setOnClickListener(v -> {
            feedSubmit();
        });
        roadName.setOnClickListener(v -> {
            List<String> roadPlaces = new ArrayList<>();
            for (int i = 0; i < roadChannels.size(); i++) {
                roadPlaces.add(roadChannels.get(i).getRoadPlace());
            }
            DialogUtils.showChoiceDialog(FeedActivity.this, roadPlaces, new DialogUtils.OnButtonClickListener() {
                @Override
                public void onPositiveButtonClick() {

                }

                @Override
                public void onNegativeButtonClick() {

                }

                @Override
                public void onChoiceItem(String str, int pos) {
                    roadName.setText(str);
                    type.setText(roadChannels.get(pos).getModelNo());
                    signalType.setText(roadChannels.get(pos).getModelType());
                    area.setText(roadChannels.get(pos).getArea());
                    currentPosition = pos;
                }
            });
        });
    }

    /**
     * 上报
     */
    private void feedSubmit() {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TASK_PAGE))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .addParam("desc",tvFeed.getText().toString().trim())
                .addParam("trafficLightId", String.valueOf(roadChannels.get(currentPosition).getId()))
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
                            roadName.setText(roadChannels.get(0).getRoadPlace());
                            type.setText(roadChannels.get(0).getModelNo());
                            signalType.setText(roadChannels.get(0).getModelType());
                            area.setText(roadChannels.get(0).getArea());

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
            if (index == 0) {
                rlMore.setVisibility(View.VISIBLE);
                rlPicture.setVisibility(View.GONE);
            } else {
                rlMore.setVisibility(View.GONE);
                rlPicture.setVisibility(View.VISIBLE);
            }

            //点击添加图片
            rlMore.setOnClickListener(v -> {
                choicePicture();
            });
        }
    }

    private void choicePicture() {
        // .choose(MimeType.ofImage())
        Matisse.from(FeedActivity.this)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.WEBP))
                .countable(true)
                .maxSelectable(3)
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
                fileData.add(getFilePathFromUri(FeedActivity.this, mSelected.get(i)));
                ImageResponse.ImageBean imageBean = new ImageResponse.ImageBean();
                imageBean.setUri(mSelected.get(i));
                imageBeans.add(1, imageBean);
            }
            pictureAdapter.bindData(true, imageBeans);
            pictureAdapter.notifyDataSetChanged();
            uploadPicture(fileData);
        }

    }

    //上传图片
    private void uploadPicture(List<String> data) {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.UP_IMG) + "/" + roadChannels.get(currentPosition).getId())
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
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
