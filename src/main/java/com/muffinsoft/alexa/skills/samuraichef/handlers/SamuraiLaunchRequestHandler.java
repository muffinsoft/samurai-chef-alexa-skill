package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.muffinsoft.alexa.sdk.activities.StateManager;
import com.muffinsoft.alexa.sdk.handlers.LaunchRequestHandler;
import com.muffinsoft.alexa.skills.samuraichef.activities.LaunchStateManager;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;

public class SamuraiLaunchRequestHandler extends LaunchRequestHandler {

    private final ConfigContainer configContainer;

    public SamuraiLaunchRequestHandler(ConfigContainer configContainer) {
        super();
        this.configContainer = configContainer;
    }

    @Override
    public StateManager nextTurn(HandlerInput input) {
        return new LaunchStateManager(getSlotsFromInput(input), input.getAttributesManager(), configContainer);
    }
}
