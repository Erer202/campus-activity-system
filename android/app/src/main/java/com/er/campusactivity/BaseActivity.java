package com.er.campusactivity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.er.campusactivity.db.ActivityDao;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
        initData();

    }

    //  抽象方法等待继承重写
    protected abstract int getLayoutId();   // 获取Layout Id
    protected abstract void initView();     // 初始化视图
    protected abstract void initData();     // 初始化数据

    // 快速吐司
    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
