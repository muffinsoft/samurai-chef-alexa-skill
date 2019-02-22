package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.muffinsoft.alexa.sdk.components.IntentFactory;

public class SamuraiMissionNavigationOnlyIntentHandler extends SamuraiGameIntentHandler {

    public SamuraiMissionNavigationOnlyIntentHandler(IntentFactory intentFactory) {
        super(intentFactory);
    }

    @Override
    protected String getIntentName() {
        return "MissionNavigationIntent";
    }
}
