package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.muffinsoft.alexa.sdk.handlers.StopIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.content.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;

public class SamuraiStopIntentHandler extends StopIntentHandler {

    private final PhraseManager phraseManager;
    private final CardManager cardManager;

    public SamuraiStopIntentHandler(CardManager cardManager, PhraseManager phraseManager) {
        super();
        this.phraseManager = phraseManager;
        this.cardManager = cardManager;
    }

    @Override
    public String getPhrase() {
        return "Cancel last action";
    }

    @Override
    public String getSimpleCard() {
        return cardManager.getValueByKey("welcome");
    }
}
