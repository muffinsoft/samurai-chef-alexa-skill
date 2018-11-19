package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.activities.StateManager;
import com.muffinsoft.alexa.sdk.handlers.StopIntentHandler;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;

import static com.muffinsoft.alexa.sdk.model.Speech.ofAlexa;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.EXIT_PHRASE;

public class SamuraiStopIntentHandler extends StopIntentHandler {

    private final PhraseManager phraseManager;

    public SamuraiStopIntentHandler(ConfigContainer configurationContainer) {
        super();
        this.phraseManager = configurationContainer.getPhraseManager();
    }

    @Override
    public StateManager nextTurn(HandlerInput handlerInput) {
        return new BaseStateManager(getSlotsFromInput(handlerInput), handlerInput.getAttributesManager()) {
            @Override
            public DialogItem nextResponse() {
                return DialogItem.builder()
                        .addResponse(ofAlexa(phraseManager.getValueByKey(EXIT_PHRASE)))
                        .withShouldEnd(true)
                        .build();
            }
        };
    }
}
