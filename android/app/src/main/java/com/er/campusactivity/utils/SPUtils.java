package com.er.campusactivity.utils;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

/**
 * SharedPreferences 工具类：存储/读取登录信息
 */
public class SPUtils {
    // SP文件名（自定义，比如“login_info”）
    private static final String SP_NAME = "login_info";
    // 存储的key（自定义）
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER = "is_remember"; // 是否记住密码

    private static SharedPreferences getSP(Context context) {
        // 获取SP实例：MODE_PRIVATE → 仅本应用可访问
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 保存登录信息
     * @param context 上下文
     * @param username 账号
     * @param password 密码（会Base64加密）
     * @param isRemember 是否记住密码
     */
    public static void saveLoginInfo(Context context, String username, String password, boolean isRemember) {
        SharedPreferences.Editor editor = getSP(context).edit();
        editor.putBoolean(KEY_REMEMBER, isRemember);
        if (isRemember) {
            // 记住密码：存储账号 + 加密后的密码
            editor.putString(KEY_USERNAME, username);
            editor.putString(KEY_PASSWORD, encrypt(password)); // 加密存储
        } else {
            // 不记住：清空存储
            editor.remove(KEY_USERNAME);
            editor.remove(KEY_PASSWORD);
        }
        editor.apply(); // 异步提交（比commit更高效）
    }

    /**
     * 获取是否记住密码
     */
    public static boolean isRememberPwd(Context context) {
        return getSP(context).getBoolean(KEY_REMEMBER, false);
    }

    /**
     * 获取存储的账号
     */
    public static String getUsername(Context context) {
        return getSP(context).getString(KEY_USERNAME, "");
    }

    /**
     * 获取存储的密码（解密后）
     */
    public static String getPassword(Context context) {
        String encryptPwd = getSP(context).getString(KEY_PASSWORD, "");
        return decrypt(encryptPwd); // 解密返回
    }

    /**
     * 清除登录信息（退出登录时调用）
     */
    public static void clearLoginInfo(Context context) {
        getSP(context).edit().clear().apply();
    }

    // ====== 简单加密/解密（Base64）======
    private static String encrypt(String content) {
        // Base64加密：避免明文存储
        return Base64.encodeToString(content.getBytes(), Base64.DEFAULT);
    }

    private static String decrypt(String encryptContent) {
        // Base64解密
        if (encryptContent.isEmpty()) return "";
        return new String(Base64.decode(encryptContent, Base64.DEFAULT));
    }
}