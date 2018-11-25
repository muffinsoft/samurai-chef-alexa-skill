package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.SlotName;
import com.muffinsoft.alexa.skills.samuraichef.components.UserReplyComparator;
import com.muffinsoft.alexa.skills.samuraichef.content.AliasManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Intents;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseSettings;
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.muffinsoft.alexa.skills.samuraichef.components.VoiceTranslator.translate;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.MISSION_ALREADY_COMPLETE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.READY_TO_CONTINUE_MISSION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.READY_TO_START_MISSION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.SELECT_MISSION_UNKNOWN_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WANT_RESET_PROGRESS_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.FINISHED_MISSIONS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_HIGH_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_LOW_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_MID_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_PROGRESS;

public class SelectLevelStateManager extends BaseStateManager {

    private static final Logger logger = LogManager.getLogger(SelectLevelStateManager.class);

    private final String userFoodSlotReply;

    private final AliasManager aliasManager;
    private final PhraseManager phraseManager;
    //    private UserProgress userProgress;
    private Set<String> finishedMissions;

    public SelectLevelStateManager(Map<String, Slot> slots, AttributesManager attributesManager, ConfigContainer configContainer) {
        super(slots, attributesManager);
        this.aliasManager = configContainer.getAliasManager();
        this.phraseManager = configContainer.getPhraseManager();
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
    protected void populateActivityVariables() {
//        LinkedHashMap rawUserProgress = (LinkedHashMap) getSessionAttributes().get(USER_PROGRESS);
//        this.userProgress = rawUserProgress != null ? mapper.convertValue(rawUserProgress, UserProgress.class) : new UserProgress(true);

        List<String> finishedMissionArray = (List<String>) getSessionAttributes().getOrDefault(FINISHED_MISSIONS, new ArrayList<String>());
        this.finishedMissions = new HashSet<>(finishedMissionArray);
    }

    @Override
    public DialogItem nextResponse() {

        logger.debug("Starting handling user reply '" + this.getUserReply() + "' ...");

        DialogItem.Builder builder = DialogItem.builder();
//        if (UserReplyComparator.compare(getUserReply(), UserReplies.YES)) {
//            builder.addResponse(translate(phraseManager.getValueByKey(SELECT_MISSION_PHRASE)));
//        }
        if (UserReplyComparator.compare(getUserReply(), UserReplies.LOW)) {
            builder.addResponse(translate(checkIfMissionAvailable(UserMission.LOW_MISSION)));
        }
        else if (UserReplyComparator.compare(getUserReply(), UserReplies.MEDIUM)) {
            builder.addResponse(translate(checkIfMissionAvailable(UserMission.MEDIUM_MISSION)));
        }
        else if (UserReplyComparator.compare(getUserReply(), UserReplies.HIGH)) {
            builder.addResponse(translate(checkIfMissionAvailable(UserMission.HIGH_MISSION)));
        }
        else {
            builder.addResponse(translate(phraseManager.getValueByKey(SELECT_MISSION_UNKNOWN_PHRASE)));
        }

        if (this.getSessionAttributes().containsKey(CURRENT_MISSION)) {
            String cardTitle = aliasManager.getValueByKey(String.valueOf(this.getSessionAttributes().get(CURRENT_MISSION)));
            builder.withCardTitle(cardTitle);
        }

        return builder.build();
    }

    private List<PhraseSettings> checkIfMissionAvailable(UserMission mission) {

        this.getSessionAttributes().put(CURRENT_MISSION, mission);

        if (finishedMissions.contains(mission.name())) {
            getSessionAttributes().put(INTENT, Intents.RESET_CONFIRMATION);
            return Arrays.asList(phraseManager.getValueByKey(MISSION_ALREADY_COMPLETE_PHRASE), phraseManager.getValueByKey(WANT_RESET_PROGRESS_PHRASE));
        }

        this.getSessionAttributes().remove(ACTIVITY_PROGRESS);
        this.getSessionAttributes().remove(USER_PROGRESS);

        if (getUserProgressForMission(mission).getPreviousActivity() == null) {
            this.getSessionAttributes().remove(STATE_PHASE);
        }
        else {
            this.getSessionAttributes().put(STATE_PHASE, StatePhase.STRIPE_INTRO);
        }
        logger.info("user will be redirected to " + mission.name());

        return startOrContinuePhrase(mission);

    }

    private List<PhraseSettings> startOrContinuePhrase(UserMission mission) {
        PhraseSettings phraseSettings;
        if (hasProgressInMission(mission)) {
            phraseSettings = phraseManager.getValueByKey(READY_TO_CONTINUE_MISSION_PHRASE);
        }
        else {
            phraseSettings = phraseManager.getValueByKey(READY_TO_START_MISSION_PHRASE);
        }

        phraseSettings.setContent(phraseSettings.getContent() + " " + aliasManager.getValueByKey(mission.name()) + "?");

        return Collections.singletonList(phraseSettings);
    }

    private boolean hasProgressInMission(UserMission mission) {
        switch (mission) {
            case LOW_MISSION:
                return this.getPersistentAttributes().containsKey(USER_LOW_PROGRESS_DB);
            case MEDIUM_MISSION:
                return this.getPersistentAttributes().containsKey(USER_MID_PROGRESS_DB);
            case HIGH_MISSION:
                return this.getPersistentAttributes().containsKey(USER_HIGH_PROGRESS_DB);
        }
        return false;
    }

    private UserProgress getUserProgressForMission(UserMission mission) {
        switch (mission) {
            case LOW_MISSION:
                return getUserProgressForMission(USER_LOW_PROGRESS_DB);
            case MEDIUM_MISSION:
                return getUserProgressForMission(USER_MID_PROGRESS_DB);
            case HIGH_MISSION:
                return getUserProgressForMission(USER_HIGH_PROGRESS_DB);
            default:
                throw new IllegalArgumentException("Can't handle User Progress for Mission " + mission);
        }
    }

    private UserProgress getUserProgressForMission(String value) {

        String jsonInString = String.valueOf(getPersistentAttributes().get(value));

        try {
            LinkedHashMap rawUserProgress = new ObjectMapper().readValue(jsonInString, LinkedHashMap.class);
            return rawUserProgress != null ? mapper.convertValue(rawUserProgress, UserProgress.class) : new UserProgress(true);
        }
        catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
