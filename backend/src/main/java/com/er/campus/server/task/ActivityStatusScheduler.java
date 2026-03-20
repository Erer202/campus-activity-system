package com.er.campus.server.task;

import com.er.campus.server.entity.MyActivity;
import com.er.campus.server.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ActivityStatusScheduler {

    @Autowired
    private ActivityRepository activityRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    // 每分钟执行一次
    @Scheduled(cron = "0 * * * * ?")
    public void updateApplyStatus() {
        List<MyActivity> activityList = activityRepository.findByApplyStatus(1);

        LocalDateTime now = LocalDateTime.now();

        for (MyActivity activity : activityList) {
            try {
                String applyTimeStr = activity.getApplyTime();

                if (applyTimeStr == null || applyTimeStr.trim().isEmpty()) {
                    continue;
                }

                LocalDateTime applyDeadline = LocalDateTime.parse(applyTimeStr, formatter);

                if (now.isAfter(applyDeadline)) {
                    activity.setApplyStatus(2); // 报名结束
                    activityRepository.save(activity);

                    System.out.println("活动报名状态已更新：activityId=" + activity.getId()
                            + ", name=" + activity.getName()
                            + ", applyStatus=2");
                }
            } catch (Exception e) {
                System.err.println("解析报名截止时间失败，activityId=" + activity.getId()
                        + ", applyTime=" + activity.getApplyTime());
                e.printStackTrace();
            }
        }
    }
}
