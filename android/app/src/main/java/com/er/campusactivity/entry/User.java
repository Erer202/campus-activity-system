package com.er.campusactivity.entry;

import com.google.gson.annotations.SerializedName;

public class User implements java.io.Serializable {

    @SerializedName("userId")
    private String userId;

    @SerializedName(value = "userPassword", alternate = {"password"})
    private String userPassword;

    @SerializedName(value = "userSchool", alternate = {"school"})
    private String userSchool;

    private Role role;

    @SerializedName(value = "userPhone", alternate = {"phone"})
    private String userPhone;

    @SerializedName(value = "userName", alternate = {"name"})
    private String userName;

    @SerializedName(value = "userGarde", alternate = {"grade"})
    private String userGarde;

    @SerializedName("isAdmin")
    private int isAdmin;

    public enum Role {
        USER(0), ADMIN(1);
        private final int value;
        Role(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    public User(String userId, String userPassword, String userSchool, String userGarde,
                String userName, String userPhone, int isAdmin) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.userSchool = userSchool;
        this.userName = userName;
        this.userGarde = userGarde;
        this.userPhone = userPhone;
        this.isAdmin = isAdmin;
    }

    public User() {}

    public User(String userId, String userPassword) {
        this.userId = userId;
        this.userPassword = userPassword;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getUserGarde() {
        return userGarde;
    }

    public void setUserGarde(String userGarde) {
        this.userGarde = userGarde;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserSchool() {
        return userSchool;
    }

    public void setUserSchool(String userSchool) {
        this.userSchool = userSchool;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }
}
