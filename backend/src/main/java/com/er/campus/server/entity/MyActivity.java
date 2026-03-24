package com.er.campus.server.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "activity")
@Data
public class MyActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String intro;
    private String dept;

    @Column(name = "activity_time")
    private String activityTime;

    @Column(name = "apply_time")
    private String applyTime;

    private String requirement;
    private String location;

    @Column(name = "publisher_id")
    private String publisherId;

    @Column(name = "apply_status")
    private Integer applyStatus = 0;

    @Column(name = "activity_status")
    private Integer activityStatus = 0;

    @Transient
    private Long signupCount = 0L;



}
