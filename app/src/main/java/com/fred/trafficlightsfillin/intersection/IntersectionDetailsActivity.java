package com.fred.trafficlightsfillin.intersection;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fred.trafficlightsfillin.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IntersectionDetailsActivity extends AppCompatActivity {

    @BindView(R.id.intersection_name)
    TextView intersectionName;
    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.light_type)
    TextView lightType;
    @BindView(R.id.light_type_two)
    TextView lightTypeTwo;
    @BindView(R.id.record_type)
    TextView recordType;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.record_from)
    TextView recordFrom;
    @BindView(R.id.describe)
    TextView describe;
    @BindView(R.id.receiving_orders)
    TextView receivingOrders;
    @BindView(R.id.back)
    TextView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intersection_details);
        ButterKnife.bind(this);
    }
}
