package com.er.campusactivity.api;

import com.er.campusactivity.entry.ApiMessage;
import com.er.campusactivity.entry.ApplicantInfo;
import com.er.campusactivity.entry.ApplyRequest;
import com.er.campusactivity.entry.MyActivity;
import com.er.campusactivity.entry.User;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {


    @GET("activity/all")
    Call<List<MyActivity>> getAllActivities();

    @POST("api/user/login")
    Call<User> login(@Body User user);

    @POST("api/user/register")
    Call<User> register(@Body User user);

    // 获取用户
    @GET("api/user/{userId}")
    Call<User> getUserById(@Path("userId") String userId);

    // 申请
    @POST("api/apply")
    Call<Object> apply(@Body ApplyRequest request);

    // 检查是否报名
    @GET("api/apply/check")
    Call<Boolean> checkApplied(@Query("studentId") String studentId,
                               @Query("activityId") Integer activityId);

    // 我的报名
    @GET("api/apply/my/{studentId}")
    Call<List<MyActivity>> getMyJoinActivities(@Path("studentId") String studentId);

    // 取消报名
    @DELETE("api/apply")
    Call<ApiMessage> cancelApply(@Query("studentId") String studentId,
                                 @Query("activityId") Integer activityId);


    // 发布活动
    @POST("activity/publish")
    Call<MyActivity> publishActivity(@Body MyActivity activity);

    // 查找发布的活动
    @GET("activity/my/{publisherId}")
    Call<List<MyActivity>> getMyPublishedActivities(@Path("publisherId") String publisherId);

    // 删除发布的活动
    @DELETE("activity/{id}")
    Call<ApiMessage> deleteActivity(@Path("id") Integer id,
                                    @Query("publisherId") String publisherId);


    // 获得某活动报名信息
    @GET("api/apply/activity/{activityId}/applicants")
    Call<List<ApplicantInfo>> getApplicantsForActivity(@Path("activityId") Integer activityId,
                                                       @Query("publisherId") String publisherId);

    // 管理员手动添加报名者
    @POST("api/apply/admin-add")
    Call<ApiMessage> adminAddApplicant(@Query("publisherId") String publisherId,
                                       @Query("studentId") String studentId,
                                       @Query("activityId") Integer activityId);


    @GET("activity/search")
    Call<List<MyActivity>> searchActivities(@Query("keyword") String keyword);
}












