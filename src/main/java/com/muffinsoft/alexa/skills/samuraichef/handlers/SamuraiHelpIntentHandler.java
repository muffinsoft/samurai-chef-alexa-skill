package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.muffinsoft.alexa.sdk.activities.StateManager;
import com.muffinsoft.alexa.sdk.handlers.HelpIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.activities.HelpStateManager;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;

public class SamuraiHelpIntentHandler extends HelpIntentHandler {

    private final SettingsDependencyContainer configurationContainer;
    private final PhraseDependencyContainer phraseDependencyContainer;

    public SamuraiHelpIntentHandler(SettingsDependencyContainer configurationContainer, PhraseDependencyContainer phraseDependencyContainer) {
        super();
        this.configurationContainer = configurationContainer;
        this.phraseDependencyContainer = phraseDependencyContainer;
    }

    @Override
    public StateManager nextTurn(HandlerInput handlerInput) {
        return new HelpStateManager(getSlotsFromInput(handlerInput), handlerInput.getAttributesManager(), configurationContainer, phraseDependencyContainer);
    }
}