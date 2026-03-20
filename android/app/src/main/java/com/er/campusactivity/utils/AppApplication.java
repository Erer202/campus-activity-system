package com.er.campusactivity.utils;

import android.app.Application;

import com.er.campusactivity.entry.User;

public class AppApplication extends Application {

    private static AppApplication instance;
    private static User currentUser;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static AppApplication getInstance() {
        return instance;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static boolean isLogin() {
        return currentUser != null;
    }

    public static void clearCurrentUser() {
        currentUser = null;
    }

    public static void updateCurrentUser(User user) {
        if (user == null) {
            return;
        }
        currentUser = user;
    }
}
