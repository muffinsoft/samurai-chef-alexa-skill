package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.muffinsoft.alexa.sdk.handlers.HelpIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.content.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;

public class SamuraiHelpIntentHandler extends HelpIntentHandler {

    private final PhraseManager phraseManager;
    private final CardManager cardManager;

    public SamuraiHelpIntentHandler(CardManager cardManager, PhraseManager phraseManager) {
        super();
        this.phraseManager = phraseManager;
        this.cardManager = cardManager;
    }

    @Override
    public String getPhrase() {
        return "Here is description";
    }

    @Override
    public String getSimpleCard() {
        return cardManager.getValueByKey("welcome");
    }
}