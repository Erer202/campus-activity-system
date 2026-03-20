package com.er.campusactivity.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.er.campusactivity.entry.User;

public class LoginDao {
    private DBHelper dbHelper;
    private Context context;

    public LoginDao(Context context) {
        this.dbHelper = new DBHelper(context);
        this.context = context;
    }

    // 登录验证：返回User对象（包含角色），null表示登录失败
    public User login(String studentId, String password){
        // 非空校验
        if (studentId.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "学号/密码不能为空", Toast.LENGTH_SHORT).show();
            return null;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            // 查询用户表：匹配学号和密码
            Cursor cursor = db.query("user", null,
                    "user_id=? AND password=?",
                    new String[]{studentId, password},
                    null, null, null);

            if (cursor.moveToFirst()) {
                // 登录成功，获取用户信息（含角色）
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String grade = cursor.getString(cursor.getColumnIndexOrThrow("grade"));
                String college = cursor.getString(cursor.getColumnIndexOrThrow("school"));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
                int isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow("is_admin"));

                // 创建User对象返回
                User user = new User(studentId,password, college, grade,name , phone, isAdmin);
                cursor.close();
                db.close();
                return user;
            } else {
                // 学号或密码错误
                Toast.makeText(context, "学号或密码错误", Toast.LENGTH_SHORT).show();
                cursor.close();
                db.close();
                return null;
            }
        } catch (Exception e) {
            Toast.makeText(context, "登录失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
            db.close();
            return null;
        }
    }

}
