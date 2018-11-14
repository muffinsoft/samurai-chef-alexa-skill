package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Response;
import com.muffinsoft.alexa.sdk.handlers.LaunchRequestHandler;
import com.muffinsoft.alexa.skills.samuraichef.content.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

import static com.muffinsoft.alexa.skills.samuraichef.constants.CardConstants.WELCOME_CARD;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WELCOME_BACK_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WELCOME_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_HIGH_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_LOW_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_MID_PROGRESS_DB;

public class SamuraiLaunchRequestHandler extends LaunchRequestHandler {

    private static final Logger logger = LogManager.getLogger(SamuraiLaunchRequestHandler.class);

    private final PhraseManager phraseManager;
    private final CardManager cardManager;

    public SamuraiLaunchRequestHandler(CardManager cardManager, PhraseManager phraseManager) {
        super();
        this.phraseManager = phraseManager;
        this.cardManager = cardManager;
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {

        String userId = input.getRequestEnvelope().getSession().getUser().getUserId();

        String simpleCard = this.getSimpleCard();

        String speechText;

        if (input.getAttributesManager().getPersistentAttributes().containsKey(USER_LOW_PROGRESS_DB)
                ||
                input.getAttributesManager().getPersistentAttributes().containsKey(USER_MID_PROGRESS_DB)
                ||
                input.getAttributesManager().getPersistentAttributes().containsKey(USER_HIGH_PROGRESS_DB)) {
            speechText = buildRoyalGreeting();

            logger.info(userId + " - Existing user was started new Game Session. Start Royal Greeting");

        }
        else {
            speechText = this.getPhrase();

            logger.info(userId + " - New user was started new Game Session.");
        }

        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard(simpleCard, speechText)
                .withReprompt(speechText)
                .build();
    }

    private String buildRoyalGreeting() {
        return phraseManager.getValueByKey(WELCOME_BACK_PHRASE);
    }

    @Override
    public String getPhrase() {
        return phraseManager.getValueByKey(WELCOME_PHRASE);
    }

    @Override
    public String getSimpleCard() {
        return cardManager.getValueByKey(WELCOME_CARD);
    }

}
