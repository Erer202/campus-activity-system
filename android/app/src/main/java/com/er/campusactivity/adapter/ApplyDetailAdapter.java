package com.er.campusactivity.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.er.campusactivity.R;
import com.er.campusactivity.entry.ApplicantInfo;
import com.er.campusactivity.entry.MyActivity;
import com.er.campusactivity.entry.User;
import com.er.campusactivity.utils.AppApplication;

import java.util.List;

public class ApplyDetailAdapter extends RecyclerView.Adapter<ApplyDetailAdapter.ViewHolder> {

    private Context context;
    private List<ApplicantInfo> userList;
    private MyActivity activity;
    public ApplyDetailAdapter(Context context, List<ApplicantInfo> userList, MyActivity activity){
        this.context = context;
        this.userList = userList;
    }


    @NonNull
    @Override
    public ApplyDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_apply_detail, parent, false);
        return new ApplyDetailAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplyDetailAdapter.ViewHolder holder, int position) {
        ApplicantInfo user = userList.get(position);
        // 绑定数据
        holder.tv_student_id.setText("学号：" + user.getUserId());
        holder.tv_student_name.setText("姓名：" + user.getUserName());
    }

    @Override
    public int getItemCount() {
        return null != userList ? userList.size() : 0;
    }

    public void setNewData(List<ApplicantInfo> newList) {
        this.userList = newList;
        notifyDataSetChanged();  // 刷新列表
    }



    // ViewHolder：缓存视图
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_student_id, tv_student_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_student_id = itemView.findViewById(R.id.tv_student_id);
            tv_student_name = itemView.findViewById(R.id.tv_student_name);

        }
    }
}
