package com.er.campus.server.service;

import com.er.campus.server.entity.MyActivity;
import com.er.campus.server.repository.ActivityRepository;
import com.er.campus.server.repository.ApplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private ApplyRepository applyRepository;


    public List<MyActivity> getAllActivities() {
        List<MyActivity> list = activityRepository.findAll();
        fillSignupCount(list);
        return activityRepository.findAll();
    }

    public List<MyActivity> searchActivities(String keyword) {
        List<MyActivity> list;
        try {
            Integer id = Integer.parseInt(keyword);
            return activityRepository.findByNameContainingOrIdEquals(keyword, id);
        } catch (NumberFormatException e) {
            list = activityRepository.findByNameContaining(keyword);
        }
        fillSignupCount(list);
        return list;
    }

    private void fillSignupCount(List<MyActivity> list) {
        for (MyActivity activity : list) {
            long count = applyRepository.countByActivityId(activity.getId());
            activity.setSignupCount(count);
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
