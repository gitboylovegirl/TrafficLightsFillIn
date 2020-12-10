package com.fred.trafficlightsfillin.intersection;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.fred.trafficlightsfillin.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 配时编辑
 */
public class TimingEditorActivity extends AppCompatActivity {

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
    @BindView(R.id.better)
    TextView better;
    @BindView(R.id.task_end)
    ImageView taskEnd;
    @BindView(R.id.end_time)
    TextView endTime;
    @BindView(R.id.submit)
    TextView submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timing_editor);
        ButterKnife.bind(this);
    }
}
