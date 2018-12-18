package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.enums.IntentType;
import com.muffinsoft.alexa.sdk.enums.StateType;
import com.muffinsoft.alexa.sdk.model.BasePhraseContainer;
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
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
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

import static com.muffinsoft.alexa.sdk.enums.StateType.ACTIVITY_INTRO;
import static com.muffinsoft.alexa.sdk.enums.StateType.DEMO;
import static com.muffinsoft.alexa.sdk.enums.StateType.MISSION_INTRO;
import static com.muffinsoft.alexa.sdk.enums.StateType.READY;
import static com.muffinsoft.alexa.sdk.enums.StateType.SUBMISSION_INTRO;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.MISSION_ALREADY_COMPLETE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.REPEAT_LAST_PHRASE;
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
import static com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies.HIGH;
import static com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies.LOW;
import static com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies.MEDIUM;

public class SelectLevelStateManager extends BaseStateManager {

    private static final Logger logger = LogManager.getLogger(SelectLevelStateManager.class);

    private final AliasManager aliasManager;
    private final MissionManager missionManager;
    private final RegularPhraseManager regularPhraseManager;
    private final ActivityPhraseManager activityPhraseManager;
    private final MissionPhraseManager missionPhraseManager;
    private StateType statePhase;
    private Set<String> finishedMissions;
    private Integer userReplyBreakpointPosition;

    public SelectLevelStateManager(Map<String, Slot> slots, AttributesManager attributesManager, SettingsDependencyContainer settingsDependencyContainer, PhraseDependencyContainer phraseDependencyContainer) {
        super(slots, attributesManager, settingsDependencyContainer.getDialogTranslator());
        this.aliasManager = settingsDependencyContainer.getAliasManager();
        this.missionManager = settingsDependencyContainer.getMissionManager();
        this.regularPhraseManager = phraseDependencyContainer.getRegularPhraseManager();
        this.missionPhraseManager = phraseDependencyContainer.getMissionPhraseManager();
        this.activityPhraseManager = phraseDependencyContainer.getActivityPhraseManager();
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
        this.statePhase = StateType.valueOf(String.valueOf(getSessionAttributes().getOrDefault(STATE_PHASE, MISSION_INTRO)));
    }

