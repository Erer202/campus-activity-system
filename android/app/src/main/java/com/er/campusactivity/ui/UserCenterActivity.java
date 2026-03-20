package com.er.campusactivity.ui;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.er.campusactivity.BaseActivity;
import com.er.campusactivity.R;
import com.er.campusactivity.api.ApiService;
import com.er.campusactivity.entry.User;
import com.er.campusactivity.network.ApiClient;
import com.er.campusactivity.utils.AppApplication;
import com.er.campusactivity.utils.SPUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserCenterActivity extends BaseActivity {
    private String userName;
    private String userId;
    private int isAdmin;
    private TextView tv_userId;
    private TextView tv_userName;
    private TextView tv_userLimit;
    private LinearLayout ll_myPublish;
    private LinearLayout ll_myJoin;
    private LinearLayout ll_touchUs;
    private LinearLayout ll_loginOut;
    private Button btn_publish;
    private String userGarde;
    private String userSchool;
    private TextView tv_user_grade_school;
    private static final int REQUEST_PUBLISH = 1001;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_center;
    }

    @Override
    protected void initView() {
        tv_userId = findViewById(R.id.tv_user_id);
        tv_userName = findViewById(R.id.tv_user_name);
        tv_userLimit = findViewById(R.id.tv_user_limit);
        tv_user_grade_school = findViewById(R.id.tv_user_grade_school);
        btn_publish = findViewById(R.id.btn_publish_activity);
        ll_myJoin = findViewById(R.id.ll_my_join);
        ll_myPublish = findViewById(R.id.ll_my_publish);
        ll_touchUs = findViewById(R.id.ll_touch_us);
        ll_loginOut = findViewById(R.id.ll_login_out);

        ll_myJoin.setOnClickListener(v -> startActivity(new Intent(this, MyJoinActivity.class)));
        ll_touchUs.setOnClickListener(v -> startActivity(new Intent(this, TouchUsActivity.class)));
        ll_myPublish.setOnClickListener(v -> startActivityForResult(new Intent(this, MyPublishActivity.class), REQUEST_PUBLISH));

        ll_loginOut.setOnClickListener(v -> {
            SPUtils.clearLoginInfo(this);
            AppApplication.setCurrentUser(null);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btn_publish.setOnClickListener(v -> startActivityForResult(new Intent(this, PublishActivity.class), REQUEST_PUBLISH));

        ImageButton btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> finish());
    }

    @Override
    protected void initData() {
        if (AppApplication.getCurrentUser() == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        String userId = AppApplication.getCurrentUser().getUserId();
        ApiClient.getApiService().getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    AppApplication.setCurrentUser(user);

                    tv_userId.setText(user.getUserId());
                    tv_userName.setText(user.getUserName());
                    tv_user_grade_school.setText(user.getUserGarde() + " " + user.getUserSchool());

                    if (user.getIsAdmin() == 1) {
                        tv_userLimit.setText("管");
                        btn_publish.setVisibility(View.VISIBLE);
                        ll_myPublish.setVisibility(View.VISIBLE);
                    } else {
                        tv_userLimit.setText("普");
                        btn_publish.setVisibility(View.GONE);
                        ll_myPublish.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(UserCenterActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UserCenterActivity.this, "网络请求失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserInfoFromServer(String userId) {
        ApiService service = ApiClient.getApiService();
        service.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    AppApplication.setCurrentUser(user);

                    userName = user.getUserName();
                    UserCenterActivity.this.userId = user.getUserId();
                    isAdmin = user.getIsAdmin();
                    userGarde = user.getUserGarde();
                    userSchool = user.getUserSchool();

                    updateUserInfoUI();
                } else {
                    Toast.makeText(UserCenterActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UserCenterActivity.this, "网络请求失败：" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUserInfoUI() {
        tv_userId.setText(userId == null ? "" : userId);
        tv_userName.setText(userName == null ? "" : userName);

        String gradeSchool = (userGarde == null ? "" : userGarde) + " " +
                (userSchool == null ? "" : userSchool);
        tv_user_grade_school.setText(gradeSchool.trim());

        if (isAdmin == 1) {
            tv_userLimit.setText("管");
            tv_userLimit.setBackgroundColor(getResources().getColor(R.color.red, getTheme()));
            tv_userLimit.setTextColor(getResources().getColor(R.color.white, getTheme()));
            btn_publish.setVisibility(View.VISIBLE);
            ll_myPublish.setVisibility(View.VISIBLE);
        } else {
            tv_userLimit.setText("普");
            tv_userLimit.setBackgroundColor(getResources().getColor(R.color.lightGrey, getTheme()));
            tv_userLimit.setTextColor(getResources().getColor(R.color.black, getTheme()));
            btn_publish.setVisibility(View.GONE);
            ll_myPublish.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppApplication.getCurrentUser() != null) {
            loadUserInfoFromServer(AppApplication.getCurrentUser().getUserId());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PUBLISH && resultCode == RESULT_OK) {
            showToast("已刷新列表");
            setResult(RESULT_OK);
            finish();
        }
    }
}
