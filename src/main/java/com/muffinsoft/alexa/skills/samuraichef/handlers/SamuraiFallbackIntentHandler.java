package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.muffinsoft.alexa.sdk.handlers.FallbackIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;

public class SamuraiFallbackIntentHandler extends FallbackIntentHandler {

    private final PhraseManager phraseManager;

    public SamuraiFallbackIntentHandler(PhraseManager phraseManager) {
        super();
        this.phraseManager = phraseManager;
    }

    @Override
    public String getPhrase() {
        return null;
    }

    @Override
    public String getSimpleCard() {
        return null;
    }
}
