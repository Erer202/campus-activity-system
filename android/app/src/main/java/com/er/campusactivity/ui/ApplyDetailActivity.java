package com.er.campusactivity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.er.campusactivity.BaseActivity;
import com.er.campusactivity.R;
import com.er.campusactivity.adapter.ApplyDetailAdapter;
import com.er.campusactivity.db.ApplyDao;
import com.er.campusactivity.entry.ApiMessage;
import com.er.campusactivity.entry.ApplicantInfo;
import com.er.campusactivity.entry.MyActivity;
import com.er.campusactivity.entry.User;
import com.er.campusactivity.network.ApiClient;
import com.er.campusactivity.utils.AppApplication;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApplyDetailActivity extends BaseActivity {

    private RecyclerView rv_applicant_list;
    private ApplyDetailAdapter adapter;
    private ApplyDao applyDao;

    private TextView tv_empty_applicant;  // 空状态提示

    private MyActivity activity;  // 当前活动
    private EditText et_student_id;
    private Button btn_add_applicant;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_apply_detail;
    }

    @Override
    protected void initView() {
        // 返回按钮
        ImageButton btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> finish());

        // 手动添加报名者
        et_student_id = findViewById(R.id.et_student_id);
        btn_add_applicant = findViewById(R.id.btn_add_applicant);
        btn_add_applicant.setOnClickListener(v -> {
            String studentId = et_student_id.getText().toString().trim();
            if (studentId.isEmpty()) {
                showToast("学号不能为空");
                return;
            }
            adminToSignUp(studentId);
        });

        rv_applicant_list = findViewById(R.id.rv_applicant_list);
        tv_empty_applicant = findViewById(R.id.tv_empty_applicant);

        rv_applicant_list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ApplyDetailAdapter(this, new ArrayList<>(), activity);
        rv_applicant_list.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        // 从 Intent 获取传入的活动对象
        activity = (MyActivity) getIntent().getSerializableExtra("activity");

        if (activity == null) {
            showToast("信息获取失败");
            finish();
            return;
        }

        loadApplicants();
    }

    // 加载报名者列表

    private void loadApplicants() {
        User currentUser = AppApplication.getCurrentUser();
        if (currentUser == null) {
            showEmptyView("请先登录");
            return;
        }

        ApiClient.getApiService()
                .getApplicantsForActivity(activity.getId(), currentUser.getUserId())
                .enqueue(new Callback<List<ApplicantInfo>>() {
                    @Override
                    public void onResponse(Call<List<ApplicantInfo>> call, Response<List<ApplicantInfo>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<ApplicantInfo> list = response.body();
                            if (list.isEmpty()) {
                                showEmptyView("暂无报名者");
                            } else {
                                hideEmptyView();
                                adapter.setNewData(list);
                            }
                        } else {
                            showEmptyView("获取报名者失败");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ApplicantInfo>> call, Throwable t) {
                        showEmptyView("网络错误：" + t.getMessage());
                    }
                });
    }

    private void showEmptyView(String tip) {
        tv_empty_applicant.setVisibility(View.VISIBLE);
        tv_empty_applicant.setText(tip);
        rv_applicant_list.setVisibility(View.GONE);
    }

    private void hideEmptyView() {
        tv_empty_applicant.setVisibility(View.GONE);
        rv_applicant_list.setVisibility(View.VISIBLE);
    }

    // 管理由手动添加报名
    private void adminToSignUp(String studentId) {
        User currentUser = AppApplication.getCurrentUser();
        if (currentUser == null) {
            showToast("请先登录");
            return;
        }

        ApiClient.getApiService()
                .adminAddApplicant(currentUser.getUserId(), studentId, activity.getId())
                .enqueue(new Callback<ApiMessage>() {
                    @Override
                    public void onResponse(Call<ApiMessage> call, Response<ApiMessage> response) {
                        if (response.isSuccessful()) {
                            showToast(response.body() != null ? response.body().getMessage() : "添加成功");
                            et_student_id.setText("");
                            loadApplicants();
                        } else {
                            showToast("添加失败：" + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiMessage> call, Throwable t) {
                        showToast("网络错误：" + t.getMessage());
                    }
                });
    }
}