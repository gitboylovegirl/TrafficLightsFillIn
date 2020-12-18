package com.fred.trafficlightsfillin.record;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fred.trafficlightsfillin.R;
import com.fred.trafficlightsfillin.base.BaseResponse;
import com.fred.trafficlightsfillin.base.RequestApi;
import com.fred.trafficlightsfillin.network.http.ProRequest;
import com.fred.trafficlightsfillin.network.http.response.ICallback;
import com.fred.trafficlightsfillin.record.bean.TaskDetailsChannel;
import com.fred.trafficlightsfillin.utils.SharedPreferenceUtils;
import com.fred.trafficlightsfillin.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordNewDetailsActivity extends AppCompatActivity {

    @BindView(R.id.road_name)
    TextView roadName;
    @BindView(R.id.road_place)
    TextView roadPlace;
    @BindView(R.id.model_one)
    TextView modelOne;
    @BindView(R.id.model_two)
    TextView modelTwo;
    @BindView(R.id.team)
    TextView team;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.task)
    TextView task;
    @BindView(R.id.sec)
    TextView sec;
    @BindView(R.id.receiving)
    TextView receiving;
    String id;
    @BindView(R.id.go_to)
    TextView goTo;
    @BindView(R.id.finish)
    TextView finish;
    @BindView(R.id.bottom_view)
    LinearLayout bottomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_new_details);
        ButterKnife.bind(this);

        initData();

        receiving.setOnClickListener(v -> {
            submit();
        });

        String state = getIntent().getStringExtra("state");
       if ("1".equals(state)) {
            //"未接单
            bottomView.setVisibility(View.GONE);
            receiving.setVisibility(View.VISIBLE);
        } else if ("2".equals(state)) {
            //未完成
            bottomView.setVisibility(View.VISIBLE);
            receiving.setVisibility(View.GONE);
        }
        //点击完成
        finish.setOnClickListener(v -> {
             changeState();
        });
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
                        Log.e("fred  新数据：", response.toString());
                        if (response.data != null) {
                            TaskDetailsChannel.TaskDetails taskDetails = response.data;
                            roadName.setText(taskDetails.roadPlace);
                            modelOne.setText(taskDetails.modelNo);
                            modelTwo.setText(taskDetails.modelType);
                            team.setText(taskDetails.teamName);

                            name.setText(taskDetails.engineerName);
                            phone.setText(taskDetails.carNumber);
                            task.setText(taskDetails.source);
                            sec.setText(taskDetails.desc);
                            roadPlace.setText(taskDetails.area);
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                    }
                });
    }

    /**
     * 接单
     */
    private void submit() {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TASK_STATE))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .addParam("taskId", id)
                .addParam("state", "2")
                .build()
                .putAsync(new ICallback<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        if(response.code==0){
                            bottomView.setVisibility(View.GONE);
                            receiving.setVisibility(View.VISIBLE);
                        }
                        ToastUtil.showMsg(RecordNewDetailsActivity.this, response.msg);
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                    }
                });
    }

    /**
     * 完成
     */
    private void changeState() {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TASK_STATE))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .addParam("taskId", id)
                .addParam("state", "3")
                .build()
                .putAsync(new ICallback<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        if(response.code==0){
                            bottomView.setVisibility(View.GONE);
                            receiving.setVisibility(View.VISIBLE);
                        }
                        ToastUtil.showMsg(RecordNewDetailsActivity.this, response.msg);
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                    }
                });
    }
}
