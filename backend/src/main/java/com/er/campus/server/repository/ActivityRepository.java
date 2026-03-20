package com.er.campus.server.repository;

import com.er.campus.server.entity.MyActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ActivityRepository extends JpaRepository<MyActivity, Integer> {
    // 自动实现：根据名称模糊查询 + 根据ID精确查询
    List<MyActivity> findByNameContainingOrIdEquals(String name, Integer id);

    List<MyActivity> findByNameContaining(String name);

    List<MyActivity> findByPublisherIdOrderByIdDesc(String publisherId);

    // 查找申请状态
    List<MyActivity> findByApplyStatus(Integer applyStatus);

    @Query(value = "SELECT a.* FROM activity a " +
            "INNER JOIN apply ap ON a.id = ap.activity_id " +
            "WHERE ap.student_id = ?1 " +
            "ORDER BY a.id DESC", nativeQuery = true)
    List<MyActivity> findMyJoinedActivities(String studentId);
}