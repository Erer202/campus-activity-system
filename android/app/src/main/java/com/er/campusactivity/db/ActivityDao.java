package com.er.campusactivity.db;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.er.campusactivity.entry.MyActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActivityDao {

    private DBHelper dbHelper;
    private Context context;
    public ActivityDao(Context context){
        dbHelper = new DBHelper(context);
    }

    // 发布活动（管理员用）
    public boolean publishActivity(MyActivity activity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            // 插入数据到activity表
            String insertSql = "INSERT INTO activity (name, intro, dept, activity_time, apply_time, requirement, location, publisher_id, apply_status, activity_status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            db.execSQL(insertSql, new Object[]{
                    activity.getName(),
                    activity.getIntro(),
                    activity.getDept(),
                    activity.getActivityTime(),
                    activity.getApplyTime(),
                    activity.getRequirement(),
                    activity.getLocation(),
                    activity.getPublisherId(),
                    activity.getApplyStatus(),
                    activity.getActivityStatus()
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }


    // 查询所有活动（活动广场列表）
    public List<MyActivity> getAllActivities() {
        List<MyActivity> activityList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query("activity", null, null, null, null, null, "id DESC"); // 按发布时间倒序
            while (cursor.moveToNext()) {
                MyActivity activity = new MyActivity();
                // 给Activity对象赋值（字段和数据库一一对应）
                activity.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                activity.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                activity.setIntro(cursor.getString(cursor.getColumnIndexOrThrow("intro")));
                activity.setDept(cursor.getString(cursor.getColumnIndexOrThrow("dept")));
                activity.setActivityTime(cursor.getString(cursor.getColumnIndexOrThrow("activity_time")));
                activity.setApplyTime(cursor.getString(cursor.getColumnIndexOrThrow("apply_time")));
                activity.setRequirement(cursor.getString(cursor.getColumnIndexOrThrow("requirement")));
                activity.setLocation(cursor.getString(cursor.getColumnIndexOrThrow("location")));
                activity.setPublisherId(cursor.getString(cursor.getColumnIndexOrThrow("publisher_id")));
                activity.setApplyStatus(cursor.getInt(cursor.getColumnIndexOrThrow("apply_status")));
                activity.setActivityStatus(cursor.getInt(cursor.getColumnIndexOrThrow("activity_status")));
                activityList.add(activity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return activityList;
    }


    // 删除活动
    public boolean deleteActivity(int activityId) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            // 开启事务：保证删除活动和报名记录的原子性
            db.beginTransaction();

            // 先删除该活动的所有报名记录（级联删除）
            int applyDeleteCount = db.delete(
                    "apply",
                    "activity_id = ?",
                    new String[]{String.valueOf(activityId)}
            );
            Log.d(TAG, "删除活动" + activityId + "的报名记录数：" + applyDeleteCount);

            // 2. 再删除活动本身
            int activityDeleteCount = db.delete(
                    "activity",
                    "id = ?",
                    new String[]{String.valueOf(activityId)}
            );

            // 标记事务成功
            db.setTransactionSuccessful();

            boolean isSuccess = activityDeleteCount > 0;
            if (isSuccess) {
                Log.d(TAG, "删除活动" + activityId + "成功");
            } else {
                Log.w(TAG, "删除活动" + activityId + "失败：无对应活动记录");
            }
            return isSuccess;

        } catch (Exception e) {
            Log.e(TAG, "删除活动异常：", e);
            return false;
        } finally {
            if (db != null) {
                // 结束事务（如果失败会回滚）
                db.endTransaction();
                db.close();
            }
        }
    }

    // 校验用户是否有删除活动的权限
    public boolean checkDeletePermission(String userId, int activityId, boolean isAdmin) {
        // 管理员直接有权限
        if (isAdmin) {
            return true;
        }

        // 普通用户
        return false;
    }

    // 获取当前用户发布的活动列表
    public List<MyActivity> getMyPublishedActivities(String publisherId) {
        List<MyActivity> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            String sql = "SELECT * FROM activity WHERE publisher_id = ? ORDER BY id DESC";
            cursor = db.rawQuery(sql, new String[]{publisherId});

            while (cursor.moveToNext()) {
                MyActivity activity = new MyActivity();
                activity.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                activity.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                activity.setIntro(cursor.getString(cursor.getColumnIndexOrThrow("intro")));
                activity.setDept(cursor.getString(cursor.getColumnIndexOrThrow("dept")));
                activity.setActivityTime(cursor.getString(cursor.getColumnIndexOrThrow("activity_time")));
                activity.setApplyTime(cursor.getString(cursor.getColumnIndexOrThrow("apply_time")));
                activity.setRequirement(cursor.getString(cursor.getColumnIndexOrThrow("requirement")));
                activity.setLocation(cursor.getString(cursor.getColumnIndexOrThrow("location")));
                activity.setPublisherId(cursor.getString(cursor.getColumnIndexOrThrow("publisher_id")));
                activity.setApplyStatus(cursor.getInt(cursor.getColumnIndexOrThrow("apply_status")));
                activity.setActivityStatus(cursor.getInt(cursor.getColumnIndexOrThrow("activity_status")));
                list.add(activity);
            }

            Log.d(TAG, "查询到发布者 " + publisherId + " 的活动数量: " + list.size());

        } catch (Exception e) {
            Log.e(TAG, "getMyPublishedActivities 异常", e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return list;
    }


    // 更新所有活动的 apply_status，根据当前时间和 apply_time
    public void updateApplyStatus() {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            Log.d(TAG, "updateApplyStatus 开始执行");

            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            String sql = "SELECT id, apply_time, apply_status FROM activity";
            cursor = db.rawQuery(sql, null);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.CHINA);  // ← 匹配你的格式
            long currentTime = System.currentTimeMillis();

            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String applyTimeStr = cursor.getString(cursor.getColumnIndexOrThrow("apply_time"));
                int currentStatus = cursor.getInt(cursor.getColumnIndexOrThrow("apply_status"));

                if (currentStatus == 2) {
                    Log.d(TAG, "活动ID=" + id + " 已结束，跳过");
                    continue;
                }

                if (applyTimeStr == null || applyTimeStr.isEmpty()) {
                    Log.w(TAG, "活动ID=" + id + " apply_time 为空，跳过");
                    continue;
                }

                try {
                    long applyTime = sdf.parse(applyTimeStr).getTime();

                    if (currentTime > applyTime) {
                        ContentValues values = new ContentValues();
                        values.put("apply_status", 2);
                        db.update("activity", values, "id = ?", new String[]{String.valueOf(id)});
                        Log.d(TAG, "更新活动ID=" + id + " apply_status 为 2 (已结束)");
                    } else if (currentStatus == 0) {
                        // 可选：假设未结束就是进行中（如果你有开始时间，可以加判断）
                        ContentValues values = new ContentValues();
                        values.put("apply_status", 1);
                        db.update("activity", values, "id = ?", new String[]{String.valueOf(id)});
                        Log.d(TAG, "更新活动ID=" + id + " apply_status 为 1 (进行中)");
                    }
                } catch (ParseException e) {
                    Log.e(TAG, "活动ID=" + id + " apply_time 格式错误：" + applyTimeStr, e);
                }
            }

            db.setTransactionSuccessful();
            Log.d(TAG, "updateApplyStatus 执行完成");

        } catch (Exception e) {
            Log.e(TAG, "updateApplyStatus 异常", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }


}
