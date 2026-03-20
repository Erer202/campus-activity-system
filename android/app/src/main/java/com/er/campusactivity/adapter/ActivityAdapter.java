package com.er.campusactivity.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.er.campusactivity.R;
import com.er.campusactivity.entry.MyActivity;
import com.er.campusactivity.ui.ApplyActivity;

import java.io.Serializable;
import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private List<MyActivity> activityList;

    public ActivityAdapter(List<MyActivity> activityList){
        this.activityList = activityList;
    }

    @NonNull
    @Override
    public ActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityAdapter.ViewHolder holder, int position) {
        MyActivity activity = activityList.get(position);

        // 设置每个item的内容
        holder.tv_name.setText(activity.getName());
        holder.tv_intro.setText(activity.getIntro());
        holder.tv_dept.setText(activity.getDept());
        holder.tv_activityTime.setText(activity.getActivityTime());
        holder.tv_location.setText(activity.getLocation());
        holder.tv_applyTime.setText(activity.getApplyTime());

        // 设置详情页面的点击监听
        holder.tv_activityMessage.setOnClickListener(v -> {
            // 跳详情页
             Intent intent = new Intent(holder.itemView.getContext(), ApplyActivity.class);
             intent.putExtra("activity",activity);
             holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return null != activityList ? activityList.size() : 0;
    }

    // 更新数据
    public void setNewData(List<MyActivity> newList) {
        this.activityList = newList;
        notifyDataSetChanged(); // 通知列表刷新
    }


    // ViewHolder：缓存视图
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_intro, tv_dept, tv_activityTime, tv_location,tv_applyTime, tv_activityMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_intro = itemView.findViewById(R.id.tv_intro);
            tv_dept = itemView.findViewById(R.id.tv_dept);
            tv_activityTime = itemView.findViewById(R.id.tv_activity_time);
            tv_location = itemView.findViewById(R.id.tv_location);
            tv_applyTime = itemView.findViewById(R.id.tv_apply_time);
            tv_activityMessage = itemView.findViewById(R.id.tv_activity_message);
        }
    }

}
