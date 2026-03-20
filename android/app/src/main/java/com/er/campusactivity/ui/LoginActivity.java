package com.er.campusactivity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.er.campusactivity.api.ApiService;
import com.er.campusactivity.network.ApiClient;
import com.er.campusactivity.utils.AppApplication;
import com.er.campusactivity.utils.SPUtils;
import com.er.campusactivity.BaseActivity;
import com.er.campusactivity.R;
import com.er.campusactivity.db.LoginDao;
import com.er.campusactivity.entry.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_register;
    private EditText et_userId;
    private EditText et_userPassword;
    private Button btn_login;

    private LoginDao loginDao;
    private CheckBox cb_remember_pwd;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        // 初始化控件
        tv_register = findViewById(R.id.tv_register);
        et_userId = findViewById(R.id.et_userId);
        et_userPassword = findViewById(R.id.et_userPassword);
        btn_login = findViewById(R.id.btn_login);
        cb_remember_pwd = findViewById(R.id.cb_remember_pwd);
        // 注册登录逻辑
        loginDao = new LoginDao(this);

        // 注册点击监听
        btn_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
        initData();
        fillLoginInfo();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_login){
            // 点击登录按钮，进行账号密码判断
            String userId = et_userId.getText().toString();
            String userPassword = et_userPassword.getText().toString();
            // 提前判断密码是否合规
            if(userPassword.length() < 6){
                showToast("密码长度不能小于6");
                return;
            }
            ApiService service = ApiClient.getApiService();
            User loginUser = new User();
            loginUser.setUserId(userId);
            loginUser.setUserPassword(userPassword);

            service.login(loginUser).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();
                        AppApplication.setCurrentUser(user);

                        // 保存记住密码
                        boolean isRemember = cb_remember_pwd.isChecked();
                        SPUtils.saveLoginInfo(LoginActivity.this, userId, userPassword, isRemember);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        showToast("登录成功");
                        finish();
                    } else {
                        showToast("学号或密码错误");
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    showToast("网络请求失败：" + t.getMessage());
                }
            });
        }else if(id == R.id.tv_register){
            // 点击注册按钮，进行注册页面跳转
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    // 自动填充上次保存的账号密码
    private void fillLoginInfo() {
        // 判断是否勾选“记住密码”
        boolean isRemember = SPUtils.isRememberPwd(this);
        if (isRemember) {
            // 读取存储的账号密码并填充
            String username = SPUtils.getUsername(this);
            String password = SPUtils.getPassword(this);
            et_userId.setText(username);
            et_userPassword.setText(password);
            // 复选框自动勾选
            cb_remember_pwd.setChecked(true);
        }
    }
}
