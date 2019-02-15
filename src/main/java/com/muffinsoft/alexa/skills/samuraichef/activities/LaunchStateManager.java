package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.enums.IntentType;
import com.muffinsoft.alexa.sdk.model.BasePhraseContainer;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.constants.AliasConstants;
import com.muffinsoft.alexa.skills.samuraichef.constants.GreetingsPhraseConstants;
import com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants;
import com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.GreetingsPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.RegularPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.AliasManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.AplManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.muffinsoft.alexa.skills.samuraichef.components.BeltColorDefiner.defineColor;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.FINISHED_MISSIONS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_HIGH_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_LOW_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_MID_PROGRESS_DB;

public class LaunchStateManager extends BaseStateManager {

    private static final Logger logger = LogManager.getLogger(LaunchStateManager.class);
    private final GreetingsPhraseManager greetingsPhraseManager;
    private final AliasManager aliasManager;
    private final AplManager aplManager;
    private final RegularPhraseManager regularPhraseManager;
    private final CardManager cardManager;
    private final AttributesManager attributesManager;
    private Set<String> finishedMissions;

    public LaunchStateManager(Map<String, Slot> inputSlots, AttributesManager attributesManager, SettingsDependencyContainer settingsDependencyContainer, PhraseDependencyContainer phraseDependencyContainer) {
        super(inputSlots, attributesManager, settingsDependencyContainer.getDialogTranslator());
        this.greetingsPhraseManager = phraseDependencyContainer.getGreetingsPhraseManager();
        this.attributesManager = attributesManager;
        this.cardManager = settingsDependencyContainer.getCardManager();
        this.regularPhraseManager = phraseDependencyContainer.getRegularPhraseManager();
        this.aliasManager = settingsDependencyContainer.getAliasManager();
        this.aplManager = settingsDependencyContainer.getAplManager();
    }

    @Override
    protected void populateActivityVariables() {

        @SuppressWarnings("unchecked") List<String> finishedMissionArray = (List<String>) getSessionAttributes().getOrDefault(FINISHED_MISSIONS, new ArrayList<String>());
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

            getSessionAttributes().put(INTENT, IntentType.GAME);

            logger.info("Existing user was started new Game Session. Start Royal Greeting");

        }
        else {
            buildInitialGreeting(builder);

            getSessionAttributes().put(INTENT, IntentType.INITIAL_GREETING);

            logger.info("New user was started new Game Session.");
        }

        return builder.build();
    }

    private DialogItem.Builder buildRoyalGreeting(DialogItem.Builder builder) {

        UserProgress lowUserProgress = getUserProgress(USER_LOW_PROGRESS_DB);
        UserProgress midUserProgress = getUserProgress(USER_MID_PROGRESS_DB);
        UserProgress highUserProgress = getUserProgress(USER_HIGH_PROGRESS_DB);

        buildRoyalGreetingWithAwards(builder, lowUserProgress, midUserProgress, highUserProgress);

        return builder
                .addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(RegularPhraseConstants.SELECT_MISSION_PHRASE)))
                .withCardTitle("Mission Selection")
                .withAplDocument(aplManager.getContainer())
                .withSmallImageUrl(cardManager.getValueByKey("mission-selection-small"))
                .withLargeImageUrl(cardManager.getValueByKey("mission-selection-large"));
    }

    private void buildRoyalGreetingWithAwards(DialogItem.Builder builder, UserProgress lowUserProgress, UserProgress midUserProgress, UserProgress highUserProgress) {

        List<BasePhraseContainer> dialog = greetingsPhraseManager.getValueByKey(GreetingsPhraseConstants.PLAYER_WITH_AWARDS_GREETING);

        for (BasePhraseContainer BasePhraseContainer : dialog) {
            String newContent = fillPlaceholder(BasePhraseContainer.getContent(), lowUserProgress, midUserProgress, highUserProgress);
            BasePhraseContainer.setContent(newContent);
            builder.addResponse(getDialogTranslator().translate(BasePhraseContainer));
        }
    }

    private String fillPlaceholder(String content, UserProgress lowUserProgress, UserProgress midUserProgress, UserProgress highUserProgress) {
        if (content == null) {
            return null;
        }
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
            return defineColor(userProgress.getStripeCount());
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
        return join(Arrays.asList(lowTitle, midTitle, highTitle), ", ");
    }

    private String join(List<String> str, String separator) {
        StringBuilder retval = new StringBuilder();
        for (String s : str) {
            retval.append(separator).append(s);
        }
        return retval.toString().replaceFirst(separator, "");
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

    private void buildInitialGreeting(DialogItem.Builder builder) {

        List<BasePhraseContainer> dialog = greetingsPhraseManager.getValueByKey(GreetingsPhraseConstants.FIRST_TIME_GREETING);

        int userReplyBreakpointPosition = 0;

        for (BasePhraseContainer BasePhraseContainer : dialog) {

            if (BasePhraseContainer.isUserResponse()) {
                this.getSessionAttributes().put(SessionConstants.USER_REPLY_BREAKPOINT, userReplyBreakpointPosition + 1);
                break;
            }
            builder.addResponse(getDialogTranslator().translate(BasePhraseContainer));
            userReplyBreakpointPosition++;
        }
        builder.withCardTitle("Welcome")
                .withAplDocument(aplManager.getContainer())
                .withSmallImageUrl(cardManager.getValueByKey("welcome-small"))
                .withLargeImageUrl(cardManager.getValueByKey("welcome-large"));
    }
}
