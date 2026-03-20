package com.er.campusactivity.ui;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.er.campusactivity.BaseActivity;
import com.er.campusactivity.R;
import com.er.campusactivity.api.ApiService;
import com.er.campusactivity.db.RegisterDao;
import com.er.campusactivity.entry.User;
import com.er.campusactivity.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private EditText et_userId;
    private Spinner spinner_grade;
    private EditText et_userSchool;
    private EditText et_userPassword;
    private EditText et_userName;
    private EditText et_userPhone;

    private RegisterDao registerDao;
    private Button btn_register;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView() {
        // 初始化控件
        et_userId = findViewById(R.id.et_userId);
        spinner_grade = findViewById(R.id.spinner_grade);
        et_userSchool = findViewById(R.id.et_userSchool);
        et_userPassword = findViewById(R.id.et_userPassword);
        et_userName = findViewById(R.id.et_userName);
        et_userPhone = findViewById(R.id.et_userPhone);
        btn_register = findViewById(R.id.btn_register);

        registerDao = new RegisterDao(this);

        btn_register.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_register){
            String userId = et_userId.getText().toString();
            String userName = et_userName.getText().toString();
            String userPhone = et_userPhone.getText().toString();
            String userGarde = spinner_grade.getSelectedItem().toString();
            String userPassword = et_userPassword.getText().toString();
            String userSchool = et_userSchool.getText().toString();
            User user = new User(userId, userPassword, userSchool, userGarde, userName, userPhone, User.Role.USER.getValue());

            ApiService service = ApiClient.getApiService();

            service.register(user).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                        finish();                    // ← 成功后立即返回上一个页面
                    } else {
                        Toast.makeText(RegisterActivity.this, "注册失败：" + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "网络请求失败：" + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
