package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.constants.AliasConstants;
import com.muffinsoft.alexa.skills.samuraichef.constants.GreetingsPhraseConstants;
import com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants;
import com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.GreetingsPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.RegularPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.AliasManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Intents;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseSettings;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
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
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.MISSION_START_STATE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_HIGH_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_LOW_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_MID_PROGRESS_DB;

public class LaunchStateManager extends BaseStateManager {

    private static final Logger logger = LogManager.getLogger(LaunchStateManager.class);
    private final GreetingsPhraseManager greetingsPhraseManager;
    private final AliasManager aliasManager;
    private final RegularPhraseManager regularPhraseManager;
    private final CardManager cardManager;
    private final AttributesManager attributesManager;
    private Set<String> finishedMissions;

    public LaunchStateManager(Map<String, Slot> inputSlots, AttributesManager attributesManager, SettingsDependencyContainer settingsDependencyContainer, PhraseDependencyContainer phraseDependencyContainer) {
        super(inputSlots, attributesManager);
        this.greetingsPhraseManager = phraseDependencyContainer.getGreetingsPhraseManager();
        this.attributesManager = attributesManager;
        this.cardManager = settingsDependencyContainer.getCardManager();
        this.regularPhraseManager = phraseDependencyContainer.getRegularPhraseManager();
        this.aliasManager = settingsDependencyContainer.getAliasManager();
    }

    @Override
    protected void populateActivityVariables() {

        List<String> finishedMissionArray = (List<String>) getSessionAttributes().getOrDefault(FINISHED_MISSIONS, new ArrayList<String>());
        this.finishedMissions = new HashSet<>(finishedMissionArray);

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

        UserProgress lowUserProgress = getUserProgress(USER_LOW_PROGRESS_DB);
        UserProgress midUserProgress = getUserProgress(USER_MID_PROGRESS_DB);
        UserProgress highUserProgress = getUserProgress(USER_HIGH_PROGRESS_DB);

        this.getSessionAttributes().put(MISSION_START_STATE, true);

        builder = buildRoyalGreetingWithAwards(builder, lowUserProgress, midUserProgress, highUserProgress);

        return builder.addResponse(translate(regularPhraseManager.getValueByKey(RegularPhraseConstants.SELECT_MISSION_PHRASE)));
    }

    private DialogItem.Builder buildRoyalGreetingWithAwards(DialogItem.Builder builder, UserProgress lowUserProgress, UserProgress midUserProgress, UserProgress highUserProgress) {

        List<PhraseSettings> dialog = greetingsPhraseManager.getValueByKey(GreetingsPhraseConstants.PLAYER_WITH_AWARDS_GREETING);

        for (PhraseSettings phraseSettings : dialog) {
            String newContent = fillPlaceholder(phraseSettings.getContent(), lowUserProgress, midUserProgress, highUserProgress);
            phraseSettings.setContent(newContent);
            builder.addResponse(translate(phraseSettings));
        }

        return builder;
    }

    private String fillPlaceholder(String content, UserProgress lowUserProgress, UserProgress midUserProgress, UserProgress highUserProgress) {
        content = content.replace("%titles%", getTitles(lowUserProgress, midUserProgress, highUserProgress));
        content = content.replace("%low_color%", getBeltColor(lowUserProgress));
        content = content.replace("%mid_color%", getBeltColor(midUserProgress));
        content = content.replace("%high_color%", getBeltColor(highUserProgress));
        return content;
    }

    private CharSequence getBeltColor(UserProgress userProgress) {

        if (userProgress == null) {
            return "white";
        }

        if (finishedMissions.contains(userProgress.getMission())) {
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

    private UserProgress getUserProgress(String dbType) {
        if (!getPersistentAttributes().containsKey(dbType)) {
            return null;
        }
        else {
            String jsonInString = String.valueOf(attributesManager.getPersistentAttributes().get(dbType));
            try {
                LinkedHashMap linkedHashMap = mapper.readValue(jsonInString, LinkedHashMap.class);
                return mapper.convertValue(linkedHashMap, UserProgress.class);
            }
            catch (IOException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }

    private CharSequence getTitles(UserProgress lowUserProgress, UserProgress midUserProgress, UserProgress highUserProgress) {
        String lowTitle = getHighestTitleOfMission(lowUserProgress);
        String midTitle = getHighestTitleOfMission(midUserProgress);
        String highTitle = getHighestTitleOfMission(highUserProgress);
        return lowTitle + midTitle + highTitle;
    }

    private String getHighestTitleOfMission(UserProgress userProgress) {
        if (userProgress == null) {
            return "";
        }
        if (userProgress.isPerfectMission()) {
            if (userProgress.getMission().equals(UserMission.LOW_MISSION.name())) {
                return aliasManager.getValueByKey(AliasConstants.LOW_MISSION_PERFECT_MISSION);
            }
            else if (userProgress.getMission().equals(UserMission.MEDIUM_MISSION.name())) {
                return aliasManager.getValueByKey(AliasConstants.MID_MISSION_PERFECT_MISSION);
            }
            else {
                return aliasManager.getValueByKey(AliasConstants.HIGH_MISSION_PERFECT_MISSION);
            }
        }
        else if (userProgress.isPerfectStripe()) {
            if (userProgress.getMission().equals(UserMission.LOW_MISSION.name())) {
                return aliasManager.getValueByKey(AliasConstants.LOW_MISSION_PERFECT_STRIPE);
            }
            else if (userProgress.getMission().equals(UserMission.MEDIUM_MISSION.name())) {
                return aliasManager.getValueByKey(AliasConstants.MID_MISSION_PERFECT_STRIPE);
            }
            else {
                return aliasManager.getValueByKey(AliasConstants.HIGH_MISSION_PERFECT_STRIPE);
            }
        }
        else if (userProgress.isPerfectActivity()) {
            if (userProgress.getMission().equals(UserMission.LOW_MISSION.name())) {
                return aliasManager.getValueByKey(AliasConstants.LOW_MISSION_PERFECT_ACTIVITY);
            }
            else if (userProgress.getMission().equals(UserMission.MEDIUM_MISSION.name())) {
                return aliasManager.getValueByKey(AliasConstants.MID_MISSION_PERFECT_ACTIVITY);
            }
            else {
                return aliasManager.getValueByKey(AliasConstants.HIGH_MISSION_PERFECT_ACTIVITY);
            }
        }
        else {
            return "";
        }
    }

    private DialogItem.Builder buildInitialGreeting(DialogItem.Builder builder) {

        List<PhraseSettings> dialog = greetingsPhraseManager.getValueByKey(GreetingsPhraseConstants.FIRST_TIME_GREETING);

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
