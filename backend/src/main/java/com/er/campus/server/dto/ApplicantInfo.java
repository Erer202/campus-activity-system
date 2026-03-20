package com.er.campus.server.dto;

public class ApplicantInfo {
    private String userId;
    private String userName;
    private String userPhone;
    private String userSchool;
    private String userGrade;
    private Integer isAdmin;

    public ApplicantInfo() {
    }

    public ApplicantInfo(String userId, String userName, String userPhone,
                         String userSchool, String userGrade, Integer isAdmin) {
        this.userId = userId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userSchool = userSchool;
        this.userGrade = userGrade;
        this.isAdmin = isAdmin;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getUserSchool() {
        return userSchool;
    }

    public String getUserGrade() {
        return userGrade;
    }

    public Integer getIsAdmin() {
        return isAdmin;
    }
}
