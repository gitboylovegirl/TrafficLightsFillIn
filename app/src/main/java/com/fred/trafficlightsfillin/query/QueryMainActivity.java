package com.fred.trafficlightsfillin.query;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.fred.trafficlightsfillin.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QueryMainActivity extends AppCompatActivity {

    @BindView(R.id.start_time)
    TextView startTime;
    @BindView(R.id.end_ime)
    TextView endIme;
    @BindView(R.id.team)
    TextView team;
    @BindView(R.id.from)
    TextView from;
    @BindView(R.id.query_one)
    TextView queryOne;
    @BindView(R.id.query_two)
    TextView queryTwo;
    @BindView(R.id.content)
    RecyclerView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_main);
        ButterKnife.bind(this);
    }
}
