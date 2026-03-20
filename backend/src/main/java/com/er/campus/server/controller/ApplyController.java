package com.er.campus.server.controller;

import com.er.campus.server.dto.ApplicantInfo;
import com.er.campus.server.dto.ApplyRequest;
import com.er.campus.server.entity.Apply;
import com.er.campus.server.entity.MyActivity;
import com.er.campus.server.entity.User;
import com.er.campus.server.repository.ActivityRepository;
import com.er.campus.server.repository.ApplyRepository;
import com.er.campus.server.repository.UserRepository;
import com.er.campus.server.service.ActivityService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/apply")
@CrossOrigin("*")
public class ApplyController {

    @Autowired
    private ApplyRepository applyRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;
    private ActivityService activityService;

    // 报名
    @PostMapping
    public ResponseEntity<?> apply(@RequestBody ApplyRequest request) {
        if (request.getStudentId() == null || request.getActivityId() == null) {
            return ResponseEntity.badRequest().body("参数不能为空");
        }

        if (!userRepository.existsById(request.getStudentId())) {
            return ResponseEntity.badRequest().body("用户不存在");
        }

        Optional<MyActivity> optionalActivity = activityRepository.findById(request.getActivityId());
        if (optionalActivity.isEmpty()) {
            return ResponseEntity.badRequest().body("活动不存在");
        }

        MyActivity activity = optionalActivity.get();

        // 只有报名状态=1 才允许报名
        if (activity.getApplyStatus() != 1) {
            return ResponseEntity.badRequest().body("当前活动不在报名中");
        }

        // 防重复报名
        if (applyRepository.existsByStudentIdAndActivityId(
                request.getStudentId(), request.getActivityId())) {
            return ResponseEntity.badRequest().body("你已报名该活动");
        }

        Apply apply = new Apply(request.getStudentId(), request.getActivityId());
        Apply saved = applyRepository.save(apply);
        return ResponseEntity.ok(saved);
    }

    // 检查是否已报名
    @GetMapping("/check")
    public ResponseEntity<?> checkApplied(@RequestParam String studentId,
                                          @RequestParam Integer activityId) {
        boolean applied = applyRepository.existsByStudentIdAndActivityId(studentId, activityId);
        return ResponseEntity.ok(applied);
    }

    // 我的报名列表
    @GetMapping("/my/{studentId}")
    public ResponseEntity<?> getMyJoinActivities(@PathVariable String studentId) {
        List<MyActivity> list = activityRepository.findMyJoinedActivities(studentId);
        return ResponseEntity.ok(list);
    }

    // 取消报名

    @DeleteMapping
    @Transactional
    public ResponseEntity<?> cancelApply(@RequestParam String studentId,
                                         @RequestParam Integer activityId) {
        long count = applyRepository.deleteByStudentIdAndActivityId(studentId, activityId);

        if (count <= 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "未找到对应报名记录"));
        }

        return ResponseEntity.ok(Map.of("message", "取消报名成功"));
    }

    // 查询我发布的活动
    @GetMapping("/my/{publisherId}")
    public ResponseEntity<?> getMyPublishedActivities(@PathVariable String publisherId) {
        List<MyActivity> list = activityService.getMyPublishedActivities(publisherId);
        return ResponseEntity.ok(list);
    }

    // 删除发布的活动
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActivity(@PathVariable Integer id,
                                            @RequestParam String publisherId) {
        boolean success = activityService.deleteActivity(id, publisherId);

        if (!success) {
            return ResponseEntity.badRequest().body(Map.of("message", "删除失败：活动不存在或你无权限删除"));
        }

        return ResponseEntity.ok(Map.of("message", "删除活动成功"));
    }

    // 查看某活动报名信息
    @GetMapping("/activity/{activityId}/applicants")
    public ResponseEntity<?> getApplicantsForActivity(@PathVariable Integer activityId,
                                                      @RequestParam String publisherId) {
        Optional<MyActivity> optionalActivity = activityRepository.findById(activityId);
        if (optionalActivity.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "活动不存在"));
        }

        MyActivity activity = optionalActivity.get();
        if (activity.getPublisherId() == null || !activity.getPublisherId().equals(publisherId)) {
            return ResponseEntity.status(403).body(Map.of("message", "你无权查看该活动报名详情"));
        }

        List<Apply> applyList = applyRepository.findByActivityId(activityId);
        if (applyList.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        List<String> userIds = applyList.stream()
                .map(Apply::getStudentId)
                .toList();

        List<User> users = userRepository.findByUserIdIn(userIds);

        List<ApplicantInfo> result = users.stream()
                .map(user -> new ApplicantInfo(
                        user.getUserId(),
                        user.getName(),
                        user.getPhone(),
                        user.getSchool(),
                        user.getGrade(),
                        user.getIsAdmin()
                ))
                .toList();

        return ResponseEntity.ok(result);
    }

    // 管理员手动添加报名者
    @PostMapping("/admin-add")
    public ResponseEntity<?> adminAddApplicant(@RequestParam String publisherId,
                                               @RequestParam String studentId,
                                               @RequestParam Integer activityId) {
        Optional<User> publisherOptional = userRepository.findById(publisherId);
        if (publisherOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "管理员账号不存在"));
        }

        User publisher = publisherOptional.get();
        if (publisher.getIsAdmin() == null || publisher.getIsAdmin() != 1) {
            return ResponseEntity.status(403).body(Map.of("message", "只有管理员才能手动添加报名者"));
        }

        Optional<MyActivity> activityOptional = activityRepository.findById(activityId);
        if (activityOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "活动不存在"));
        }

        MyActivity activity = activityOptional.get();
        if (activity.getPublisherId() == null || !activity.getPublisherId().equals(publisherId)) {
            return ResponseEntity.status(403).body(Map.of("message", "你无权操作该活动"));
        }

        Optional<User> studentOptional = userRepository.findById(studentId);
        if (studentOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "要添加的用户不存在"));
        }

        if (applyRepository.existsByStudentIdAndActivityId(studentId, activityId)) {
            return ResponseEntity.badRequest().body(Map.of("message", "该用户已报名此活动"));
        }

        Apply apply = new Apply(studentId, activityId);
        applyRepository.save(apply);

        return ResponseEntity.ok(Map.of("message", "添加报名者成功"));
    }

}
