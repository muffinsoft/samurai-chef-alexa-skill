package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.muffinsoft.alexa.sdk.handlers.HelpIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;

public class SamuraiHelpIntentHandler extends HelpIntentHandler {

    private final PhraseManager phraseManager;

    public SamuraiHelpIntentHandler(PhraseManager phraseManager) {
        super();
        this.phraseManager = phraseManager;
    }

    @Override
    public String getPhrase() {
        return "I am here to give details";
    }

    @Override
    public String getSimpleCard() {
        return phraseManager.getValueByKey("welcomeCard");
    }
}