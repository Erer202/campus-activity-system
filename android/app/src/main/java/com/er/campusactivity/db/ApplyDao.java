package com.er.campusactivity.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.er.campusactivity.entry.MyActivity;
import com.er.campusactivity.entry.User;

import java.util.ArrayList;
import java.util.List;

public class ApplyDao {
    private static final String TAG = "ApplyDao";
    private DBHelper dbHelper;
    private Context context;

    public ApplyDao(Context context){
        this.dbHelper = new DBHelper(context);
        this.context = context;
    }

    // 报名
    public boolean signUp(String userId, int activityId) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            // 先查询该活动是否存在，且报名状态为“进行中”（apply_status=1）
            String activitySql = "SELECT apply_status FROM activity WHERE id=?";
            Cursor activityCursor = db.rawQuery(activitySql, new String[]{String.valueOf(activityId)});
            if (!activityCursor.moveToFirst()) {
                Log.d(TAG, "signup失败：活动ID=" + activityId + "不存在");
                activityCursor.close();
                return false;
            }
            int applyStatus = activityCursor.getInt(activityCursor.getColumnIndex("apply_status"));
            activityCursor.close();

            // 判断报名状态是否为“进行中”（1），非1则无法报名
            if (applyStatus != 1) {
                Log.d(TAG, "signup失败：活动ID=" + activityId + "报名状态为" + applyStatus + "（仅1=进行中可报名）");
                return false;
            }

            // 检查是否已报名（避免重复报名）
            if (isApplied(db,userId, activityId)) {
                Log.d(TAG, "signup失败：用户" + userId + "已报名活动" + activityId);
                return false;
            }

