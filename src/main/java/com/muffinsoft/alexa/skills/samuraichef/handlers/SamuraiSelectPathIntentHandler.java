package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.muffinsoft.alexa.sdk.components.IntentFactory;

public class SamuraiSelectPathIntentHandler extends SamuraiGameIntentHandler {

    public SamuraiSelectPathIntentHandler(IntentFactory intentFactory) {
        super(intentFactory);
    }

    @Override
    protected String getIntentName() {
        return "SelectPathIntent";
    }
}
