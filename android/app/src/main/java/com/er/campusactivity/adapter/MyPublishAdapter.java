package com.er.campusactivity.adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.er.campusactivity.R;
import com.er.campusactivity.db.ActivityDao;
import com.er.campusactivity.db.ApplyDao;
import com.er.campusactivity.entry.ApiMessage;
import com.er.campusactivity.entry.MyActivity;
import com.er.campusactivity.entry.User;
import com.er.campusactivity.network.ApiClient;
import com.er.campusactivity.ui.ApplyDetailActivity;
import com.er.campusactivity.ui.MyPublishActivity;
import com.er.campusactivity.utils.AppApplication;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPublishAdapter extends RecyclerView.Adapter<MyPublishAdapter.ViewHolder> {

    private List<MyActivity> activityList;
    private ApplyDao applyDao;
    private ActivityDao activityDao;
    private Context context;
    private static final int RESULT_OK = -1;  // 系统常量
    public MyPublishAdapter(List<MyActivity> activityList, Context context){
        this.activityList = activityList;
        this.context = context;

    }

    @NonNull
    @Override
    public MyPublishAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_publish, parent, false);
        return new MyPublishAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPublishAdapter.ViewHolder holder, int position) {
        User currentUser = AppApplication.getCurrentUser();
        MyActivity activity = activityList.get(position);

        holder.tv_name.setText(activity.getName());
        holder.tv_intro.setText(activity.getIntro() != null ? activity.getIntro() : "暂无介绍");
        holder.tv_dept.setText(activity.getDept());
        holder.tv_activityTime.setText(activity.getActivityTime());
        holder.tv_location.setText(activity.getLocation());
        holder.tv_applyTime.setText(activity.getApplyTime());

        // 删除发布的活动
        holder.btn_delete_activity.setOnClickListener(v -> {
            if (currentUser == null) {
                Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiClient.getApiService().deleteActivity(activity.getId(), currentUser.getUserId())
                    .enqueue(new Callback<ApiMessage>() {
                        @Override
                        public void onResponse(Call<ApiMessage> call, Response<ApiMessage> response) {
                            if (response.isSuccessful()) {
                                int adapterPosition = holder.getAdapterPosition();
                                if (adapterPosition != RecyclerView.NO_POSITION) {
                                    activityList.remove(adapterPosition);
                                    notifyItemRemoved(adapterPosition);
                                    notifyItemRangeChanged(adapterPosition, activityList.size());
                                }

                                Toast.makeText(
                                        context,
                                        response.body() != null ? response.body().getMessage() : "删除活动成功",
                                        Toast.LENGTH_SHORT
                                ).show();

                                if (context instanceof MyPublishActivity) {
                                    ((MyPublishActivity) context).setResult(RESULT_OK);
                                }
                            } else {
                                Toast.makeText(context, "删除失败：" + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiMessage> call, Throwable t) {
                            Toast.makeText(context, "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        // 点击活动详细
        holder.btn_check_apply_detail.setOnClickListener(v -> {
            Intent intent = new Intent(context, ApplyDetailActivity.class);
            intent.putExtra("activity", activity);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return null != activityList ? activityList.size() : 0;
    }

    public void setNewData(List<MyActivity> newList) {
        this.activityList = newList;
        notifyDataSetChanged(); // 通知列表刷新
    }





    // ViewHolder：缓存视图
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_intro, tv_dept, tv_activityTime, tv_location,tv_applyTime;
        Button btn_delete_activity, btn_check_apply_detail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_intro = itemView.findViewById(R.id.tv_intro);
            tv_dept = itemView.findViewById(R.id.tv_dept);
            tv_activityTime = itemView.findViewById(R.id.tv_activity_time);
            tv_location = itemView.findViewById(R.id.tv_location);
            tv_applyTime = itemView.findViewById(R.id.tv_apply_time);
            btn_delete_activity = itemView.findViewById(R.id.btn_delete_activity);
            btn_check_apply_detail = itemView.findViewById(R.id.btn_check_apply_detail);
        }
    }
}
