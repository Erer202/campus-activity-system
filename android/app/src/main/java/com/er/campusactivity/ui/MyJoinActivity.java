package com.er.campusactivity.ui;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.er.campusactivity.BaseActivity;
import com.er.campusactivity.R;
import com.er.campusactivity.adapter.MyJoinAdapter;
import com.er.campusactivity.entry.MyActivity;
import com.er.campusactivity.entry.User;
import com.er.campusactivity.network.ApiClient;
import com.er.campusactivity.utils.AppApplication;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyJoinActivity extends BaseActivity {
    private RecyclerView rv_my_join;
    private MyJoinAdapter adapter;
    private User currentUser;
    private TextView tv_empty;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_join;
    }

    @Override
    protected void initView() {
        ImageButton btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> finish());

        rv_my_join = findViewById(R.id.rv_my_join);
        tv_empty = findViewById(R.id.tv_empty);
        rv_my_join.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MyJoinAdapter(this, new ArrayList<>());
        rv_my_join.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        currentUser = AppApplication.getCurrentUser();
        loadMyJoinData();
    }

    private void loadMyJoinData() {
        if (currentUser == null) {
            tv_empty.setText("请先登录");
            tv_empty.setVisibility(View.VISIBLE);
            return;
        }

        ApiClient.getApiService().getMyJoinActivities(currentUser.getUserId())
                .enqueue(new Callback<List<MyActivity>>() {
                    @Override
                    public void onResponse(Call<List<MyActivity>> call, Response<List<MyActivity>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<MyActivity> list = response.body();
                            if (list.isEmpty()) {
                                tv_empty.setText("暂无报名记录");
                                tv_empty.setVisibility(View.VISIBLE);
                            } else {
                                tv_empty.setVisibility(View.GONE);
                                adapter.setNewData(list);
                            }
                        } else {
                            tv_empty.setText("获取报名记录失败");
                            tv_empty.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MyActivity>> call, Throwable t) {
                        tv_empty.setText("网络错误：" + t.getMessage());
                        tv_empty.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMyJoinData();
    }
}
