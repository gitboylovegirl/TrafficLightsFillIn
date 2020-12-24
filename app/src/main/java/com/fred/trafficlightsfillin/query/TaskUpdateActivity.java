package com.fred.trafficlightsfillin.query;

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
import com.fred.trafficlightsfillin.query.bean.UpPictureResponse;
import com.fred.trafficlightsfillin.record.RecordNewDetailsActivity;
import com.fred.trafficlightsfillin.record.bean.TaskDetailsChannel;
import com.fred.trafficlightsfillin.utils.DialogUtils;
import com.fred.trafficlightsfillin.utils.GetImagePath;
import com.fred.trafficlightsfillin.utils.SharedPreferenceUtils;
import com.fred.trafficlightsfillin.utils.ToastUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * 任务上传界面
 */
public class TaskUpdateActivity extends AppCompatActivity {
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
    @BindView(R.id.task_from)
    TextView taskFrom;
    @BindView(R.id.desc)
    TextView desc;
    @BindView(R.id.picture)
    RecyclerView picture;
    @BindView(R.id.task_end)
    ImageView taskEnd;
    @BindView(R.id.state)
    TextView state;
    @BindView(R.id.end_time)
    TextView endTime;
    @BindView(R.id.submit)
    TextView submit;

    PictureAdapter pictureAdapter;
    String id;
    @BindView(R.id.input_better)
    EditText inputBetter;
    @BindView(R.id.remark)
    TextView remark;
    List<ImageResponse.ImageBean> imageBeans=new ArrayList<>();
    String trafficLightId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_update);
        ButterKnife.bind(this);
        trafficLightId = getIntent().getStringExtra("trafficLightId");

        initView();
        //initPictrue();
        initData();
        imageBeans.add(imageBeans.size(), new ImageResponse.ImageBean());
        pictureAdapter.bindData(true, imageBeans);
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
                        if (response.data != null) {
                            TaskDetailsChannel.TaskDetails taskDetails = response.data;
                            number.setText("编号："+taskDetails.trafficLightNo);
                            roadName.setText(taskDetails.roadPlace);
                            modelNumber.setText(taskDetails.modelNo);
                            modelType.setText(taskDetails.modelType);

                            roadPosition.setText(taskDetails.location);
                            roadType.setText(taskDetails.roadPlaceType);
                            taskFrom.setText(taskDetails.source);
                            desc.setText(taskDetails.desc);
                            area.setText(taskDetails.area);
                            remark.setText(taskDetails.remark);
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                    }
                });
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        picture.setLayoutManager(layoutManager);
        pictureAdapter = new PictureAdapter();
        picture.setAdapter(pictureAdapter);
        
        //上传任务
        submit.setOnClickListener(v -> {
            DialogUtils.showCurrencyDialog(this, "是否确认上传？", new DialogUtils.OnButtonClickListener() {
                @Override
                public void onPositiveButtonClick() {
                    changeState();
                }

                @Override
                public void onNegativeButtonClick() {

                }

                @Override
                public void onChoiceItem(String str, int pos) {

                }
            });
        });
    }

    /**
     * 完成
     */
    private void changeState() {
        String cause = inputBetter.getText() == null ? "" : inputBetter.getText().toString().trim();
        if("".equals(cause) || cause.length() < 10){
            ToastUtil.showMsg(TaskUpdateActivity.this, "配时优化调整内容描述必须大于10个字！");
            return;
        }
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TASK_STATE))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .addParam("taskId", id)
                .addParam("state", "3")
                .addParam("cause",inputBetter.getText().toString().trim())
                .build()
                .putAsync(new ICallback<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        ToastUtil.showMsg(TaskUpdateActivity.this, response.msg);
                        finish();
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
        String trafficLightId = getIntent().getStringExtra("trafficLightId");
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
                            Log.e("fred",imageBeans.size()+"  数量");
                            if (pictureAdapter==null){
                                pictureAdapter=new PictureAdapter();
                            }
                            pictureAdapter.bindData(true, imageBeans);
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
                    delPicture(imageBeans.get(index).id);
                    imageBeans.remove(index);
                    pictureAdapter.bindData(true, imageBeans);
                    pictureAdapter.notifyDataSetChanged();
                });

                String pictureUrl = RequestApi.BASE_OFFICIAL_URL+RequestApi.DOWN_IMG + "/" + imageBean.path;
                if(imageBean.getUri()!=null&&!TextUtils.isEmpty(imageBean.getUri().toString())){
                    String filePath = GetImagePath.getPath(TaskUpdateActivity.this, imageBean.getUri());
                    Bitmap bm = GetImagePath.displayImage(TaskUpdateActivity.this,filePath);
                    if(bm==null){
                        ToastUtil.showShort(TaskUpdateActivity.this,"获取图片失败");
                        return;
                    }
                    picture.setImageBitmap(bm);
                }else {
                    GlideUrl glideUrl = new GlideUrl(pictureUrl, new LazyHeaders.Builder()
                            .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                            .build());

                    Glide.with(TaskUpdateActivity.this)
                            .load(glideUrl)
                            .into(picture);
                }
            }

            //点击添加图片
            rlMore.setOnClickListener(v -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (TaskUpdateActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                        //如果没有写sd卡权限
                        TaskUpdateActivity.this.requestPermissions(
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                100);
                    }else {
                        choicePicture();
                    }
                }else{
                    choicePicture();
                }            });
        }
    }

    private void choicePicture() {
        // .choose(MimeType.ofImage())
        Matisse.from(TaskUpdateActivity.this)
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
                fileData.add(getFilePathFromUri(TaskUpdateActivity.this, mSelected.get(i)));
                ImageResponse.ImageBean imageBean=new ImageResponse.ImageBean();
                imageBean.setUri(mSelected.get(i));
                imageBeans.add(0,imageBean);
            }
            pictureAdapter.bindData(true,imageBeans);
            pictureAdapter.notifyDataSetChanged();

            for (int i = 0; i < fileData.size(); i++) {
                List<String> updateData=new ArrayList<>();
                updateData.add(fileData.get(i));
                uploadPicture(fileData);
            }
        }

    }

    //上传图片
    private void uploadPicture(List<String> data) {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.UP_IMG) + "/" + trafficLightId)
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .addUploadFiles(data)
                .build()
                .uploadFiles(new ICallback<UpPictureResponse>() {
                    @Override
                    public void onSuccess(UpPictureResponse response) {
                        if (response.code == 0) {
                            //initPictrue();
                            pictureAdapter.bindData(true, imageBeans);
                            pictureAdapter.notifyDataSetChanged();
                            ToastUtil.showMsg(TaskUpdateActivity.this, "图片上传成功");
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                        ToastUtil.showMsg(TaskUpdateActivity.this, "图片上传失败");
                    }
                });
    }

    //删除图片
    private void delPicture(String imgID) {
        if(TextUtils.isEmpty(imgID)){
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
                            //initPictrue();
                            ToastUtil.showMsg(TaskUpdateActivity.this, "图片删除成功");
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                        ToastUtil.showMsg(TaskUpdateActivity.this, "图片删除失败");
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
