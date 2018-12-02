package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.SlotName;
import com.muffinsoft.alexa.skills.samuraichef.components.UserReplyComparator;
import com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.ActivityPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.MissionPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.RegularPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.AliasManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.MissionManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.Intents;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseSettings;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SpeechSettings;
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

import static com.muffinsoft.alexa.skills.samuraichef.components.VoiceTranslator.translate;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.MISSION_ALREADY_COMPLETE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.SELECT_MISSION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.WANT_RESET_PROGRESS_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.FINISHED_MISSIONS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_HIGH_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_LOW_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_MID_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_REPLY_BREAKPOINT;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.ACTIVITY_INTRO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.DEMO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.MISSION_INTRO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.READY_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.STRIPE_INTRO;

public class SelectLevelStateManager extends BaseStateManager {

    private static final Logger logger = LogManager.getLogger(SelectLevelStateManager.class);

    private final String userFoodSlotReply;

    private final AliasManager aliasManager;
    private final MissionManager missionManager;
    private final RegularPhraseManager regularPhraseManager;
    private final ActivityPhraseManager activityPhraseManager;
    private final MissionPhraseManager missionPhraseManager;
    private StatePhase statePhase;
    private Set<String> finishedMissions;
    private Integer userReplyBreakpointPosition;

    public SelectLevelStateManager(Map<String, Slot> slots, AttributesManager attributesManager, SettingsDependencyContainer settingsDependencyContainer, PhraseDependencyContainer phraseDependencyContainer) {
        super(slots, attributesManager);
        this.aliasManager = settingsDependencyContainer.getAliasManager();
        this.missionManager = settingsDependencyContainer.getMissionManager();
        this.regularPhraseManager = phraseDependencyContainer.getRegularPhraseManager();
        this.missionPhraseManager = phraseDependencyContainer.getMissionPhraseManager();
        this.activityPhraseManager = phraseDependencyContainer.getActivityPhraseManager();
        String foodSlotName = SlotName.AMAZON_FOOD.text;
        this.userFoodSlotReply = slots != null ? (slots.containsKey(foodSlotName) ? slots.get(foodSlotName).getValue() : null) : null;
    }

    @Override
    public String getUserReply() {
        String userReply = super.getUserReply();
        if (userReply != null && !userReply.isEmpty()) {
            return userReply;
        }
        else {
            return this.userFoodSlotReply;
        }
    }

    @Override
    protected void updateSessionAttributes() {

        getSessionAttributes().put(STATE_PHASE, this.statePhase);

        logger.debug("Session attributes on the end of handling: " + this.getSessionAttributes().toString());
    }

    @Override
    protected void populateActivityVariables() {
        this.userReplyBreakpointPosition = (Integer) this.getSessionAttributes().getOrDefault(USER_REPLY_BREAKPOINT, null);
        @SuppressWarnings("unchecked") List<String> finishedMissionArray = (List<String>) getSessionAttributes().getOrDefault(FINISHED_MISSIONS, new ArrayList<String>());
        this.finishedMissions = new HashSet<>(finishedMissionArray);
        this.statePhase = StatePhase.valueOf(String.valueOf(getSessionAttributes().getOrDefault(STATE_PHASE, MISSION_INTRO)));
    }

    @Override
    public DialogItem nextResponse() {

        logger.debug("Starting handling user reply '" + this.getUserReply() + "' ...");

        DialogItem.Builder builder = DialogItem.builder();
        if (UserReplyComparator.compare(getUserReply(), UserReplies.LOW)) {
            checkIfMissionAvailable(builder, UserMission.LOW_MISSION);
        }
        else if (UserReplyComparator.compare(getUserReply(), UserReplies.MEDIUM)) {
            checkIfMissionAvailable(builder, UserMission.MEDIUM_MISSION);
        }
        else if (UserReplyComparator.compare(getUserReply(), UserReplies.HIGH)) {
            checkIfMissionAvailable(builder, UserMission.HIGH_MISSION);
        }
        else {
            builder.addResponse(translate(regularPhraseManager.getValueByKey(SELECT_MISSION_PHRASE)));
        }

        if (this.getSessionAttributes().containsKey(CURRENT_MISSION)) {
            String cardTitle = aliasManager.getValueByKey(String.valueOf(this.getSessionAttributes().get(CURRENT_MISSION)));
            builder.withCardTitle(cardTitle);
        }

        return builder.build();
    }

