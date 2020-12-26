package com.fred.trafficlightsfillin.query;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.fred.trafficlightsfillin.base.BaseViewHolder;
import com.fred.trafficlightsfillin.base.RequestApi;
import com.fred.trafficlightsfillin.intersection.TimingDetailsActivity;
import com.fred.trafficlightsfillin.intersection.bean.ImageResponse;
import com.fred.trafficlightsfillin.network.http.ProRequest;
import com.fred.trafficlightsfillin.network.http.response.ICallback;
import com.fred.trafficlightsfillin.record.bean.TaskDetailsChannel;
import com.fred.trafficlightsfillin.utils.DialogUtils;
import com.fred.trafficlightsfillin.utils.SharedPreferenceUtils;
import com.fred.trafficlightsfillin.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskDetailsActivity extends AppCompatActivity {

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
    @BindView(R.id.cad_img_lable)
    TextView cadImgLable;
    @BindView(R.id.picture)
    RecyclerView picture;

    @BindView(R.id.task_team_name)
    TextView taskTeamName;
    @BindView(R.id.task_engineer_name)
    TextView taskEngineerName;
    @BindView(R.id.task_car)
    TextView taskCar;
    @BindView(R.id.task_phone)
    TextView taskPhone;

    /*@BindView(R.id.road_state)
    TextView roadState;*/
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.team)
    TextView team;
    @BindView(R.id.task_state)
    TextView taskState;
    @BindView(R.id.better)
    TextView better;
    PictureAdapter pictureAdapter;
    List<ImageResponse.ImageBean> imageBeans=new ArrayList<>();
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        ButterKnife.bind(this);

        initView();
        initData();
        initPictrue();
    }

    private void initView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        picture.setLayoutManager(layoutManager);
        pictureAdapter = new PictureAdapter();
        picture.setAdapter(pictureAdapter);

        pictureAdapter.setOnItemClickListener((adapter, holder, itemView, index) -> {
            DialogUtils.showPictureDialog(TaskDetailsActivity.this,imageBeans , index,1, new DialogUtils.OnButtonClickListener() {

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
    }
    private void initData() {
        id=getIntent().getStringExtra("id");
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TASK_DETAILS)+"/"+id)
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh-token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .getAsyncTwo(new ICallback<TaskDetailsChannel>() {
                    @Override
                    public void onSuccess(TaskDetailsChannel response) {
                        if(response.data!=null){
                            TaskDetailsChannel.TaskDetails taskDetails = response.data;
                            roadName.setText(taskDetails.roadPlace);
                            modelNumber.setText(taskDetails.modelNo);
                            modelType.setText(taskDetails.modelType);
                            roadType.setText(taskDetails.roadPlaceType);
                            area.setText(taskDetails.area);
                            team.setText(taskDetails.teamName);
                            roadPosition.setText(taskDetails.location);
                            taskFrom.setText(taskDetails.source);
                            desc.setText(taskDetails.desc);
                            better.setText(taskDetails.getCause());
                            taskState.setText(intStr2TaskState(taskDetails.state));

                            taskPhone.setText(taskDetails.engineerPhone);
                            taskTeamName.setText(taskDetails.teamName);
                            taskEngineerName.setText(taskDetails.engineerName);
                            taskCar.setText(taskDetails.carNumber);

                            if(taskDetails.updateTime == null || "".equals(taskDetails.updateTime.trim())){
                                time.setText("");
                            }else{
                                time.setText(TimeUtils.time7(taskDetails.updateTime));
                            }
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                    }
                });
    }


    private String intStr2RoadState(String state){
        //0异常 1正常 2停用
        if("0".equals(state)){
            return "异常";
        }else if("1".equals(state)){
            return "正常";
        }else if("2".equals(state)){
            return "停用";
        }
        return "未知";
    }

    private String intStr2TaskState(String state){
        //任务状态 0 后台取消 1 未接单、2 未完成、3 配时表未更新、4 完成已上传
        if("0".equals(state)){
            return "后台取消";
        }else if("1".equals(state)){
            return "未接单";
        }else if("2".equals(state)){
            return "未完成";
        }else if("3".equals(state)){
            return "配时表未更新";
        }else if("4".equals(state)){
            return "完成已上传";
        }
        return "未知";
    }

    /**
     * 获取现场图片
     */
    private void initPictrue() {
        String  trafficLightId = getIntent().getStringExtra("trafficLightId");
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
                            pictureAdapter.bindData(true,response.data);
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

            String pictureUrl =RequestApi.BASE_OFFICIAL_URL+RequestApi.DOWN_IMG + "/" + imageBean.path;

            GlideUrl glideUrl = new GlideUrl(pictureUrl, new LazyHeaders.Builder()
                    .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                    .build());

            Glide.with(TaskDetailsActivity.this)
                    .load(glideUrl)
                    .into(picture);
        }
    }
}
