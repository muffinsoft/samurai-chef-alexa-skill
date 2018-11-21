package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.constants.GreetingsConstants;
import com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants;
import com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants;
import com.muffinsoft.alexa.skills.samuraichef.content.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.content.GreetingsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Intents;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseSettings;
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.muffinsoft.alexa.sdk.model.Speech.ofAlexa;
import static com.muffinsoft.alexa.skills.samuraichef.components.VoiceTranslator.translate;
import static com.muffinsoft.alexa.skills.samuraichef.constants.CardConstants.WELCOME_CARD;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.FINISHED_MISSIONS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STAR_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_HIGH_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_LOW_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_MID_PROGRESS_DB;

public class LaunchStateManager extends BaseStateManager {

    private static final Logger logger = LogManager.getLogger(LaunchStateManager.class);
    private final GreetingsManager greetingsManager;
    private final PhraseManager phraseManager;
    private final CardManager cardManager;
    private final AttributesManager attributesManager;
    private int starCount;
    private Set<String> finishedMissions;

    public LaunchStateManager(Map<String, Slot> inputSlots, AttributesManager attributesManager, GreetingsManager greetingsManager, ConfigContainer configContainer) {
        super(inputSlots, attributesManager);
        this.greetingsManager = greetingsManager;
        this.attributesManager = attributesManager;
        this.cardManager = configContainer.getCardManager();
        this.phraseManager = configContainer.getPhraseManager();
    }

    @Override
    protected void populateActivityVariables() {

        this.starCount = (int) getSessionAttributes().getOrDefault(STAR_COUNT, 0);

        //noinspection unchecked
        this.finishedMissions = (Set<String>) getSessionAttributes().getOrDefault(FINISHED_MISSIONS, new HashSet<>());

        logger.debug("Session attributes on the start of handling: " + this.getSessionAttributes().toString());
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

    private DialogItem.Builder buildRoyalGreeting(DialogItem.Builder builder) {

        if (starCount == 0) {
            builder = buildRoyalGreetingWithBelts(builder);
        }
        else {
            builder = buildRoyalGreetingWithTitles(builder);
        }

        return builder.addResponse(translate(phraseManager.getValueByKey(PhraseConstants.SELECT_MISSION_PHRASE)));
    }

    private DialogItem.Builder buildRoyalGreetingWithTitles(DialogItem.Builder builder) {

        List<PhraseSettings> dialog = greetingsManager.getValueByKey(GreetingsConstants.PLAYER_WITH_TITLE_GREETING);

        for (PhraseSettings phraseSettings : dialog) {
            String newContent = fillPlaceholder(phraseSettings.getContent());
            phraseSettings.setContent(newContent);
            builder.addResponse(translate(phraseSettings));
        }

        return builder;
    }

    private DialogItem.Builder buildRoyalGreetingWithBelts(DialogItem.Builder builder) {

        List<PhraseSettings> dialog = greetingsManager.getValueByKey(GreetingsConstants.PLAYER_WITH_BELTS_GREETING);

        for (PhraseSettings phraseSettings : dialog) {
            String newContent = fillPlaceholder(phraseSettings.getContent());
            phraseSettings.setContent(newContent);
            builder.addResponse(translate(phraseSettings));
        }

        return builder;
    }

    private String fillPlaceholder(String content) {
        content = content.replace("%titles%", getTitles());
        content = content.replace("%low_color%", getLowBeltColor());
        content = content.replace("%mid_color%", getMidBeltColor());
        content = content.replace("%high_color%", getHighBeltColor());
        return content;
    }

    private CharSequence getBeltColorByType(String dbType, String missionType) {
        if (!getPersistentAttributes().containsKey(dbType)) {
            return "white";
        }
        String jsonInString = String.valueOf(attributesManager.getPersistentAttributes().get(dbType));
        try {
            LinkedHashMap linkedHashMap = mapper.readValue(jsonInString, LinkedHashMap.class);
            UserProgress userProgress = mapper.convertValue(linkedHashMap, UserProgress.class);
            if (finishedMissions.contains(missionType)) {
                return "black";
            }
            else {
                switch (userProgress.getStripeCount()) {
                    case 0:
                        return "white";
                    case 1:
                        return "yellow";
                    case 2:
                        return "orange";
                    case 3:
                        return "green";
                    case 4:
                        return "purple";
                    default:
                        return "white";
                }
            }
        }
        catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }


    private CharSequence getHighBeltColor() {
        return getBeltColorByType(USER_HIGH_PROGRESS_DB, UserMission.HIGH_MISSION.name());
    }

    private CharSequence getMidBeltColor() {
        return getBeltColorByType(USER_MID_PROGRESS_DB, UserMission.MEDIUM_MISSION.name());
    }

    private CharSequence getLowBeltColor() {
        return getBeltColorByType(USER_LOW_PROGRESS_DB, UserMission.LOW_MISSION.name());
    }

    private CharSequence getTitles() {
        return "";
    }

    private DialogItem.Builder buildInitialGreeting(DialogItem.Builder builder) {

        List<PhraseSettings> dialog = greetingsManager.getValueByKey(GreetingsConstants.FIRST_TIME_GREETING);

        int userReplyBreakpointPosition = 0;

        for (PhraseSettings phraseSettings : dialog) {

            if (phraseSettings.isUserResponse()) {
                this.getSessionAttributes().put(SessionConstants.USER_REPLY_BREAKPOINT, userReplyBreakpointPosition + 1);
                break;
            }
            builder.addResponse(ofAlexa(phraseSettings.getContent()));
            userReplyBreakpointPosition++;
        }

        return builder;
    }
}
