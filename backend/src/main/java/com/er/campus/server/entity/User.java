package com.er.campus.server.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user")
@Data
public class User {

    @Id
    @Column(name = "user_id")
    private String userId;

    @JsonAlias("userName")
    private String name;

    @JsonAlias("userGarde")
    private String grade;

    @JsonAlias("userSchool")
    private String school;

    @JsonAlias("userPhone")
    private String phone;

    @JsonAlias("userPassword")
    private String password;

    @Column(name = "is_admin")
    private Integer isAdmin = 0;

    public enum Role {
        USER(0), ADMIN(1);
        private final int value;
        Role(int value) { this.value = value; }
        public int getValue() { return value; }
    }

    public User() {
    }

    public User(String userId, String name, String grade, String school,
                String phone, String password, Integer isAdmin) {
        this.userId = userId;
        this.name = name;
        this.grade = grade;
        this.school = school;
        this.phone = phone;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Integer isAdmin) {
        this.isAdmin = isAdmin;
    }
}