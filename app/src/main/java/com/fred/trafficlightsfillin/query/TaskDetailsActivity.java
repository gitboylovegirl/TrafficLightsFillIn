package com.fred.trafficlightsfillin.query;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.fred.trafficlightsfillin.R;
import com.fred.trafficlightsfillin.base.RequestApi;
import com.fred.trafficlightsfillin.network.http.ProRequest;
import com.fred.trafficlightsfillin.network.http.response.ICallback;
import com.fred.trafficlightsfillin.record.bean.TaskDetailsChannel;
import com.fred.trafficlightsfillin.utils.SharedPreferenceUtils;
import com.fred.trafficlightsfillin.utils.TimeUtils;

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
    @BindView(R.id.picture)
    RecyclerView picture;
    @BindView(R.id.road_state)
    TextView roadState;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.team)
    TextView team;
    @BindView(R.id.task_state)
    TextView taskState;

    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        ButterKnife.bind(this);
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
                            modelNumber.setText(taskDetails.modelNo);
                            modelType.setText(taskDetails.modelType);
                            team.setText(taskDetails.teamName);

                            roadPosition.setText(taskDetails.location);
                            roadType.setText(taskDetails.roadPlaceType);
                            taskFrom.setText(taskDetails.source);
                            desc.setText(taskDetails.desc);
                            area.setText(taskDetails.area);

                            roadState.setText(taskDetails.state);

                            time.setText(TimeUtils.time7(taskDetails.trafficLightLastUpdateTime));
                            team.setText(taskDetails.teamName);
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                    }
                });
    }
}
