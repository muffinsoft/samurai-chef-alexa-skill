package com.muffinsoft.alexa.skills.samuraichef.content;

import com.fasterxml.jackson.core.type.TypeReference;
import com.muffinsoft.alexa.sdk.util.ContentLoader;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserLevel;
import com.muffinsoft.alexa.skills.samuraichef.models.LevelActivities;
import com.muffinsoft.alexa.skills.samuraichef.models.ProgressContainer;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProgressManager {

    private ProgressContainer container;

    public ProgressManager(String path) {
        this.container = new ContentLoader().loadContent(this.container, path, new TypeReference<ProgressContainer>() {
        });
    }

    public ProgressContainer getContainer() {
        return container;
    }

    public void setContainer(ProgressContainer container) {
        this.container = container;
    }

    public Activities getFirstActivityForLevel(UserLevel userLevel) {

        List<LevelActivities> allLevels = container.getLevels();

        for (LevelActivities level : allLevels) {

            if (Objects.equals(level.getTitle(), userLevel.name())) {
                return getFirstActivity(level.getActivities());
            }
        }
        return null;
    }

    private Activities getFirstActivity(Map<String, Integer> activities) {

        String possibleActivity = null;
        int minimalValue = 0;

        for (Map.Entry<String, Integer> entry : activities.entrySet()) {
            if (entry.getValue() <= minimalValue) {
                possibleActivity = entry.getKey();
            }
        }
        return Activities.valueOf(possibleActivity);
    }

    public Activities getNextActivity(Activities currentActivity, UserLevel currentLevel) {
        return null;
    }
}
