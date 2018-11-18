package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static com.muffinsoft.alexa.sdk.model.Speech.ofAudio;
import static com.muffinsoft.alexa.sdk.model.Speech.ofText;
import static com.muffinsoft.alexa.skills.samuraichef.constants.CardConstants.WELCOME_CARD;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WELCOME_BACK_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WELCOME_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_HIGH_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_LOW_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_MID_PROGRESS_DB;

public class LaunchStateManager extends BaseStateManager {

    private static final Logger logger = LogManager.getLogger(LaunchStateManager.class);
    private final PhraseManager phraseManager;
    private final CardManager cardManager;

    public LaunchStateManager(Map<String, Slot> inputSlots, AttributesManager attributesManager, ConfigContainer configContainer) {
        super(inputSlots, attributesManager);
        this.phraseManager = configContainer.getPhraseManager();
        this.cardManager = configContainer.getCardManager();
    }

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
                .addResponse(ofText(speechText))
                .withReprompt(ofText(speechText))
                .withCardTitle(cardManager.getValueByKey(WELCOME_CARD))
                .build();
    }
}
