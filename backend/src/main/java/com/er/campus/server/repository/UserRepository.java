package com.er.campus.server.repository;

import com.er.campus.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {

    List<User> findByUserIdIn(List<String> userIds);
}