package com.er.campusactivity.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.er.campusactivity.entry.User;

public class RegisterDao {
    private DBHelper dbHelper;
    private Context context;

    public RegisterDao(Context context) {
        this.dbHelper = new DBHelper(context);
        this.context = context;
    }

    public boolean register(User user){
        // 对注册信息进行验证是否都填写
        // 判断密码长度和手机号是否正常
        if(user.getUserId().isEmpty() || user.getUserPassword().isEmpty() || 
                user.getUserName().isEmpty() || user.getUserSchool().isEmpty() || 
                user.getUserPhone().isEmpty() || user.getUserGarde().isEmpty()){
            Toast.makeText(context,"请填写所有信息", Toast.LENGTH_SHORT).show();
            return false;
        } else if (user.getUserPassword().length() < 6) {
            Toast.makeText(context,"密码长度不能小于6", Toast.LENGTH_SHORT).show();
            return false;
        }else if (user.getUserPhone().length() != 11) {
            Toast.makeText(context,"请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return false;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("user_id", user.getUserId());
            values.put("name", user.getUserName());
            values.put("grade", user.getUserGarde());
            values.put("school", user.getUserSchool());
            values.put("phone", user.getUserPhone());
            values.put("password", user.getUserPassword());
            // 默认普通用户，is_admin=0
            if("2".equals(user.getUserId())){
                // 如果账号为 1 ，设置为管理员
                values.put("is_admin", User.Role.ADMIN.getValue());
            } else {
                values.put("is_admin", User.Role.USER.getValue());
            }


            // 插入数据：返回-1表示学号重复
            long result = db.insert("user", null, values);
            if (result == -1) {
                Toast.makeText(context, "学号已存在", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();
                return true;
            }
        } catch (Exception e) {
            Toast.makeText(context, "注册失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        } finally {
            db.close();
        }
    }


}
