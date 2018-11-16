package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.muffinsoft.alexa.sdk.activities.StateManager;
import com.muffinsoft.alexa.sdk.handlers.HelpIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.activities.HelpStateManager;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;

public class SamuraiHelpIntentHandler extends HelpIntentHandler {

    private final ConfigContainer configurationContainer;

    public SamuraiHelpIntentHandler(ConfigContainer configurationContainer) {
        super();
        this.configurationContainer = configurationContainer;
    }

    @Override
    public StateManager nextTurn(HandlerInput handlerInput) {
        return new HelpStateManager(getSlotsFromInput(handlerInput), handlerInput.getAttributesManager(), configurationContainer);
    }
}