package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.muffinsoft.alexa.sdk.handlers.FallbackIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.content.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;

public class SamuraiFallbackIntentHandler extends FallbackIntentHandler {

    private final PhraseManager phraseManager;
    private final CardManager cardManager;

    public SamuraiFallbackIntentHandler(CardManager cardManager, PhraseManager phraseManager) {
        super();
        this.phraseManager = phraseManager;
        this.cardManager = cardManager;
    }

    @Override
    public String getPhrase() {
        return phraseManager.getValueByKey("fallback");
    }

    @Override
    public String getSimpleCard() {
        return cardManager.getValueByKey("fallback");
    }
}
