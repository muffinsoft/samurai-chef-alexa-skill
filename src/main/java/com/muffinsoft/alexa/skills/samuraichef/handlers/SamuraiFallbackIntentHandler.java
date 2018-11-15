package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.muffinsoft.alexa.sdk.activities.SessionStateManager;
import com.muffinsoft.alexa.sdk.handlers.FallbackIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.content.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;

public class SamuraiFallbackIntentHandler extends FallbackIntentHandler {

    private final PhraseManager phraseManager;
    private final CardManager cardManager;

    public SamuraiFallbackIntentHandler(ConfigContainer configurationContainer) {
        super();
        this.phraseManager = configurationContainer.getPhraseManager();
        this.cardManager = configurationContainer.getCardManager();
    }

    @Override
    public SessionStateManager nextTurn(HandlerInput handlerInput) {
        return null;
    }
}
