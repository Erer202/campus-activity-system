package com.er.campus.server.controller;

import com.er.campus.server.entity.MyActivity;
import com.er.campus.server.entity.User;
import com.er.campus.server.repository.UserRepository;
import com.er.campus.server.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin // 允许安卓跨域访问
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserRepository userRepository;


    // 1. 获取所有活动
    @GetMapping("/all")
    public List<MyActivity> getAllActivities() {
        return activityService.getAllActivities();
    }

    // 2. 搜索活动
    @GetMapping("/search")
    public List<MyActivity> searchActivities(@RequestParam String keyword) {
        return activityService.searchActivities(keyword);
    }

    // 发布活动
    @PostMapping("/publish")
    public ResponseEntity<?> publishActivity(@RequestBody MyActivity activity) {
        if (activity.getName() == null || activity.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "活动名称不能为空"));
        }
        if (activity.getLocation() == null || activity.getLocation().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "活动地点不能为空"));
        }
        if (activity.getDept() == null || activity.getDept().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "组织部门不能为空"));
        }
        if (activity.getActivityTime() == null || activity.getActivityTime().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "活动时间不能为空"));
        }
        if (activity.getApplyTime() == null || activity.getApplyTime().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "报名截止时间不能为空"));
        }
        if (activity.getPublisherId() == null || activity.getPublisherId().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "发布者不能为空"));
        }

        Optional<User> optionalUser = userRepository.findById(activity.getPublisherId());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "发布者不存在"));
        }

        User publisher = optionalUser.get();
        if (publisher.getIsAdmin() == null || publisher.getIsAdmin() != 1) {
            return ResponseEntity.status(403).body(Map.of("message", "只有管理员才能发布活动"));
        }

        if (activity.getIntro() == null || activity.getIntro().trim().isEmpty()) {
            activity.setIntro("暂无介绍");
        }
        if (activity.getRequirement() == null || activity.getRequirement().trim().isEmpty()) {
            activity.setRequirement("无特殊要求");
        }
        if (activity.getApplyStatus() == null) {
            activity.setApplyStatus(1);
        }
        if (activity.getActivityStatus() == null) {
            activity.setActivityStatus(0);
        }

        activity.setId(null);

        MyActivity saved = activityService.publishActivity(activity);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/my/{publisherId}")
    public ResponseEntity<?> getMyPublishedActivities(@PathVariable String publisherId) {
        List<MyActivity> list = activityService.getMyPublishedActivities(publisherId);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActivity(@PathVariable Integer id,
                                            @RequestParam String publisherId) {
        boolean success = activityService.deleteActivity(id, publisherId);

        if (!success) {
            return ResponseEntity.badRequest().body(Map.of("message", "删除失败：活动不存在或你无权限删除"));
        }

        return ResponseEntity.ok(Map.of("message", "删除活动成功"));
    }



}
