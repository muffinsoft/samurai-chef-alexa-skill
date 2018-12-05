package com.muffinsoft.alexa.skills.samuraichef.models;

import java.util.Map;

public class MissionActivities {

    private String title;

    private Map<String, Integer> activitiesOrder;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Integer> getActivitiesOrder() {
        return activitiesOrder;
    }

    public void setActivitiesOrder(Map<String, Integer> activitiesOrder) {
        this.activitiesOrder = activitiesOrder;
    }
}
