package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.muffinsoft.alexa.sdk.components.IntentFactory;

public class SamuraiActionIntentHandler extends SamuraiGameIntentHandler {

    public SamuraiActionIntentHandler(IntentFactory intentFactory) {
        super(intentFactory);
    }

    @Override
    protected String getIntentName() {
        return "ActionIntent";
    }
}