    private void checkIfMissionAvailable(DialogItem.Builder builder, UserMission mission) {

        this.getSessionAttributes().put(CURRENT_MISSION, mission);

        if (finishedMissions.contains(mission.name())) {
            getSessionAttributes().put(INTENT, Intents.RESET_CONFIRMATION);
            builder.addResponse(translate(regularPhraseManager.getValueByKey(MISSION_ALREADY_COMPLETE_PHRASE)));
            builder.addResponse(translate(regularPhraseManager.getValueByKey(WANT_RESET_PROGRESS_PHRASE)));
            return;
        }

        this.getSessionAttributes().remove(ACTIVITY_PROGRESS);

        if (getUserProgressForMission(mission).getPreviousActivity() == null) {
            this.getSessionAttributes().remove(STATE_PHASE);
        }
        else {
            this.getSessionAttributes().put(STATE_PHASE, StatePhase.STRIPE_INTRO);
        }
        logger.info("user will be redirected to " + mission.name());

        appendIntro(builder, mission);
    }

    private void appendIntro(DialogItem.Builder builder, UserMission mission) {

        UserProgress userProgress = null;

        try {
            userProgress = getProgressInMission(mission);
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        if (userProgress == null) {
            handleMissionIntroState(builder, mission, new UserProgress(mission, true));
        }
        else {
            handleStripeIntroState(builder, userProgress, mission, userProgress.getStripeCount());
        }
    }

    private void handleMissionIntroState(DialogItem.Builder builder, UserMission currentMission, UserProgress userProgress) {

        this.statePhase = STRIPE_INTRO;

        List<PhraseSettings> dialog = missionPhraseManager.getMissionIntro(currentMission);

        int iterationPointer = wrapAnyUserResponse(dialog, builder, MISSION_INTRO);

        if (iterationPointer >= dialog.size()) {
            builder = handleStripeIntroState(builder, userProgress, currentMission, userProgress.getStripeCount());
        }

        builder.withSlotName(actionSlotName);
    }

    @SuppressWarnings("Duplicates")
    private DialogItem.Builder handleStripeIntroState(DialogItem.Builder builder, UserProgress userProgress, UserMission currentMission, int number) {

        this.statePhase = ACTIVITY_INTRO;

        Activities currentActivity = missionManager.getFirstActivityForMission(currentMission);

        List<PhraseSettings> dialog = missionPhraseManager.getStripeIntroByMission(currentMission, number);

        int iterationPointer = wrapAnyUserResponse(dialog, builder, STRIPE_INTRO);

        if (iterationPointer >= dialog.size()) {
            builder = handleActivityIntroState(builder, userProgress, currentActivity, currentMission, number);
        }

        return builder.withSlotName(actionSlotName);
    }

    @SuppressWarnings("Duplicates")
    private DialogItem.Builder handleActivityIntroState(DialogItem.Builder builder, UserProgress userProgress, Activities activity, UserMission currentMission, int number) {

        SpeechSettings speechSettings = activityPhraseManager.getSpeechForActivityByStripeNumberAtMission(activity, number, currentMission);

        for (PhraseSettings partOfSpeech : speechSettings.getIntro()) {
            builder.addResponse(translate(partOfSpeech));
        }

        if (speechSettings.isShouldRunDemo()) {

            logger.debug("Handling " + this.statePhase + ". Moving to " + DEMO);

            this.statePhase = DEMO;

            SpeechSettings demoSpeechSettings = activityPhraseManager.getSpeechForActivityByStripeNumberAtMission(activity, userProgress.getStripeCount(), currentMission);

            builder.addResponse(translate(demoSpeechSettings.getShouldRunDemoPhrase()));
        }
        else {

            logger.debug("Handling " + this.statePhase + ". Moving to " + READY_PHASE);

            this.statePhase = READY_PHASE;
            appendReadyToStart(builder, userProgress, activity, currentMission);
        }

        return builder.withSlotName(actionSlotName);
    }

    private void appendReadyToStart(DialogItem.Builder builder, UserProgress userProgress, Activities activity, UserMission currentMission) {

        SpeechSettings speechSettings = activityPhraseManager.getSpeechForActivityByStripeNumberAtMission(activity, userProgress.getStripeCount(), currentMission);

        builder.addResponse(translate(speechSettings.getReadyToStartPhrase()));
    }

    @SuppressWarnings("Duplicates")
    private int wrapAnyUserResponse(List<PhraseSettings> dialog, DialogItem.Builder builder, StatePhase statePhase) {

        if (this.userReplyBreakpointPosition != null) {
            this.getSessionAttributes().remove(USER_REPLY_BREAKPOINT);
        }

        int index = 0;

        for (PhraseSettings phraseSettings : dialog) {

            index++;

            if (this.userReplyBreakpointPosition != null && index <= this.userReplyBreakpointPosition) {
                continue;
            }

            if (phraseSettings.isUserResponse()) {
                this.getSessionAttributes().put(SessionConstants.USER_REPLY_BREAKPOINT, index);
                this.statePhase = statePhase;
                break;
            }
            builder.addResponse(translate(phraseSettings));
        }
        return index;
    }

    private UserProgress getProgressInMission(UserMission mission) throws IOException {
        String jsonInString = null;
        switch (mission) {
            case LOW_MISSION:
                if (this.getPersistentAttributes().containsKey(USER_LOW_PROGRESS_DB)) {
                    jsonInString = String.valueOf(this.getPersistentAttributes().get(USER_LOW_PROGRESS_DB));
                }
            case MEDIUM_MISSION:
                if (this.getPersistentAttributes().containsKey(USER_MID_PROGRESS_DB)) {
                    jsonInString = String.valueOf(this.getPersistentAttributes().get(USER_MID_PROGRESS_DB));
                }
            case HIGH_MISSION:
                if (this.getPersistentAttributes().containsKey(USER_HIGH_PROGRESS_DB)) {
                    jsonInString = String.valueOf(this.getPersistentAttributes().get(USER_HIGH_PROGRESS_DB));
                }
        }
        if (jsonInString != null) {
            LinkedHashMap linkedHashMap = mapper.readValue(jsonInString, LinkedHashMap.class);
            return mapper.convertValue(linkedHashMap, UserProgress.class);
        }
        return null;
    }

    private UserProgress getUserProgressForMission(UserMission mission) {
        switch (mission) {
            case LOW_MISSION:
                return getUserProgressForMission(USER_LOW_PROGRESS_DB, UserMission.LOW_MISSION);
            case MEDIUM_MISSION:
                return getUserProgressForMission(USER_MID_PROGRESS_DB, UserMission.MEDIUM_MISSION);
            case HIGH_MISSION:
                return getUserProgressForMission(USER_HIGH_PROGRESS_DB, UserMission.HIGH_MISSION);
            default:
                throw new IllegalArgumentException("Can't handle User Progress for Mission " + mission);
        }
    }

    private UserProgress getUserProgressForMission(String value, UserMission mission) {

        String jsonInString = String.valueOf(getPersistentAttributes().get(value));

        try {
            LinkedHashMap rawUserProgress = new ObjectMapper().readValue(jsonInString, LinkedHashMap.class);
            return rawUserProgress != null ? mapper.convertValue(rawUserProgress, UserProgress.class) : new UserProgress(mission, true);
        }
        catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
