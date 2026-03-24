package com.er.campusactivity.entry;

import java.io.Serializable;

public class MyActivity implements Serializable {

    // 对应数据库的字段
    private int id;
    private String name;
    private String intro;
    private String dept;
    private String activityTime;
    private String applyTime;
    private String requirement;
    private String location;
    private String publisherId;
    private int applyStatus;     // 报名状态：0-未开始 1-进行中 2-已结束
    private int activityStatus;  // 活动状态：0-未开始 1-进行中 2-已结束
    private long signupCount;

    public MyActivity(int id, String name, String intro, String dept,
                      String activityTime, String applyTime, String requirement,
                      String location, String publisherId, int applyStatus, int activityStatus,long signupCount) {
        this.id = id;
        this.name = name;
        this.intro = intro;
        this.dept = dept;
        this.activityTime = activityTime;
        this.applyTime = applyTime;
        this.requirement = requirement;
        this.location = location;
        this.publisherId = publisherId;
        this.applyStatus = applyStatus;
        this.activityStatus = activityStatus;
        this.signupCount = signupCount;
    }

    public MyActivity(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(String activityTime) {
        this.activityTime = activityTime;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public int getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(int applyStatus) {
        this.applyStatus = applyStatus;
    }

    public int getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(int activityStatus) {
        this.activityStatus = activityStatus;
    }

    public long getSignupCount() {
        return signupCount;
    }

    public void setSignupCount(long signupCount) {
        this.signupCount = signupCount;
    }
}
