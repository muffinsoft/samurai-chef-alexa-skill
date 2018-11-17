package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.components.UserReplyComparator;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.HelpStates;
import com.muffinsoft.alexa.skills.samuraichef.enums.Intents;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;
import com.muffinsoft.alexa.skills.samuraichef.models.ActivityProgress;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.muffinsoft.alexa.sdk.model.Speech.ofText;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.HELP_ACTIVITY_FOOD_TASTER_DESCRIPTION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.HELP_ACTIVITY_JUICE_WARRIOR_DESCRIPTION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.HELP_ACTIVITY_SUSHI_SLICE_DESCRIPTION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.HELP_ACTIVITY_WORD_BOARD_KARATE_DESCRIPTION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.HELP_COMPETITION_RULES_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.HELP_CONTINUE_PLAYING_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.HELP_GENERAL_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.HELP_LEARN_MORE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.HELP_MISSION_HIGH_DESCRIPTION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.HELP_MISSION_LOW_DESCRIPTION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.HELP_MISSION_MID_DESCRIPTION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.HELP_MORE_DETAILS_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.HELP_NEW_WORDS_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.RETURN_TO_GAME_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.SELECT_MISSION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WANT_START_MISSION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.HELP_STATE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.QUESTION_TIME;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.enums.HelpStates.COMPETITION_REMINDER_HELP;
import static com.muffinsoft.alexa.skills.samuraichef.enums.HelpStates.CONTINUE_PLAYING_HELP;
import static com.muffinsoft.alexa.skills.samuraichef.enums.HelpStates.GENERAL_HELP;
import static com.muffinsoft.alexa.skills.samuraichef.enums.HelpStates.LEARN_MORE_HELP;
import static com.muffinsoft.alexa.skills.samuraichef.enums.HelpStates.MORE_DETAILS_HELP;
import static com.muffinsoft.alexa.skills.samuraichef.enums.HelpStates.valueOf;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.MISSION_INTRO;

public class HelpStateManager extends BaseStateManager {

    private static final Logger logger = LogManager.getLogger(HelpStateManager.class);

    private final PhraseManager phraseManager;

    private ActivityProgress activityProgress;
    private StatePhase statePhase;
    private UserProgress userProgress;
    private UserMission currentMission;
    private Activities currentActivity;
    private Intents currentIntent;

    public HelpStateManager(Map<String, Slot> inputSlots, AttributesManager attributesManager, ConfigContainer configContainer) {
        super(inputSlots, attributesManager);
        this.phraseManager = configContainer.getPhraseManager();
    }

    @Override
    protected void populateActivityVariables() {

        String stringifyMission = String.valueOf(getSessionAttributes().get(CURRENT_MISSION));
        if (stringifyMission != null && !stringifyMission.isEmpty() && !stringifyMission.equals("null")) {
            this.currentMission = UserMission.valueOf(stringifyMission);
        }
        else {
            this.currentMission = null;
        }

        this.currentIntent = Intents.valueOf(String.valueOf(getSessionAttributes().getOrDefault(INTENT, Intents.GAME.name())));

        String stringifyState = String.valueOf(getSessionAttributes().getOrDefault(STATE_PHASE, MISSION_INTRO));
        if (stringifyState != null && !stringifyState.isEmpty() && !stringifyState.equals("null")) {
            this.statePhase = StatePhase.valueOf(stringifyState);
        }
        else {
            this.statePhase = null;
        }

        LinkedHashMap rawUserProgress = (LinkedHashMap) getSessionAttributes().get(USER_PROGRESS);
        this.userProgress = rawUserProgress != null ? mapper.convertValue(rawUserProgress, UserProgress.class) : new UserProgress(this.currentMission, true);

        this.currentActivity = this.userProgress.getCurrentActivity() != null ? Activities.valueOf(this.userProgress.getCurrentActivity()) : null;

        LinkedHashMap rawActivityProgress = (LinkedHashMap) getSessionAttributes().get(ACTIVITY_PROGRESS);
        this.activityProgress = rawActivityProgress != null ? mapper.convertValue(rawActivityProgress, ActivityProgress.class) : new ActivityProgress();

        logger.debug("Session attributes on the start of handling: " + this.getSessionAttributes().toString());
    }

    @Override
    public DialogItem nextResponse() {

        if (this.currentIntent == Intents.GAME) {
            return handleFirstLoopHelp();
        }
        else {
            return handleSecondLoopHelp();
        }
    }

    private DialogItem handleSecondLoopHelp() {

        HelpStates helpState = valueOf(String.valueOf(getSessionAttributes().get(HELP_STATE)));

        DialogItem.Builder builder = DialogItem.builder();

        switch (helpState) {

            case GENERAL_HELP:

                if (UserReplyComparator.compare(getUserReply(), UserReplies.YES)) {
                    getSessionAttributes().put(INTENT, Intents.GAME);
                    getSessionAttributes().remove(HELP_STATE);
                    getSessionAttributes().remove(CURRENT_MISSION);
                    builder.addResponse(ofText(phraseManager.getValueByKey(SELECT_MISSION_PHRASE)));
                }
                else {
                    handleProceedGame(builder);
                }
                break;

            case LEARN_MORE_HELP:
                if (UserReplyComparator.compare(getUserReply(), UserReplies.YES)) {
                    handleMissionDescription(builder);
                }
                else {
                    handleProceedGame(builder);
                }
                break;

            case CONTINUE_PLAYING_HELP:
                if (UserReplyComparator.compare(getUserReply(), UserReplies.YES)) {
                    handleProceedGame(builder);
                }
                else {
                    handleGeneralHelp(builder);
                }
                break;

            case MORE_DETAILS_HELP:
                if (UserReplyComparator.compare(getUserReply(), UserReplies.YES)) {
                    handleActivityDescription(builder);
                }
                handleLearnMore(builder);
                break;

            case COMPETITION_REMINDER_HELP:
                if (UserReplyComparator.compare(getUserReply(), UserReplies.YES)) {
                    handleLearnMore(builder);
                }
                else {
                    handleGeneralHelp(builder);
                }
                break;
        }

        return builder.build();
    }

