package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.muffinsoft.alexa.sdk.components.IntentFactory;

public class SamuraiSelectPathOnlyIntentHandler extends SamuraiGameIntentHandler {

    public SamuraiSelectPathOnlyIntentHandler(IntentFactory intentFactory) {
        super(intentFactory);
    }

    @Override
    protected String getIntentName() {
        return "SelectPathIntent";
    }
}
