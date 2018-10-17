package com.muffinsoft.alexa.skills.samuraichef.content;

import com.muffinsoft.alexa.sdk.content.BaseContentManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;

import java.util.Map;

public class ActivitiesManager extends BaseContentManager<Integer> {

    public ActivitiesManager(String path) {
        super(path);
    }

    public Activities getNextActivity(Activities currentActivity) {

        Integer currentValue = getValueByKey(currentActivity.name());

        int searchedValue = currentValue + 1;

        for (Map.Entry entry : getContainer().entrySet()) {
            if ((Integer) entry.getValue() == searchedValue) {
                return Activities.valueOf(String.valueOf(entry.getKey()));
            }
        }
        return currentActivity;
    }
}
