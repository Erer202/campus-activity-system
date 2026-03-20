package com.er.campusactivity.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    // 数据库名和版本
    private static final String DB_NAME = "campus_activity.db";
    private static final int DB_VERSION = 5;

    // 日志标签，方便调试
    private static final String TAG = "DBHelper";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(TAG, "DBHelper初始化，数据库名：" + DB_NAME + "，版本：" + DB_VERSION);
    }
    // 用户表创建语句（包含管理员标记is_admin）
    private static final String CREATE_USER_TABLE = "CREATE TABLE user (" +
            "user_id TEXT PRIMARY KEY," +
            "name TEXT NOT NULL," +
            "grade TEXT NOT NULL," +
            "school TEXT NOT NULL," +
            "phone TEXT NOT NULL," +
            "password TEXT NOT NULL," +
            "is_admin INTEGER DEFAULT 0)";

    // 活动表（先创建，后续用）
    private static final String CREATE_ACTIVITY_TABLE = "CREATE TABLE activity (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT NOT NULL," +
            "intro TEXT," +    // 活动介绍
            "dept TEXT NOT NULL," +     // 组织部门（如 “学生会”“计算机学院”）
            "activity_time TEXT NOT NULL," +
            "apply_time TEXT NOT NULL," +
            "requirement TEXT," +
            "location TEXT NOT NULL," +
            "publisher_id TEXT NOT NULL," +
            "apply_status INTEGER NOT NULL," +      // 报名状态：0-未开始 1-进行中 2-已结束
            "activity_status INTEGER NOT NULL)";    // 活动状态：0-未开始 1-进行中 2-已结束

    // 报名关联表
    private static final String CREATE_APPLY_TABLE = "CREATE TABLE apply (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "student_id TEXT NOT NULL," +
            "activity_id INTEGER NOT NULL," +
            "FOREIGN KEY(student_id) REFERENCES user(user_id)," +
            "FOREIGN KEY(activity_id) REFERENCES activity(id))";




    @Override
    public void onCreate(SQLiteDatabase db) {

        // 开启外键约束
        db.execSQL("PRAGMA foreign_keys = ON;");

        // 创建三张表
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_ACTIVITY_TABLE);
        db.execSQL(CREATE_APPLY_TABLE);

        Log.d(TAG, "三张表（user/activity/apply）创建成功");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "数据库升级：旧版本=" + oldVersion + "，新版本=" + newVersion);

        // 开发阶段：删除旧表后重建（注意：会清空所有数据）
        db.execSQL("DROP TABLE IF EXISTS apply");
        db.execSQL("DROP TABLE IF EXISTS activity");
        db.execSQL("DROP TABLE IF EXISTS user");

        // 重新创建表
        onCreate(db);
        Log.d(TAG, "数据库升级完成，已重建所有表");
    }
}
