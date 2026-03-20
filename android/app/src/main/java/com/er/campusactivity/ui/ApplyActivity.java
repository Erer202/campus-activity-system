package com.er.campusactivity.ui;


import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.er.campusactivity.api.ApiService;
import com.er.campusactivity.entry.ApplyRequest;
import com.er.campusactivity.network.ApiClient;
import com.er.campusactivity.utils.AppApplication;
import com.er.campusactivity.BaseActivity;
import com.er.campusactivity.R;
import com.er.campusactivity.db.ApplyDao;
import com.er.campusactivity.entry.MyActivity;
import com.er.campusactivity.entry.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApplyActivity extends BaseActivity implements View.OnClickListener {
    private ApplyDao applyDao;
    private TextView tv_apply_name;
    private TextView tv_apply_dept;
    private TextView tv_apply_intro;
    private TextView tv_apply_location;
    private TextView tv_apply_time;
    private EditText et_user_name;
    private EditText et_user_id;
    private EditText et_user_phone;
    private EditText et_user_school;
    private Spinner spinner_user_grade;
    private Button btn_apply_confirm;
    private MyActivity activity;
    private TextView tv_activity_id;
    private TextView tv_apply_apply_time;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_apply;
    }

    @Override
    protected void initView() {
        // 初始化视图
        tv_apply_name = findViewById(R.id.tv_apply_activity_name);
        tv_apply_dept = findViewById(R.id.tv_apply_activity_dept);
        tv_apply_intro = findViewById(R.id.tv_apply_activity_intro);
        tv_apply_location = findViewById(R.id.tv_apply_activity_location);
        tv_apply_time = findViewById(R.id.tv_apply_activity_time);
        tv_apply_apply_time = findViewById(R.id.tv_apply_apply_time);
        tv_activity_id = findViewById(R.id.tv_activity_id);
        et_user_name = findViewById(R.id.et_apply_user_name);
        et_user_id = findViewById(R.id.et_apply_user_id);
        et_user_phone = findViewById(R.id.et_apply_user_phone);
        et_user_school = findViewById(R.id.et_apply_user_school);
        spinner_user_grade = findViewById(R.id.spinner_apply_grade);
        btn_apply_confirm = findViewById(R.id.btn_apply_confirm);


        // 初始化Dao
        applyDao = new ApplyDao(this);
        // 设置点击监听
        btn_apply_confirm.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        // 获取从列表传递的Activity对象
        Intent intent = getIntent();
        if (intent != null) {
            activity = (MyActivity) intent.getSerializableExtra("activity");
            if (activity != null) {
                tv_apply_name.setText(activity.getName());
                tv_apply_dept.setText(activity.getDept());
                tv_apply_intro.setText(activity.getIntro());
                tv_apply_location.setText(activity.getLocation());
                tv_apply_time.setText(activity.getActivityTime());
                tv_apply_apply_time.setText(activity.getApplyTime());
                tv_activity_id.setText(String.format("%d", activity.getId()));
            }
        }

        // 获取当前登录用户信息，直接填充报名信息
        User currentUser = AppApplication.getCurrentUser();
        if (currentUser != null) {
            // 填充输入框
            et_user_name.setText(currentUser.getUserName());
            et_user_id.setText(currentUser.getUserId());
            et_user_phone.setText(currentUser.getUserPhone());
            et_user_school.setText(currentUser.getUserSchool());


        }
            // 刷新报名状态
            refreshSignupButton();

    }

    private void refreshSignupButton() {

        User currentUser = AppApplication.getCurrentUser();
        if (currentUser == null || activity == null) return;

        boolean isAlreadyApplied = applyDao.isApplied(currentUser.getUserId(), activity.getId());

        ApiService service = ApiClient.getApiService();
        service.checkApplied(currentUser.getUserId(), activity.getId()).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean isApplied = response.body();

                    if (activity.getApplyStatus() == 2) {
                        btn_apply_confirm.setText("报名已结束");
                        btn_apply_confirm.setEnabled(false);
                    } else if (isApplied) {
                        btn_apply_confirm.setText("已报名");
                        btn_apply_confirm.setEnabled(false);
                    } else {
                        btn_apply_confirm.setText("立即报名");
                        btn_apply_confirm.setEnabled(true);
                    }
                } else {
                    showToast("获取报名状态失败");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                showToast("网络错误：" + t.getMessage());
            }
        });
    }

    // 锁定文本输入框
    private void setEditTextUneditable(EditText editText) {
        editText.setFocusable(false); // 不可获取焦点（无光标）
        editText.setFocusableInTouchMode(false); // 触摸时也不可获取焦点
        editText.setClickable(false); // 不可点击
        editText.setCursorVisible(false); // 隐藏光标
    }
    // 打开文本输入框
    private void setEditTextable(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setClickable(true);
        editText.setCursorVisible(true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_apply_confirm) {
            User currentUser = AppApplication.getCurrentUser();
            if (currentUser == null || activity == null) {
                showToast("请先登录");
                return;
            }

            ApplyRequest request = new ApplyRequest(currentUser.getUserId(), activity.getId());

            ApiClient.getApiService().apply(request).enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        showToast("报名成功");
                        refreshSignupButton();
                    } else {
                        showToast("报名失败：" + response.message());
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    showToast("网络请求失败：" + t.getMessage());
                }
            });
        }
    }
}
