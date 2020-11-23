package com.fred.trafficlightsfillin.record;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.fred.trafficlightsfillin.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordNewActivity extends AppCompatActivity {

    @BindView(R.id.content)
    RecyclerView content;
    @BindView(R.id.back)
    TextView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_new);
        ButterKnife.bind(this);
    }
}