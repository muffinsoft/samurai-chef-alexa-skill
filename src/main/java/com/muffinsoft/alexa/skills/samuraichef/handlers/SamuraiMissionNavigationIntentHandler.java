package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.muffinsoft.alexa.sdk.components.IntentFactory;

public class SamuraiMissionNavigationIntentHandler extends SamuraiGameIntentHandler {

    public SamuraiMissionNavigationIntentHandler(IntentFactory intentFactory) {
        super(intentFactory);
    }

    @Override
    protected String getIntentName() {
        return "MissionNavigationIntent";
    }
}
