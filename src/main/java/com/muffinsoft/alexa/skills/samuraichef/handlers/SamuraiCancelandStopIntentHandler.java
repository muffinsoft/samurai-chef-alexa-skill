package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.muffinsoft.alexa.sdk.handlers.CancelandStopIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;

public class SamuraiCancelandStopIntentHandler extends CancelandStopIntentHandler {

    private final PhraseManager phraseManager;

    public SamuraiCancelandStopIntentHandler(PhraseManager phraseManager) {
        super();
        this.phraseManager = phraseManager;
    }

    @Override
    public String getPhrase() {
        return "Bye Bye";
    }

    @Override
    public String getSimpleCard() {
        return phraseManager.getValueByKey("welcomeCard");
    }
}
