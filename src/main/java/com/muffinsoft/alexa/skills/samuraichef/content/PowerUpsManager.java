package com.muffinsoft.alexa.skills.samuraichef.content;

import com.muffinsoft.alexa.sdk.content.BaseContentManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;

public class PowerUpsManager extends BaseContentManager<String> {

    public PowerUpsManager(String path) {
        super(path);
    }

    public String getNextRandomForActivity(Activities currentActivity) {
        return "Sushi Blade";
    }
}
