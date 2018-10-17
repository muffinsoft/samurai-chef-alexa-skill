package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.muffinsoft.alexa.sdk.handlers.LaunchRequestHandler;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;

public class SamuraiLaunchRequestHandler extends LaunchRequestHandler {

    private final PhraseManager phraseManager;

    public SamuraiLaunchRequestHandler(PhraseManager phraseManager) {
        super();
        this.phraseManager = phraseManager;
    }

    @Override
    public String getPhrase() {
        return phraseManager.getValueByKey("welcomeMessage");
    }

    @Override
    public String getSimpleCard() {
        return phraseManager.getValueByKey("welcomeCard");
    }

}