    @Override
    public DialogItem nextResponse() {

        logger.debug("Starting handling user reply '" + this.getUserReply(SlotName.ACTION) + "' ...");

        DialogItem.Builder builder = DialogItem.builder();
        if (UserReplyComparator.compare(getUserReply(SlotName.MISSION), LOW)) {
            checkIfMissionAvailable(builder, UserMission.LOW_MISSION);
        }
        else if (UserReplyComparator.compare(getUserReply(SlotName.MISSION), MEDIUM)) {
            checkIfMissionAvailable(builder, UserMission.MEDIUM_MISSION);
        }
        else if (UserReplyComparator.compare(getUserReply(SlotName.MISSION), HIGH)) {
            checkIfMissionAvailable(builder, UserMission.HIGH_MISSION);
        }
        else {
            builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(REPEAT_LAST_PHRASE)));
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
            getSessionAttributes().put(INTENT, IntentType.RESET);
            builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(MISSION_ALREADY_COMPLETE_PHRASE)));
            builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(WANT_RESET_PROGRESS_PHRASE)));
            return;
        }

        this.getSessionAttributes().remove(ACTIVITY_PROGRESS);

        if (getUserProgressForMission(mission).getPreviousActivity() == null) {
            this.getSessionAttributes().remove(STATE_PHASE);
        }
        else {
            this.getSessionAttributes().put(STATE_PHASE, StateType.SUBMISSION_INTRO);
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

        logger.info("User progress " + userProgress);

        if (userProgress == null) {
            handleMissionIntroState(builder, mission, new UserProgress(mission, true));
        }
        else {
            handleStripeIntroState(builder, mission, userProgress);
        }
    }

    private void handleMissionIntroState(DialogItem.Builder builder, UserMission currentMission, UserProgress userProgress) {

        this.statePhase = StateType.SUBMISSION_INTRO;

        List<BasePhraseContainer> dialog = missionPhraseManager.getMissionIntro(currentMission);

        int iterationPointer = wrapAnyUserResponse(dialog, builder, MISSION_INTRO);

        if (iterationPointer >= dialog.size()) {
            handleStripeIntroState(builder, currentMission, userProgress);
        }
    }

    @SuppressWarnings("Duplicates")
    private void handleStripeIntroState(DialogItem.Builder builder, UserMission currentMission, UserProgress userProgress) {

        this.statePhase = ACTIVITY_INTRO;

        List<BasePhraseContainer> dialog = missionPhraseManager.getStripeIntroByMission(currentMission, userProgress.getStripeCount());

        int iterationPointer = wrapAnyUserResponse(dialog, builder, SUBMISSION_INTRO);

        if (iterationPointer >= dialog.size()) {
            handleActivityIntroState(builder, currentMission, userProgress);
        }
    }

    @SuppressWarnings("Duplicates")
    private void handleActivityIntroState(DialogItem.Builder builder, UserMission currentMission, UserProgress userProgress) {

        Activities activity;

        String currentActivity = userProgress.getCurrentActivity();
        if (currentActivity != null && !currentActivity.isEmpty()) {
            activity = Activities.valueOf(currentActivity);
        }
        else {
            activity = missionManager.getFirstActivityForMission(currentMission);
        }

        SpeechSettings speechSettings = activityPhraseManager.getSpeechForActivityByStripeNumberAtMission(activity, userProgress.getStripeCount(), currentMission);

        for (BasePhraseContainer partOfSpeech : speechSettings.getIntro()) {
            builder.addResponse(getDialogTranslator().translate(partOfSpeech));
        }

        if (speechSettings.isShouldRunDemo()) {

            logger.debug("Handling " + this.statePhase + ". Moving to " + DEMO);

            this.statePhase = DEMO;

            SpeechSettings demoSpeechSettings = activityPhraseManager.getSpeechForActivityByStripeNumberAtMission(activity, userProgress.getStripeCount(), currentMission);

            builder.addResponse(getDialogTranslator().translate(demoSpeechSettings.getShouldRunDemoPhrase()));
        }
        else {

            logger.debug("Handling " + this.statePhase + ". Moving to " + READY);

            this.statePhase = READY;
            appendReadyToStart(builder, userProgress, activity, currentMission);
        }
    }

    private void appendReadyToStart(DialogItem.Builder builder, UserProgress userProgress, Activities activity, UserMission currentMission) {

        SpeechSettings speechSettings = activityPhraseManager.getSpeechForActivityByStripeNumberAtMission(activity, userProgress.getStripeCount(), currentMission);

        builder.addResponse(getDialogTranslator().translate(speechSettings.getReadyToStartPhrase()));
    }

    @SuppressWarnings("Duplicates")
    private int wrapAnyUserResponse(List<BasePhraseContainer> dialog, DialogItem.Builder builder, StateType statePhase) {

        if (this.userReplyBreakpointPosition != null) {
            this.getSessionAttributes().remove(USER_REPLY_BREAKPOINT);
        }

        int index = 0;

        for (BasePhraseContainer BasePhraseContainer : dialog) {

            index++;

            if (this.userReplyBreakpointPosition != null && index <= this.userReplyBreakpointPosition) {
                continue;
            }

            if (BasePhraseContainer.isUserResponse()) {
                this.getSessionAttributes().put(SessionConstants.USER_REPLY_BREAKPOINT, index);
                this.statePhase = statePhase;
                break;
            }
            builder.addResponse(getDialogTranslator().translate(BasePhraseContainer));
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
                break;
            case MEDIUM_MISSION:
                if (this.getPersistentAttributes().containsKey(USER_MID_PROGRESS_DB)) {
                    jsonInString = String.valueOf(this.getPersistentAttributes().get(USER_MID_PROGRESS_DB));
                }
                break;
            case HIGH_MISSION:
                if (this.getPersistentAttributes().containsKey(USER_HIGH_PROGRESS_DB)) {
                    jsonInString = String.valueOf(this.getPersistentAttributes().get(USER_HIGH_PROGRESS_DB));
                }
                break;
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
