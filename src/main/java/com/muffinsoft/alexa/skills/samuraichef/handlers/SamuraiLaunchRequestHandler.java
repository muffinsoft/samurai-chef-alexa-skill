package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.muffinsoft.alexa.sdk.handlers.LaunchRequestHandler;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;

public class SamuraiLaunchRequestHandler extends LaunchRequestHandler {

    @Override
    public String getPhrase() {
        return PhraseManager.getPhrase("welcome");
    }

    @Override
    public String getSimpleCard() {
        return PhraseManager.getPhrase("welcomeCard");
    }
}
