package com.fred.trafficlightsfillin.record;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fred.trafficlightsfillin.R;
import com.fred.trafficlightsfillin.base.BaseResponse;
import com.fred.trafficlightsfillin.base.RequestApi;
import com.fred.trafficlightsfillin.network.http.ProRequest;
import com.fred.trafficlightsfillin.network.http.response.ICallback;
import com.fred.trafficlightsfillin.record.bean.TaskDetailsChannel;
import com.fred.trafficlightsfillin.utils.SharedPreferenceUtils;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_new_details);
        ButterKnife.bind(this);

        initData();

        receiving.setOnClickListener(v -> {
            submit();
        });
    }


    private void initData() {
        id=getIntent().getStringExtra("id");
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TASK_DETAILS)+"/"+id)
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .build()
                .getAsyncTwo(new ICallback<TaskDetailsChannel>() {
                    @Override
                    public void onSuccess(TaskDetailsChannel response) {
                        Log.e("fred  新数据：", response.toString());
                        if(response.data!=null){
                            TaskDetailsChannel.TaskDetails taskDetails = response.data;
                            roadName.setText(taskDetails.roadPlace);
                            modelOne.setText(taskDetails.modelNo);
                            modelTwo.setText(taskDetails.modelType);

                            team.setText(taskDetails.teamName);
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
    private void submit(){
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.TASK_STATE))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh_token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .addParam("taskId",id)
                .addParam("state","2")
                .build()
                .putAsync(new ICallback<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        if(response.code==0){

                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                    }
                });
    }
}
