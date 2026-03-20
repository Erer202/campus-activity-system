package com.er.campusactivity.ui;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.er.campusactivity.BaseActivity;
import com.er.campusactivity.R;
import com.er.campusactivity.db.ActivityDao;
import com.er.campusactivity.entry.MyActivity;
import com.er.campusactivity.entry.User;
import com.er.campusactivity.network.ApiClient;
import com.er.campusactivity.utils.AppApplication;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublishActivity extends BaseActivity implements View.OnClickListener {

    // 控件
    private TextView tv_activity_timeStart, tv_activity_timeEnd, tv_apply_deadline;
    private EditText et_activity_name, et_activity_location, et_activity_dept, et_activity_intro;
    private Button btn_submit_activity;
    private ImageButton btn_back;
    private ActivityDao activityDao;
    private AppApplication appApplication;
    private User currentUser;

    // 日历工具 + 格式化器
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.CHINA);
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.CHINA);

    @Override
    protected int getLayoutId() {
        return R.layout.activity_publish_activity;
    }

    @Override
    protected void initView() {

        // 返回按钮
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> {finish();});

        // 输入项
        et_activity_name = findViewById(R.id.et_activity_name);
        et_activity_location = findViewById(R.id.et_activity_location);
        et_activity_dept = findViewById(R.id.et_activity_dept);
        et_activity_intro = findViewById(R.id.et_activity_intro);

        // 时间选择项
        tv_activity_timeStart = findViewById(R.id.tv_activity_time_start);
        tv_activity_timeEnd = findViewById(R.id.tv_activity_time_end);
        tv_apply_deadline = findViewById(R.id.tv_apply_deadline);
        tv_activity_timeStart.setOnClickListener(this);
        tv_activity_timeEnd.setOnClickListener(this);
        tv_apply_deadline.setOnClickListener(this);

        // 提交按钮
        btn_submit_activity = findViewById(R.id.btn_submit_activity);
        btn_submit_activity.setOnClickListener(this);


    }

    @Override
    protected void initData() {
        currentUser = AppApplication.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_activity_time_start) {
            showDatePicker(tv_activity_timeStart); // 仅选日期
        } else if (id == R.id.tv_activity_time_end) {
            showDatePicker(tv_activity_timeEnd); // 仅选日期
        } else if (id == R.id.tv_apply_deadline) {
            showDateTimePicker(tv_apply_deadline); // 选日期+时间
        } else if (id == R.id.btn_submit_activity) {
            submitActivity(); // 提交发布
        }
    }

    // 纯日期选择器
    private void showDatePicker(final TextView target) {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            target.setText(dateFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showDateTimePicker(final TextView target) {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);

            new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                target.setText(dateTimeFormat.format(calendar.getTime()));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }


    private void submitActivity() {
        // 关键防护：重新获取最新用户信息
        currentUser = AppApplication.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "登录状态异常，请重新登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String name = et_activity_name.getText().toString().trim();
        String location = et_activity_location.getText().toString().trim();
        String dept = et_activity_dept.getText().toString().trim();
        String intro = et_activity_intro.getText().toString().trim();

        String activityTimeStart = tv_activity_timeStart.getText().toString().trim();
        String activityTimeEnd = tv_activity_timeEnd.getText().toString().trim();
        String applyDeadline = tv_apply_deadline.getText().toString().trim();

        // 校验必填项
        if (name.isEmpty()) {
            et_activity_name.setError("请输入活动名称");
            return;
        }
        if (location.isEmpty()) {
            et_activity_location.setError("请输入活动地点");
            return;
        }
        if (dept.isEmpty()) {
            et_activity_dept.setError("请输入组织部门");
            return;
        }
        if (activityTimeStart.isEmpty() || activityTimeEnd.isEmpty()) {
            Toast.makeText(this, "请选择活动时间", Toast.LENGTH_SHORT).show();
            return;
        }
        if (applyDeadline.isEmpty()) {
            Toast.makeText(this, "请选择报名截止时间", Toast.LENGTH_SHORT).show();
            return;
        }

        // 构造 MyActivity 对象
        MyActivity activity = new MyActivity();
        activity.setName(name);
        activity.setLocation(location);
        activity.setDept(dept);
        activity.setIntro(intro.isEmpty() ? "暂无介绍" : intro);
        activity.setActivityTime(activityTimeStart + " - " + activityTimeEnd);
        activity.setApplyTime(applyDeadline);
        activity.setRequirement("无特殊要求");
        activity.setPublisherId(currentUser.getUserId()); // 发布者是当前登录用户
        activity.setApplyStatus(1);                       // 1 = 报名进行中
        activity.setActivityStatus(0);                    // 0 = 活动未开始

        ApiClient.getApiService().publishActivity(activity).enqueue(new Callback<MyActivity>() {
            @Override
            public void onResponse(Call<MyActivity> call, Response<MyActivity> response) {
                btn_submit_activity.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(PublishActivity.this, "活动发布成功！", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(PublishActivity.this, "发布失败：" + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MyActivity> call, Throwable t) {
                btn_submit_activity.setEnabled(true);
                Toast.makeText(PublishActivity.this, "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次回到页面都重新获取用户信息，防止被系统回收后为空
        currentUser = AppApplication.getCurrentUser();
    }
}
