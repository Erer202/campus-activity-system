package com.er.campusactivity.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.er.campusactivity.BaseActivity;
import com.er.campusactivity.R;
import com.er.campusactivity.adapter.ActivityAdapter;
import com.er.campusactivity.api.ApiService;
import com.er.campusactivity.db.ActivityDao;
import com.er.campusactivity.entry.MyActivity;
import com.er.campusactivity.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private PopupWindow filterPopupWindow; // 筛选弹窗
    private RecyclerView recyclerView;
    private ImageButton btn_filter;
    private List<MyActivity> activityList = new ArrayList<>();
    private ActivityAdapter activityAdapter;
    private ImageButton btn_user;
    private static final int REQUEST_USER_CENTER = 1002;

    private ImageButton btn_search;
    private Button btn_to_search;
    private LinearLayout ll_search;
    private EditText et_search;
    private ImageButton btn_exit;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        // 初始化控件
        btn_filter = findViewById(R.id.btn_filter);
        btn_user = findViewById(R.id.btn_user);
        btn_search = findViewById(R.id.btn_search);
        btn_to_search = findViewById(R.id.btn_to_search);
        btn_exit = findViewById(R.id.btn_exit);
        ll_search = findViewById(R.id.ll_search);
        // 初始化列表视图
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));  // 设置为线性视图

        // 初始化筛选弹窗
        initFilterPopupWindow();


        // 注册点击事件
        btn_filter.setOnClickListener(this);
        btn_user.setOnClickListener(this);
        // 点击搜索栏按钮
        btn_search.setOnClickListener(v -> {
            if(ll_search.getVisibility() == View.GONE) {
                ll_search.setVisibility(View.VISIBLE);
            } else if (ll_search.getVisibility() == View.VISIBLE) {
                ll_search.setVisibility(View.GONE);
            }
        });
        
        // 对搜索栏进行处理
        et_search = findViewById(R.id.et_search);
        btn_to_search.setOnClickListener(v -> {
            String searchContent = et_search.getText().toString().trim();
            if (searchContent.isEmpty()) {
                showToast("请输入搜索内容");
                return;
            }
            searchActivityData(searchContent);
        });

        btn_exit.setOnClickListener(v -> {
            ll_search.setVisibility(View.GONE);
            et_search.setText("");
            loadActivityData();
        });

        btn_exit.setOnClickListener(v -> {
            // 关闭搜索框
            ll_search.setVisibility(View.GONE);
            et_search.setText(""); // 清空输入框（建议加上）

            // 重新从后端加载全部活动
            loadActivityData();
        });

    }


    private void searchActivityData(String keyword) {
        ApiService apiService = ApiClient.getApiService();
        apiService.searchActivities(keyword).enqueue(new Callback<List<MyActivity>>() {
            @Override
            public void onResponse(Call<List<MyActivity>> call, Response<List<MyActivity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MyActivity> searchList = response.body();
                    activityAdapter.setNewData(searchList);

                    if (searchList.isEmpty()) {
                        showToast("未搜索到活动");
                    }
                } else {
                    showToast("搜索失败");
                }
            }

            @Override
            public void onFailure(Call<List<MyActivity>> call, Throwable t) {
                showToast("网络请求失败：" + t.getMessage());
            }
        });
    }

    private void initFilterPopupWindow() {
        //  加载筛选面板布局
        View filterView = LayoutInflater.from(this).inflate(R.layout.filter_panel, null);

        //  创建PopupWindow（设置宽高、可点击外部关闭）
        filterPopupWindow = new PopupWindow(
                filterView, // 弹窗布局
                ViewGroup.LayoutParams.WRAP_CONTENT, // 宽度自适应
                ViewGroup.LayoutParams.WRAP_CONTENT, // 高度自适应
                true // 是否可点击（必须设true，否则外部点击不关闭）
        );

        //  弹窗属性设置
        // 设置背景（否则外部点击关闭无效）
        filterPopupWindow.setBackgroundDrawable(new ColorDrawable());
        // 允许点击外部区域关闭弹窗
        filterPopupWindow.setOutsideTouchable(true);
        // 允许弹窗获取焦点（否则RadioButton无法选中）
        filterPopupWindow.setFocusable(true);


        //  获取弹窗内的控件（关键：从filterView里找，不是主布局）
        RadioGroup rg_apply_status = filterView.findViewById(R.id.rg_apply_status);
        Button btn_filter_confirm = filterView.findViewById(R.id.btn_filter_confirm);

        // 给弹窗内的“确认筛选”按钮设置点击监听（核心筛选逻辑）
        btn_filter_confirm.setOnClickListener(v -> {
            // 获取选中的RadioButton
            int checkedId = rg_apply_status.getCheckedRadioButtonId();
            int filterStatus = -1;
            if (checkedId == R.id.cb_filter_apply_ing) {
                filterStatus = 1; // 报名进行中
            } else if (checkedId == R.id.cb_filter_activity_ing) {
                filterStatus = 2; // 活动进行中
            } else if (checkedId == R.id.cb_filter_activity_end) {
                filterStatus = 3; // 已结束
            }

            // 执行筛选逻辑
            filterActivityList(filterStatus);

            // 关闭弹窗
            filterPopupWindow.dismiss();
        });

    }

    private void filterActivityList(int filterStatus) {
        //  空判断
        if (activityList == null || activityList.isEmpty()) {
            showToast("暂无活动数据");
            return;
        }

        //  创建筛选后的列表
        List<MyActivity> filterList = new ArrayList<>();

        // 根据筛选状态过滤
        for (MyActivity activity : activityList) {
            if (filterStatus == -1) {
                // 无筛选：显示所有
                filterList.add(activity);
            } else {
                // 有筛选，去匹配状态
                if (filterStatus == 1 && activity.getApplyStatus() == 1) {
                    // 报名进行中
                    filterList.add(activity);

                }
            }
        }
        //  更新适配器数据
        activityAdapter.setNewData(filterList);
        showToast("筛选出" + filterList.size() + "个活动");
    }

    @Override
    protected void initData() {
        activityAdapter = new ActivityAdapter(activityList);
        recyclerView.setAdapter(activityAdapter);

        loadActivityData();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == btn_filter.getId()){
            // 点击筛选
             if (filterPopupWindow.isShowing()) {
                // 弹窗已显示 → 关闭
                filterPopupWindow.dismiss();
            } else {
                // 弹窗未显示 → 在筛选按钮下方弹出（右对齐）
                filterPopupWindow.showAsDropDown(btn_filter, 0, 0, Gravity.RIGHT);
            }
        } else if (id == btn_user.getId()) {
            // 跳转个人中心
            Intent intent = new Intent(this, UserCenterActivity.class);
            startActivityForResult(intent, REQUEST_USER_CENTER);
        }
    }


    private void loadActivityData() {
        ApiService apiService = ApiClient.getApiService();
        // 接口返回值改 List<MyActivity>
        apiService.getAllActivities().enqueue(new Callback<List<MyActivity>>() {
            @Override
            public void onResponse(Call<List<MyActivity>> call, Response<List<MyActivity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 改这里：MyActivity
                    activityList = response.body();
                    activityAdapter.setNewData(activityList);
                } else {
                    Toast.makeText(MainActivity.this, "接口返回空数据", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MyActivity>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(MainActivity.this, "网络请求失败：" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // 只要有 RESULT_OK 就刷新列表
            refreshActivityList();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadActivityData();
    }

    private void refreshActivityList() {
        loadActivityData();
    }
}