package com.er.campus.server.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "apply")
public class Apply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "activity_id", nullable = false)
    private Integer activityId;

    public Apply() {
    }

    public Apply(String studentId, Integer activityId) {
        this.studentId = studentId;
        this.activityId = activityId;
    }

    public Integer getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }
}
