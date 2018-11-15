package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.activities.StateManager;
import com.muffinsoft.alexa.sdk.handlers.LaunchRequestHandler;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.Speech;
import com.muffinsoft.alexa.skills.samuraichef.content.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;

import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.constants.CardConstants.WELCOME_CARD;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WELCOME_BACK_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WELCOME_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_HIGH_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_LOW_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_MID_PROGRESS_DB;

public class SamuraiLaunchRequestHandler extends LaunchRequestHandler {

    private final PhraseManager phraseManager;
    private final CardManager cardManager;

    public SamuraiLaunchRequestHandler(ConfigContainer configContainer) {
        super();
        this.phraseManager = configContainer.getPhraseManager();
        this.cardManager = configContainer.getCardManager();
    }

    @Override
    public StateManager nextTurn(HandlerInput input) {

        Map<String, Slot> slots = getSlotsFromInput(input);

        return new BaseStateManager(slots, input.getAttributesManager()) {

            private String buildRoyalGreeting() {
                return phraseManager.getValueByKey(WELCOME_BACK_PHRASE);
            }

            @Override
            public DialogItem nextResponse() {

                String speechText;

                if (getPersistentAttributes().containsKey(USER_LOW_PROGRESS_DB)
                        ||
                        getPersistentAttributes().containsKey(USER_MID_PROGRESS_DB)
                        ||
                        getPersistentAttributes().containsKey(USER_HIGH_PROGRESS_DB)) {
                    speechText = buildRoyalGreeting();

                    logger.info("Existing user was started new Game Session. Start Royal Greeting");

                }
                else {
                    speechText = phraseManager.getValueByKey(WELCOME_PHRASE);

                    logger.info("New user was started new Game Session.");
                }

                return DialogItem.builder()
                        .withResponse(Speech.ofText(speechText))
                        .withReprompt(speechText)
                        .withCardTitle(cardManager.getValueByKey(WELCOME_CARD))
                        .build();
            }
        };
    }
}
