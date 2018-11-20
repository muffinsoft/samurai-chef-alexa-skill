package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.constants.GreetingsConstants;
import com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants;
import com.muffinsoft.alexa.skills.samuraichef.content.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.content.GreetingsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Intents;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

import static com.muffinsoft.alexa.sdk.model.Speech.ofAlexa;
import static com.muffinsoft.alexa.skills.samuraichef.constants.CardConstants.WELCOME_CARD;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_HIGH_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_LOW_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_MID_PROGRESS_DB;

public class LaunchStateManager extends BaseStateManager {

    private static final Logger logger = LogManager.getLogger(LaunchStateManager.class);
    private final GreetingsManager greetingsManager;
    private final PhraseManager phraseManager;
    private final CardManager cardManager;

    public LaunchStateManager(Map<String, Slot> inputSlots, AttributesManager attributesManager, GreetingsManager greetingsManager, ConfigContainer configContainer) {
        super(inputSlots, attributesManager);
        this.phraseManager = configContainer.getPhraseManager();
        this.greetingsManager = greetingsManager;
        this.cardManager = configContainer.getCardManager();
    }

    private DialogItem.Builder buildRoyalGreeting(DialogItem.Builder builder) {

        List<PhraseSettings> dialog;

        if (true) {
            dialog = greetingsManager.getValueByKey(GreetingsConstants.PLAYER_WITH_BELTS_GREETING);
        }
        else {
            dialog = greetingsManager.getValueByKey(GreetingsConstants.PLAYER_WITH_TITLE_GREETING);
        }

        for (PhraseSettings phraseSettings : dialog) {
            builder.addResponse(ofAlexa(phraseSettings.getContent()));
        }

        return builder;
    }

    private DialogItem.Builder buildInitialGreeting(DialogItem.Builder builder) {

        ObjectMapper objectMapper = new ObjectMapper();

        List dialog = greetingsManager.getValueByKey(GreetingsConstants.FIRST_TIME_GREETING);

        int userReplyBreakpointPosition = 0;
        for (Object rawPhraseSettings : dialog) {
            PhraseSettings phraseSettings = objectMapper.convertValue(rawPhraseSettings, PhraseSettings.class);
            if (phraseSettings.isUserResponse()) {
                this.getSessionAttributes().put(SessionConstants.USER_REPLY_BREAKPOINT, userReplyBreakpointPosition);
                break;
            }
            builder.addResponse(ofAlexa(phraseSettings.getContent()));
            userReplyBreakpointPosition++;
        }

        return builder;
    }

    @Override
    public DialogItem nextResponse() {

        DialogItem.Builder builder = DialogItem.builder();

        if (getPersistentAttributes().containsKey(USER_LOW_PROGRESS_DB)
                ||
                getPersistentAttributes().containsKey(USER_MID_PROGRESS_DB)
                ||
                getPersistentAttributes().containsKey(USER_HIGH_PROGRESS_DB)) {

            builder = buildRoyalGreeting(builder);

            getSessionAttributes().put(INTENT, Intents.GAME);

            logger.info("Existing user was started new Game Session. Start Royal Greeting");

        }
        else {
            builder = buildInitialGreeting(builder);

            getSessionAttributes().put(INTENT, Intents.INITIAL_GREETING);

            logger.info("New user was started new Game Session.");
        }

        return builder
                .withCardTitle(cardManager.getValueByKey(WELCOME_CARD))
                .build();
    }
}
