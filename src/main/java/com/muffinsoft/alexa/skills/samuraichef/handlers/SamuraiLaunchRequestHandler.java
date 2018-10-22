package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.muffinsoft.alexa.sdk.handlers.LaunchRequestHandler;
import com.muffinsoft.alexa.skills.samuraichef.content.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;

import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WELCOME_PHRASE;

public class SamuraiLaunchRequestHandler extends LaunchRequestHandler {

    private final PhraseManager phraseManager;
    private final CardManager cardManager;

    public SamuraiLaunchRequestHandler(CardManager cardManager, PhraseManager phraseManager) {
        super();
        this.phraseManager = phraseManager;
        this.cardManager = cardManager;
    }

    @Override
    public String getPhrase() {
        return phraseManager.getValueByKey(WELCOME_PHRASE);
    }

    @Override
    public String getSimpleCard() {
        return cardManager.getValueByKey("welcome");
    }

}
