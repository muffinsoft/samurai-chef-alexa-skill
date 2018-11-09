package com.muffinsoft.alexa.skills.samuraichef.models;

import java.util.Map;

public class LevelActivities {

    private String title;

    private Map<String, Integer> activities;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Integer> getActivities() {
        return activities;
    }

    public void setActivities(Map<String, Integer> activities) {
        this.activities = activities;
    }
}
