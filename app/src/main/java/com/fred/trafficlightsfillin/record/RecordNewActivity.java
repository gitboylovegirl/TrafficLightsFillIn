package com.fred.trafficlightsfillin.record;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fred.trafficlightsfillin.R;
import com.fred.trafficlightsfillin.base.BaseRecyclerAdapter;
import com.fred.trafficlightsfillin.base.BaseViewHolder;
import com.fred.trafficlightsfillin.base.RequestApi;
import com.fred.trafficlightsfillin.login.LocationResponse;
import com.fred.trafficlightsfillin.login.LoginResponse;
import com.fred.trafficlightsfillin.network.http.ProRequest;
import com.fred.trafficlightsfillin.network.http.response.ICallback;
import com.fred.trafficlightsfillin.record.bean.NewRecordChannel;
import com.fred.trafficlightsfillin.record.bean.NewRecordResponse;
import com.fred.trafficlightsfillin.utils.AccountManager;
import com.fred.trafficlightsfillin.utils.SharedPreferenceUtils;
import com.fred.trafficlightsfillin.utils.TimeUtils;
import com.scwang.smartrefresh.header.WaterDropHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordNewActivity extends AppCompatActivity {

    @BindView(R.id.content)
    RecyclerView content;
    @BindView(R.id.back)
    TextView back;

    @BindView(R.id.smartRefresh)
    SmartRefreshLayout smartRefresh;
    NewRecordAdapter recordAdapter;
    List<NewRecordChannel> list = new ArrayList<>();
    int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_new);
        ButterKnife.bind(this);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        page = 1;
        initNewData();
    }

    private void initView() {
        back.setOnClickListener(v -> {
            finish();
        });

        recordAdapter = new NewRecordAdapter();
        content.setLayoutManager(new LinearLayoutManager(RecordNewActivity.this));
        content.setAdapter(recordAdapter);

        //设置下拉样式  可自定义
        smartRefresh.setRefreshHeader(new ClassicsHeader(this));
        smartRefresh.setRefreshFooter(new ClassicsFooter(this));
        //下拉刷新
        smartRefresh.setOnRefreshListener(refreshLayout -> {
            page = 1;
            initNewData();
        });
        //上拉加载更多
        smartRefresh.setOnLoadMoreListener(refreshLayout -> {
            page++;
            initNewData();
        });
        initNewData();
        recordAdapter.setOnItemClickListener((adapter, holder, itemView, index) -> {
            Intent intent =new Intent(RecordNewActivity.this,RecordNewDetailsActivity.class);
            intent.putExtra("taskId",list.get(index).getId());
            intent.putExtra("state",list.get(index).state);
            startActivity(intent);
        });
    }

    /**
     * 请求数据
     */
    private void initNewData() {
        ProRequest.get().setUrl(RequestApi.getUrl(RequestApi.NEW_LIST))
                .addHeader("authorization", SharedPreferenceUtils.getInstance().getToken())
                .addHeader("refresh-token", SharedPreferenceUtils.getInstance().getrefreshToken())
                .addParam("pageNum", String.valueOf(page))
                .addParam("pageSize", "50")
                .build()
                .postAsync(new ICallback<NewRecordResponse>() {
                    @Override
                    public void onSuccess(NewRecordResponse response) {
                        if(response.code==401001){
                            return;
                        }
                        if (page == 1) {
                            list.clear();
                            list=response.data.list;
                        }else {
                            list.addAll(response.data.list);
                        }
                        recordAdapter.bindData(true,list);
                        smartRefresh.finishLoadMore();
                        smartRefresh.finishRefresh();
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                    }
                });
    }

    class NewRecordAdapter extends BaseRecyclerAdapter<NewRecordChannel> {

        @Override
        public int bindView(int viewType) {
            return R.layout.layout_new_record_item;
        }

        @Override
        public void onBindHolder(BaseViewHolder holder, @Nullable NewRecordChannel newRecordChannel, int index) {
            TextView road_name = holder.obtainView(R.id.road_name);
            TextView task_status = holder.obtainView(R.id.task_status);
            TextView task_from = holder.obtainView(R.id.task_from);
            TextView time = holder.obtainView(R.id.time);

            time.setText(TimeUtils.time7(String.valueOf(newRecordChannel.createTime)));
            task_from.setText("来源：" + newRecordChannel.source);
            road_name.setText(newRecordChannel.roadPlace);
            if("0".equals(newRecordChannel.state)){
                task_status.setText("后台取消");
            }else if("1".equals(newRecordChannel.state)){
                task_status.setText("未接单");
                task_status.setTextColor(Color.parseColor("#FF8631"));
            }else if("2".equals(newRecordChannel.state)){
                task_status.setText("未完成");
                task_status.setTextColor(Color.parseColor("#66CC66"));
            }else if("3".equals(newRecordChannel.state)){
                task_status.setText("配时表未更新");
                task_status.setTextColor(Color.parseColor("#FF8631"));
            }else if("4".equals(newRecordChannel.state)){
                task_status.setTextColor(Color.parseColor("#FF8631"));
                task_status.setText("完成已上传");
            }
        }
    }
}