            // 插入报名记录到apply表
            String insertSql = "INSERT INTO apply (student_id, activity_id) VALUES (?, ?)";
            db.execSQL(insertSql, new Object[]{userId, activityId});
            Log.d(TAG, "signup成功：用户" + userId + "报名活动" + activityId);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "signup异常：", e);
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    // 管理员强制报名
    public boolean adminSignUp(String userId, int activityId) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            // 检查是否已报名（避免重复报名）
            if (isApplied(db,userId, activityId)) {
                Log.d(TAG, "adminSignUp失败：用户" + userId + "已报名活动" + activityId);
                return false;
            }

            // 插入报名记录（不判断报名状态）
            String insertSql = "INSERT INTO apply (student_id, activity_id) VALUES (?, ?)";
            db.execSQL(insertSql, new Object[]{userId, activityId});
            Log.d(TAG, "adminSignUp成功：管理员为用户" + userId + "报名活动" + activityId);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "adminSignUp异常：", e);
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
    // 取消报名
    public boolean cancelSignUp(String userId, int activityId) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();

            // 直接尝试删除报名记录
            int deleteCount = db.delete(
                    "apply",
                    "student_id = ? AND activity_id = ?",
                    new String[]{userId, String.valueOf(activityId)}
            );

            if (deleteCount > 0) {
                Log.d(TAG, "取消报名成功：用户 " + userId + " 取消了活动 " + activityId);
                return true;
            } else {
                Log.w(TAG, "取消报名失败：没有找到对应记录（可能已取消或数据不一致） userId=" + userId + ", activityId=" + activityId);
                return false;
            }

        } catch (Exception e) {
            Log.e(TAG, "cancelSignUp 异常", e);
            return false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }


    // 供外部传入db实例，不关闭db
    private boolean isApplied(SQLiteDatabase db, String userId, int activityId) {
        Cursor cursor = null;
        try {
            String sql = "SELECT id FROM apply WHERE student_id=? AND activity_id=?";
            cursor = db.rawQuery(sql, new String[]{userId, String.valueOf(activityId)});
            return cursor.moveToFirst(); // 有记录则返回true
        } catch (Exception e) {
            Log.e(TAG, "isApplied异常（传入db）：", e);
            return false;
        } finally {
            // 仅关闭Cursor，不关闭db
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // 获取当前用户已报名的所有活动列表
    public List<MyActivity> getMyJoinActivities(String userId) {
        List<MyActivity> list = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            // SQL查询语句完全匹配数据库表/字段名
            String sql = "SELECT a.* FROM activity a " +
                    "INNER JOIN apply ap ON a.id = ap.activity_id " +
                    "WHERE ap.student_id = ? " +  // apply表的student_id对应user表的user_id
                    "ORDER BY a.id DESC";

            cursor = db.rawQuery(sql, new String[]{userId});

            // 遍历查询结果，注意字段名必须和CREATE_ACTIVITY_TABLE中的一致
            while (cursor.moveToNext()) {
                MyActivity activity = new MyActivity();


                activity.setId(cursor.getInt(cursor.getColumnIndex("id")));
                activity.setName(cursor.getString(cursor.getColumnIndex("name")));
                activity.setIntro(cursor.getString(cursor.getColumnIndex("intro")));
                activity.setDept(cursor.getString(cursor.getColumnIndex("dept")));
                activity.setActivityTime(cursor.getString(cursor.getColumnIndex("activity_time")));

                activity.setApplyTime(cursor.getString(cursor.getColumnIndex("apply_time")));
                activity.setRequirement(cursor.getString(cursor.getColumnIndex("requirement")));
                activity.setLocation(cursor.getString(cursor.getColumnIndex("location")));

                activity.setPublisherId(cursor.getString(cursor.getColumnIndex("publisher_id")));
                activity.setApplyStatus(cursor.getInt(cursor.getColumnIndex("apply_status")));
                activity.setActivityStatus(cursor.getInt(cursor.getColumnIndex("activity_status")));

                list.add(activity);
            }

            Log.d(TAG, "查询到用户 " + userId + " 已报名 " + list.size() + " 条活动");

        } catch (Exception e) {
            Log.e(TAG, "getMyJoinActivities 异常", e);
        } finally {
            // 资源关闭：先关Cursor，再关DB，避免内存泄漏
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return list;
    }


    // 检查是否已经报名
    public boolean isApplied(String userId, int activityId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            String sql = "SELECT id FROM apply WHERE student_id=? AND activity_id=?";
            cursor = db.rawQuery(sql, new String[]{userId, String.valueOf(activityId)});
            return cursor.moveToFirst(); // 有记录则返回true
        } catch (Exception e) {
            Log.e(TAG, "isApplied异常：", e);
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }


    // 判断指定活动是否由指定用户发布
    public boolean isPublished(String userId, int activityId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            // 查询activity表中指定id的活动，且发布者id匹配
            String sql = "SELECT id FROM activity WHERE id = ? AND publisher_id = ?";
            cursor = db.rawQuery(sql, new String[]{String.valueOf(activityId), userId});

            boolean isPublisher = cursor.moveToFirst();
            if (isPublisher) {
                Log.d(TAG, "用户" + userId + "是活动" + activityId + "的发布者");
            } else {
                Log.d(TAG, "用户" + userId + "不是活动" + activityId + "的发布者");
            }
            return isPublisher;

        } catch (Exception e) {
            Log.e(TAG, "isPublished异常：", e);
            return false;
        } finally {
            // 关闭游标和数据库连接
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    public List<User> getApplicantsForActivity(int activityId) {
        List<User> list = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            String sql = "SELECT u.user_id, u.name " +  // 只查询学号和姓名
                    "FROM user u " +
                    "INNER JOIN apply ap ON u.user_id = ap.student_id " +
                    "WHERE ap.activity_id = ?";

            cursor = db.rawQuery(sql, new String[]{String.valueOf(activityId)});

            while (cursor.moveToNext()) {
                User user = new User();
                user.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
                user.setUserName(cursor.getString(cursor.getColumnIndexOrThrow("name")));


                list.add(user);
            }

            Log.d(TAG, "查询到活动 " + activityId + " 的报名者数量: " + list.size());

        } catch (Exception e) {
            Log.e(TAG, "getApplicantsForActivity 异常", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return list;
    }

}
