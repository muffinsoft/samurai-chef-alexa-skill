package com.muffinsoft.alexa.skills.samuraichef.content;

import com.muffinsoft.alexa.sdk.content.BaseContentManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;

import java.util.Map;

public class ActivitiesManager extends BaseContentManager<Integer> {

    public ActivitiesManager(String path) {
        super(path);
    }

    public Activities getNextActivity(Activities currentActivity) {

        Integer valueByKey = getValueByKey(currentActivity.name());

        int searchedOderValue = valueByKey + 1;

        if (searchedOderValue > Activities.values().length - 1) {
            searchedOderValue = 1;
        }

        for (Map.Entry<String, Integer> entry : getContainer().entrySet()) {
            if (entry.getValue() == searchedOderValue) {
                return Activities.valueOf(String.valueOf(entry.getKey()));
            }
        }
        return currentActivity;
    }
}
