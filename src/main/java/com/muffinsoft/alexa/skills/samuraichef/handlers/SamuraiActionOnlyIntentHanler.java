package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.muffinsoft.alexa.sdk.components.IntentFactory;

public class SamuraiActionOnlyIntentHanler extends SamuraiGameIntentHandler {

    public SamuraiActionOnlyIntentHanler(IntentFactory intentFactory) {
        super(intentFactory);
    }

    @Override
    protected String getIntentName() {
        return "ActionOnlyIntent";
    }
}
