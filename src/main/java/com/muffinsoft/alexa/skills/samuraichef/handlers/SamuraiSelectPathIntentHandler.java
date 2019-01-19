package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.components.IntentFactory;

import java.util.Map;

public class SamuraiSelectPathIntentHandler extends SamuraiGameIntentHandler {

    public SamuraiSelectPathIntentHandler(IntentFactory intentFactory) {
        super(intentFactory);
    }

    @Override
    protected String getIntentName() {
        return "SelectPathIntent";
    }
}
