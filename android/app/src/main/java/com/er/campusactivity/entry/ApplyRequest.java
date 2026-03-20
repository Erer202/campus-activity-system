package com.er.campusactivity.entry;

public class ApplyRequest {
    private String studentId;
    private Integer activityId;

    public ApplyRequest(String studentId, Integer activityId) {
        this.studentId = studentId;
        this.activityId = activityId;
    }

    public String getStudentId() {
        return studentId;
    }

    public Integer getActivityId() {
        return activityId;
    }
}