    private DialogItem handleFirstLoopHelp() {

        getSessionAttributes().put(INTENT, Intents.HELP);

        DialogItem.Builder builder = DialogItem.builder();

        if (this.currentActivity != null) {
            if (Activities.checkIfCompetition(this.currentActivity)) {
                // Competition Rules & Examples
                builder.addResponse(ofText(phraseManager.getValueByKey(HELP_COMPETITION_RULES_PHRASE)));
                getSessionAttributes().put(HELP_STATE, COMPETITION_REMINDER_HELP);
            }
            else {
                builder.addResponse(ofText(phraseManager.getValueByKey(HELP_NEW_WORDS_PHRASE)));
                if (userProgress.getFinishedMissions().isEmpty() && userProgress.getFinishedActivities().isEmpty()) {
                    handleLearnMore(builder);
                }
                else {
                    getSessionAttributes().put(HELP_STATE, MORE_DETAILS_HELP);
                    builder.addResponse(ofText(phraseManager.getValueByKey(HELP_MORE_DETAILS_PHRASE)));
                }
            }
        }
        else if (this.currentMission != null) {
            handleMissionDescription(builder);
        }
        else {
            handleGeneralHelp(builder);
        }

        getSessionAttributes().put(INTENT, Intents.HELP);
        return builder.build();
    }


    private void handleLearnMore(DialogItem.Builder builder) {
        getSessionAttributes().put(HELP_STATE, LEARN_MORE_HELP);
        builder.addResponse(ofText(phraseManager.getValueByKey(HELP_LEARN_MORE_PHRASE)));
    }

    private void handleActivityDescription(DialogItem.Builder builder) {
        if (currentActivity == Activities.SUSHI_SLICE) {
            builder.addResponse(ofText(phraseManager.getValueByKey(HELP_ACTIVITY_SUSHI_SLICE_DESCRIPTION_PHRASE)));
        }
        else if (currentActivity == Activities.JUICE_WARRIOR) {
            builder.addResponse(ofText(phraseManager.getValueByKey(HELP_ACTIVITY_JUICE_WARRIOR_DESCRIPTION_PHRASE)));
        }
        else if (currentActivity == Activities.WORD_BOARD_KARATE) {
            builder.addResponse(ofText(phraseManager.getValueByKey(HELP_ACTIVITY_WORD_BOARD_KARATE_DESCRIPTION_PHRASE)));
        }
        else {
            builder.addResponse(ofText(phraseManager.getValueByKey(HELP_ACTIVITY_FOOD_TASTER_DESCRIPTION_PHRASE)));
        }
    }

    private void handleMissionDescription(DialogItem.Builder builder) {
        // Mission specific goal description
        // Current Star progress
        //  Do you want to continue playing?
        if (currentMission == UserMission.LOW_MISSION) {
            builder.addResponse(ofText(phraseManager.getValueByKey(HELP_MISSION_LOW_DESCRIPTION_PHRASE)));
        }
        else if (currentMission == UserMission.MEDIUM_MISSION) {
            builder.addResponse(ofText(phraseManager.getValueByKey(HELP_MISSION_MID_DESCRIPTION_PHRASE)));
        }
        else {
            builder.addResponse(ofText(phraseManager.getValueByKey(HELP_MISSION_HIGH_DESCRIPTION_PHRASE)));
        }
        getSessionAttributes().put(HELP_STATE, CONTINUE_PLAYING_HELP);
        builder.addResponse(ofText(phraseManager.getValueByKey(HELP_CONTINUE_PLAYING_PHRASE)));
    }

    private void handleProceedGame(DialogItem.Builder builder) {
        getSessionAttributes().put(INTENT, Intents.GAME);
        getSessionAttributes().remove(HELP_STATE);
        builder.addResponse(ofText(phraseManager.getValueByKey(RETURN_TO_GAME_PHRASE)));
        if (statePhase == StatePhase.PHASE_1 || statePhase == StatePhase.PHASE_2) {
            builder.addResponse(ofText(activityProgress.getPreviousIngredient()));
        }
        getSessionAttributes().put(QUESTION_TIME, System.currentTimeMillis());
        getSessionAttributes().remove(HELP_STATE);
    }

    private void handleGeneralHelp(DialogItem.Builder builder) {
        getSessionAttributes().put(HELP_STATE, GENERAL_HELP);
        // General Help %Game  description%
        builder.addResponse(ofText(phraseManager.getValueByKey(HELP_GENERAL_PHRASE)));
        builder.addResponse(ofText(phraseManager.getValueByKey(WANT_START_MISSION_PHRASE)));
    }
}