package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.Speech;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.models.ActivityProgress;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.HELP_COMPETITION_RULES_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.HELP_GENERAL_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.HELP_MISSION_HIGH_DESCRIPTION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.HELP_MISSION_LOW_DESCRIPTION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.HELP_MISSION_MID_DESCRIPTION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.HELP_NEW_WORDS_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.MISSION_INTRO;

public class HelpStateManager extends BaseStateManager {

    private static final Logger logger = LogManager.getLogger(HelpStateManager.class);

    private final PhraseManager phraseManager;

    private ActivityProgress activityProgress;
    private StatePhase statePhase = null;
    private UserProgress userProgress;
    private UserMission currentMission = null;
    private Activities currentActivity = null;

    public HelpStateManager(Map<String, Slot> inputSlots, AttributesManager attributesManager, ConfigContainer configContainer) {
        super(inputSlots, attributesManager);
        this.phraseManager = configContainer.getPhraseManager();
    }

    @Override
    protected void populateActivityVariables() {

        String stringifyMission = String.valueOf(getSessionAttributes().get(CURRENT_MISSION));
        if (stringifyMission != null) {
            currentMission = UserMission.valueOf(stringifyMission);
        }

        String stringifyActivity = String.valueOf(getSessionAttributes().get(ACTIVITY));
        if (stringifyActivity != null) {
            currentActivity = Activities.valueOf(stringifyActivity);
        }

        String stringifyState = String.valueOf(getSessionAttributes().getOrDefault(STATE_PHASE, MISSION_INTRO));
        if (stringifyState != null) {
            statePhase = StatePhase.valueOf(stringifyState);
        }

        LinkedHashMap rawUserProgress = (LinkedHashMap) getSessionAttributes().get(USER_PROGRESS);
        userProgress = rawUserProgress != null ? mapper.convertValue(rawUserProgress, UserProgress.class) : new UserProgress(this.currentMission, true);

        LinkedHashMap rawActivityProgress = (LinkedHashMap) getSessionAttributes().get(ACTIVITY_PROGRESS);
        activityProgress = rawActivityProgress != null ? mapper.convertValue(rawActivityProgress, ActivityProgress.class) : new ActivityProgress();

        logger.debug("Session attributes on the start of handling: " + this.getSessionAttributes().toString());
    }

    @Override
    public DialogItem nextResponse() {

        logger.debug("Help was evoked ...");

        String dialog;

        if (this.currentActivity != null && StatePhase.getActivityStates().contains(this.statePhase)) {
            logger.debug("... from activity ...");
            if (Activities.checkIfCompetition(this.currentActivity)) {
                logger.debug("... for competition.");
                dialog = phraseManager.getValueByKey(HELP_COMPETITION_RULES_PHRASE);
                // Competititon Rules & Examples
            }
            else {
                logger.debug("... for non-competition.");
                dialog = phraseManager.getValueByKey(HELP_NEW_WORDS_PHRASE);
            }
        }
        else if (this.statePhase != null && this.currentMission != null) {
            logger.debug("... from mission ...");
            // Mission specific goal description
            // Current Star progress
            //  Do you want to continue playing?
            if (currentMission == UserMission.LOW_MISSION) {
                dialog = phraseManager.getValueByKey(HELP_MISSION_LOW_DESCRIPTION_PHRASE);
            }
            else if (currentMission == UserMission.MEDIUM_MISSION) {
                dialog = phraseManager.getValueByKey(HELP_MISSION_MID_DESCRIPTION_PHRASE);
            }
            else {
                dialog = phraseManager.getValueByKey(HELP_MISSION_HIGH_DESCRIPTION_PHRASE);
            }
        }
        else {
            logger.debug("... outside mission ...");
            // General Help %Game  description%
            dialog = phraseManager.getValueByKey(HELP_GENERAL_PHRASE);
        }
        return DialogItem.builder().withResponse(Speech.ofText(dialog)).build();
    }
}
