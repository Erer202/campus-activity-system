package com.er.campus.server.service;

import com.er.campus.server.entity.MyActivity;
import com.er.campus.server.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public List<MyActivity> getAllActivities() {
        return activityRepository.findAll();
    }

    public List<MyActivity> searchActivities(String keyword) {
        try {
            Integer id = Integer.parseInt(keyword);
            return activityRepository.findByNameContainingOrIdEquals(keyword, id);
        } catch (NumberFormatException e) {
            return activityRepository.findByNameContaining(keyword);
        }
    }

    public MyActivity publishActivity(MyActivity activity) {
        return activityRepository.save(activity);
    }

    public List<MyActivity> getMyPublishedActivities(String publisherId) {
        return activityRepository.findByPublisherIdOrderByIdDesc(publisherId);
    }

    public boolean deleteActivity(Integer id, String publisherId) {
        Optional<MyActivity> optional = activityRepository.findById(id);
        if (optional.isEmpty()) {
            return false;
        }

        MyActivity activity = optional.get();
        if (activity.getPublisherId() == null || !activity.getPublisherId().equals(publisherId)) {
            return false;
        }

        activityRepository.deleteById(id);
        return true;
    }
}
