package com.er.campusactivity.adapter;

import android.content.Context;
import android.widget.Button;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.er.campusactivity.R;
import com.er.campusactivity.entry.ApiMessage;
import com.er.campusactivity.entry.MyActivity;
import com.er.campusactivity.entry.User;
import com.er.campusactivity.network.ApiClient;
import com.er.campusactivity.utils.AppApplication;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyJoinAdapter extends RecyclerView.Adapter<MyJoinAdapter.ViewHolder> {

    private List<MyActivity> activityList;
    private Context context;

    public MyJoinAdapter(Context context, List<MyActivity> activityList) {
        this.context = context;
        this.activityList = activityList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_join, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User currentUser = AppApplication.getCurrentUser();
        MyActivity activity = activityList.get(position);

        holder.tv_name.setText(activity.getName());
        holder.tv_intro.setText(activity.getIntro() != null ? activity.getIntro() : "暂无介绍");
        holder.tv_dept.setText(activity.getDept());
        holder.tv_activityTime.setText(activity.getActivityTime());
        holder.tv_location.setText(activity.getLocation());
        holder.tv_applyTime.setText(activity.getApplyTime());

        holder.btn_my_join_cancel_apply.setOnClickListener(v -> {
            if (currentUser == null) {
                Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiClient.getApiService().cancelApply(currentUser.getUserId(), activity.getId())
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
                                        response.body() != null ? response.body().getMessage() : "取消报名成功",
                                        Toast.LENGTH_SHORT
                                ).show();
                            } else {
                                Toast.makeText(context, "取消报名失败：" + response.code() + " " + response.message(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiMessage> call, Throwable t) {
                            Toast.makeText(context, "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    @Override
    public int getItemCount() {
        return activityList != null ? activityList.size() : 0;
    }

    public void setNewData(List<MyActivity> newList) {
        this.activityList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_intro, tv_dept, tv_activityTime, tv_location, tv_applyTime;
        Button btn_my_join_cancel_apply;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_intro = itemView.findViewById(R.id.tv_intro);
            tv_dept = itemView.findViewById(R.id.tv_dept);
            tv_activityTime = itemView.findViewById(R.id.tv_activity_time);
            tv_location = itemView.findViewById(R.id.tv_location);
            tv_applyTime = itemView.findViewById(R.id.tv_apply_time);
            btn_my_join_cancel_apply = itemView.findViewById(R.id.btn_my_join_cancel_apply);
        }
    }
}
