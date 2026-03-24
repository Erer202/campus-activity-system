package com.er.campus.server.repository;

import com.er.campus.server.entity.Apply;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplyRepository extends JpaRepository<Apply, Integer> {

    boolean existsByStudentIdAndActivityId(String studentId, Integer activityId);
    @Transactional
    long deleteByStudentIdAndActivityId(String studentId, Integer activityId);


    List<Apply> findByActivityId(Integer activityId);

    List<Apply> findByStudentId(String studentId);

    // 查找该活动的报名人数
    long countByActivityId(Integer activityId);
}