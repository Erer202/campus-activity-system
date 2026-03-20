package com.er.campusactivity.ui;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.er.campusactivity.BaseActivity;
import com.er.campusactivity.R;
import com.er.campusactivity.adapter.MyPublishAdapter;
import com.er.campusactivity.db.ActivityDao;
import com.er.campusactivity.entry.MyActivity;
import com.er.campusactivity.entry.User;
import com.er.campusactivity.network.ApiClient;
import com.er.campusactivity.utils.AppApplication;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPublishActivity extends BaseActivity {
    private static final int RESULT_OK = -1;  // 系统常量
    private RecyclerView rv_my_publish;
    private MyPublishAdapter adapter;
    private ActivityDao activityDao;
    private User currentUser;

    private TextView tv_empty;  // 空状态提示

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_publish;
    }

    @Override
    protected void initView() {
        // 返回按钮
        ImageButton btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> finish());

        rv_my_publish = findViewById(R.id.rv_my_publish);
        tv_empty = findViewById(R.id.tv_empty);

        rv_my_publish.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyPublishAdapter(new ArrayList<>(),this);
        rv_my_publish.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        activityDao = new ActivityDao(this);
        currentUser = AppApplication.getCurrentUser();

        if (currentUser == null) {
            showEmptyView("请先登录");
            return;
        }

        loadMyPublishData();
    }

    // 加载我发布的活动列表
    private void loadMyPublishData() {
        ApiClient.getApiService().getMyPublishedActivities(currentUser.getUserId())
                .enqueue(new Callback<List<MyActivity>>() {
                    @Override
                    public void onResponse(Call<List<MyActivity>> call, Response<List<MyActivity>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<MyActivity> list = response.body();

                            if (list.isEmpty()) {
                                showEmptyView("暂无发布记录");
                            } else {
                                hideEmptyView();
                                adapter.setNewData(list);
                            }
                        } else {
                            showEmptyView("获取发布记录失败");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MyActivity>> call, Throwable t) {
                        showEmptyView("网络错误：" + t.getMessage());
                    }
                });
    }

    private void showEmptyView(String tip) {
        tv_empty.setVisibility(View.VISIBLE);
        tv_empty.setText(tip);
        rv_my_publish.setVisibility(View.GONE);
    }

    private void hideEmptyView() {
        tv_empty.setVisibility(View.GONE);
        rv_my_publish.setVisibility(View.VISIBLE);
    }

    // 每次页面可见时都刷新一次数据
    @Override
    protected void onResume() {
        super.onResume();
        if (currentUser == null) {
            currentUser = AppApplication.getCurrentUser();
        }
        if (currentUser != null) {
            loadMyPublishData();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            loadMyPublishData();  // 刷新自己的列表
            setResult(RESULT_OK); // 传递给上层 UserCenterActivity
        }
    }

}