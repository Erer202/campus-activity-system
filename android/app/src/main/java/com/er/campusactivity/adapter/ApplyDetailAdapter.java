package com.er.campusactivity.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.er.campusactivity.R;
import com.er.campusactivity.entry.ApiMessage;
import com.er.campusactivity.entry.ApplicantInfo;
import com.er.campusactivity.entry.MyActivity;
import com.er.campusactivity.entry.User;
import com.er.campusactivity.network.ApiClient;
import com.er.campusactivity.utils.AppApplication;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApplyDetailAdapter extends RecyclerView.Adapter<ApplyDetailAdapter.ViewHolder> {

    private Context context;
    private List<ApplicantInfo> userList;
    private MyActivity activity;
    private OnApplicantChangedListener listener;

    public ApplyDetailAdapter(Context context, List<ApplicantInfo> userList, MyActivity activity){
        this.context = context;
        this.userList = userList;
        this.activity = activity;
    }

    public void setOnApplicantChangedListener(OnApplicantChangedListener listener) {
        this.listener = listener;
    }


    public interface OnApplicantChangedListener {
        void onApplicantChanged(int newCount);
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

        holder.btn_cancel_apply.setOnClickListener(v -> {
            User currentUser = AppApplication.getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
                return;
            }

            if (activity == null) {
                Toast.makeText(context, "活动信息异常", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiClient.getApiService()
                    .adminCancelApplicant(currentUser.getUserId(), user.getUserId(), activity.getId())
                    .enqueue(new Callback<ApiMessage>() {
                        @Override
                        public void onResponse(Call<ApiMessage> call, Response<ApiMessage> response) {
                            if (response.isSuccessful()) {
                                int adapterPosition = holder.getAdapterPosition();
                                if (adapterPosition != RecyclerView.NO_POSITION) {
                                    userList.remove(adapterPosition);
                                    notifyItemRemoved(adapterPosition);
                                    notifyItemRangeChanged(adapterPosition, userList.size());
                                }

                                Toast.makeText(context,
                                        response.body() != null ? response.body().getMessage() : "取消报名成功",
                                        Toast.LENGTH_SHORT).show();

                                if (listener != null) {
                                    listener.onApplicantChanged(userList.size());
                                }
                            } else {
                                Toast.makeText(context, "取消报名失败：" + response.code(), Toast.LENGTH_SHORT).show();
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
        return null != userList ? userList.size() : 0;
    }

    public void setNewData(List<ApplicantInfo> newList) {
        this.userList = newList;
        notifyDataSetChanged();  // 刷新列表
    }



    // ViewHolder：缓存视图
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_student_id, tv_student_name;
        Button btn_cancel_apply;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_student_id = itemView.findViewById(R.id.tv_student_id);
            tv_student_name = itemView.findViewById(R.id.tv_student_name);
            btn_cancel_apply = itemView.findViewById(R.id.btn_cancel_apply);
        }
    }
}
