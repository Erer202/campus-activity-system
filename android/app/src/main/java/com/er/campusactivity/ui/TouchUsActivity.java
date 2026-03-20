package com.er.campusactivity.ui;

import android.widget.Button;
import android.widget.ImageButton;

import com.er.campusactivity.BaseActivity;
import com.er.campusactivity.R;

public class TouchUsActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_touch_us;
    }

    @Override
    protected void initView() {
        // 返回按钮
        ImageButton btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> finish());
    }

    @Override
    protected void initData() {

    }
}
