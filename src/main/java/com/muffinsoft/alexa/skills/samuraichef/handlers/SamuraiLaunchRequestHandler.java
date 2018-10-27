package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Response;
import com.muffinsoft.alexa.sdk.handlers.LaunchRequestHandler;
import com.muffinsoft.alexa.skills.samuraichef.content.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;

import java.util.Optional;

import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WELCOME_BACK_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WELCOME_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_PROGRESS_DB;

public class SamuraiLaunchRequestHandler extends LaunchRequestHandler {

    private final PhraseManager phraseManager;
    private final CardManager cardManager;

    public SamuraiLaunchRequestHandler(CardManager cardManager, PhraseManager phraseManager) {
        super();
        this.phraseManager = phraseManager;
        this.cardManager = cardManager;
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {

        String simpleCard = this.getSimpleCard();
        String speechText;

        if (input.getAttributesManager().getPersistentAttributes().containsKey(USER_PROGRESS_DB)) {
            speechText = phraseManager.getValueByKey(WELCOME_BACK_PHRASE);
        }
        else {
            speechText = this.getPhrase();
        }

        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard(simpleCard, speechText)
                .withReprompt(speechText)
                .build();
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
