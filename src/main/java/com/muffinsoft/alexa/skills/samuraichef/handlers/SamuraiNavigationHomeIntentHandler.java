package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.muffinsoft.alexa.sdk.activities.SessionStateManager;
import com.muffinsoft.alexa.sdk.handlers.NavigateHomeIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.content.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;

public class SamuraiNavigationHomeIntentHandler extends NavigateHomeIntentHandler {

    private final PhraseManager phraseManager;
    private final CardManager cardManager;

    public SamuraiNavigationHomeIntentHandler(CardManager cardManager, PhraseManager phraseManager) {
        super();
        this.phraseManager = phraseManager;
        this.cardManager = cardManager;
    }

    @Override
    public SessionStateManager nextTurn(HandlerInput handlerInput) {
        return null;
    }
}
