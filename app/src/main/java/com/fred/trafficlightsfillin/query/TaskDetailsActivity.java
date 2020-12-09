package com.fred.trafficlightsfillin.query;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.fred.trafficlightsfillin.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        ButterKnife.bind(this);
    }
}
