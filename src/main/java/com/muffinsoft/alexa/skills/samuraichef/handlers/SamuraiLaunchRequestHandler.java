package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.muffinsoft.alexa.sdk.activities.StateManager;
import com.muffinsoft.alexa.sdk.handlers.LaunchRequestHandler;
import com.muffinsoft.alexa.skills.samuraichef.activities.LaunchStateManager;
import com.muffinsoft.alexa.skills.samuraichef.content.GreetingsManager;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;

public class SamuraiLaunchRequestHandler extends LaunchRequestHandler {

    private final ConfigContainer configContainer;
    private final GreetingsManager greetingsManager;

    public SamuraiLaunchRequestHandler(ConfigContainer configContainer, GreetingsManager greetingsManager) {
        super();
        this.configContainer = configContainer;
        this.greetingsManager = greetingsManager;
    }

    @Override
    public StateManager nextTurn(HandlerInput input) {
        return new LaunchStateManager(getSlotsFromInput(input), input.getAttributesManager(), greetingsManager, configContainer);
    }
}
