package com.muffinsoft.alexa.skills.samuraichef.activities.action;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.components.UserReplyComparator;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivityManager;
import com.muffinsoft.alexa.skills.samuraichef.content.AliasManager;
import com.muffinsoft.alexa.skills.samuraichef.content.MissionManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;
import com.muffinsoft.alexa.skills.samuraichef.models.ActivityProgress;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.IngredientReaction;
import com.muffinsoft.alexa.skills.samuraichef.models.Speech;
import com.muffinsoft.alexa.skills.samuraichef.models.Stripe;
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.muffinsoft.alexa.sdk.model.Speech.ofText;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.FAILURE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.FAILURE_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.READY_TO_START_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.READY_TO_START_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.SELECT_MISSION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.SEVERAL_VALUES_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.TRY_AGAIN_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WON_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WON_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.QUESTION_TIME;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_HIGH_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_LOW_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_MID_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.ACTIVITY_INTRO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.DEMO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.LOSE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.MISSION_INTO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.MISSION_OUTRO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_1;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.READY_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.STRIPE_INTRO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.STRIPE_OUTRO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.WIN;

abstract class BaseSamuraiChefStateManager extends BaseStateManager {

    protected static final Logger logger = LogManager.getLogger(BaseSamuraiChefStateManager.class);

    protected final PhraseManager phraseManager;
    protected final ActivityManager activityManager;

    protected final AliasManager aliasManager;
    protected final MissionManager missionManager;
    protected Activities currentActivity;
    protected StatePhase statePhase;
    protected Stripe stripe;
    protected ActivityProgress activityProgress;
    protected String dialogPrefix = null;
    private UserProgress userProgress;
    private UserMission currentMission;
    private boolean gameIsComplete = false;
    private boolean missionIsComplete = false;
    private boolean stripeIsComplete = false;
    private boolean isLeaveMission = false;

    BaseSamuraiChefStateManager(Map<String, Slot> slots, AttributesManager attributesManager, ConfigContainer configContainer) {
        super(slots, attributesManager);
        this.phraseManager = configContainer.getPhraseManager();
        this.activityManager = configContainer.getActivityManager();
        this.aliasManager = configContainer.getAliasManager();
        this.missionManager = configContainer.getMissionManager();
    }

    @Override
    protected void populateActivityVariables() {

        currentMission = UserMission.valueOf(String.valueOf(getSessionAttributes().get(CURRENT_MISSION)));

        statePhase = StatePhase.valueOf(String.valueOf(getSessionAttributes().getOrDefault(STATE_PHASE, MISSION_INTO)));

        LinkedHashMap rawUserProgress = (LinkedHashMap) getSessionAttributes().get(USER_PROGRESS);
        userProgress = rawUserProgress != null ? mapper.convertValue(rawUserProgress, UserProgress.class) : new UserProgress(this.currentMission, true);

        LinkedHashMap rawActivityProgress = (LinkedHashMap) getSessionAttributes().get(ACTIVITY_PROGRESS);
        activityProgress = rawActivityProgress != null ? mapper.convertValue(rawActivityProgress, ActivityProgress.class) : new ActivityProgress();

        logger.debug("Session attributes on the start of handling: " + this.getSessionAttributes().toString());
    }

    @Override
    protected void updatePersistentAttributes() {
        updateUserProgress();
        if (stripeIsComplete) {
            updateStarCountInAllLevels();
        }
        if (missionIsComplete) {
            updateMissionCompleteInAllLevels();
        }
        logger.debug("Persistent attributes on the end of handling: " + this.getPersistentAttributes().toString());
    }

    private void updateUserProgress() {
        try {
            String json = mapper.writeValueAsString(this.userProgress);
            switch (currentMission) {
                case LOW_MISSION:
                    getPersistentAttributes().put(USER_LOW_PROGRESS_DB, json);
                    break;
                case MEDIUM_MISSION:
                    getPersistentAttributes().put(USER_MID_PROGRESS_DB, json);
                    break;
                case HIGH_MISSION:
                    getPersistentAttributes().put(USER_HIGH_PROGRESS_DB, json);
                    break;
            }
        }
        catch (JsonProcessingException e) {
            throw new IllegalStateException("Exception while saving Persistent Attributes", e);
        }
    }

    private void updateMissionCompleteInAllLevels() {
        if (this.currentMission == UserMission.LOW_MISSION) {
            updateMissionCompleteInForLevel(USER_MID_PROGRESS_DB);
            updateMissionCompleteInForLevel(USER_HIGH_PROGRESS_DB);
        }
        else if (this.currentMission == UserMission.MEDIUM_MISSION) {
            updateMissionCompleteInForLevel(USER_LOW_PROGRESS_DB);
            updateMissionCompleteInForLevel(USER_HIGH_PROGRESS_DB);
        }
        else {
            updateMissionCompleteInForLevel(USER_LOW_PROGRESS_DB);
            updateMissionCompleteInForLevel(USER_MID_PROGRESS_DB);
        }
    }

    private void updateMissionCompleteInForLevel(String value) {
        try {
            UserProgress missionUserProgress = null;
            if (getPersistentAttributes().containsKey(value)) {
                String jsonInString = String.valueOf(getPersistentAttributes().get(value));
                LinkedHashMap rawUserProgress = mapper.readValue(jsonInString, LinkedHashMap.class);
                missionUserProgress = mapper.convertValue(rawUserProgress, UserProgress.class);
            }
            if (missionUserProgress == null) {
                missionUserProgress = new UserProgress(this.currentMission);
            }
            missionUserProgress.addFinishedMission(this.currentMission.name());
            getPersistentAttributes().put(value, mapper.writeValueAsString(missionUserProgress));
        }
        catch (IOException e) {
            throw new IllegalStateException("Exception while updating Star Count in Persistent Attributes", e);
        }
    }

    private void updateStarCountInAllLevels() {
        if (this.currentMission == UserMission.LOW_MISSION) {
            updateStarCountForMission(USER_MID_PROGRESS_DB);
            updateStarCountForMission(USER_HIGH_PROGRESS_DB);
        }
        else if (this.currentMission == UserMission.MEDIUM_MISSION) {
            updateStarCountForMission(USER_LOW_PROGRESS_DB);
            updateStarCountForMission(USER_HIGH_PROGRESS_DB);
        }
        else {
            updateStarCountForMission(USER_LOW_PROGRESS_DB);
            updateStarCountForMission(USER_MID_PROGRESS_DB);
        }
    }

    private void updateStarCountForMission(String value) {
        try {
            UserProgress missionUserProgress = null;
            if (getPersistentAttributes().containsKey(value)) {
                String jsonInString = String.valueOf(getPersistentAttributes().get(value));
                LinkedHashMap rawUserProgress = mapper.readValue(jsonInString, LinkedHashMap.class);
                missionUserProgress = mapper.convertValue(rawUserProgress, UserProgress.class);
            }
            if (missionUserProgress == null) {
                missionUserProgress = new UserProgress(this.currentMission);
            }
            missionUserProgress.iterateStarCount();
            getPersistentAttributes().put(value, mapper.writeValueAsString(missionUserProgress));
        }
        catch (IOException e) {
            throw new IllegalStateException("Exception while updating Star Count in Persistent Attributes", e);
        }
    }

    @Override
    protected void updateSessionAttributes() {

        this.userProgress.setCurrentActivity(this.currentActivity.name());

        if (this.isLeaveMission) {
            getSessionAttributes().remove(CURRENT_MISSION);
            getSessionAttributes().remove(USER_PROGRESS);
            getSessionAttributes().remove(ACTIVITY_PROGRESS);
            getSessionAttributes().remove(ACTIVITY);
            getSessionAttributes().remove(STATE_PHASE);
            getSessionAttributes().remove(QUESTION_TIME);
        }
        else {
            getSessionAttributes().put(USER_PROGRESS, this.userProgress);
            getSessionAttributes().put(ACTIVITY_PROGRESS, this.activityProgress);
            getSessionAttributes().put(STATE_PHASE, this.statePhase);
            getSessionAttributes().put(ACTIVITY, this.currentActivity);
        }

        logger.debug("Session attributes on the end of handling: " + this.getSessionAttributes().toString());
    }

    @Override
    public DialogItem nextResponse() {

        DialogItem dialog;

        if (!getUserMultipleReplies().isEmpty()) {
            return handleMultipleResponses();
        }

        stripe = activityManager.getLevelForActivity(this.currentActivity, this.userProgress.getStripeCount());

        switch (this.statePhase) {
            case MISSION_INTO:
                dialog = handleMissionIntroState(this.currentMission);
                break;
            case STRIPE_INTRO:
                dialog = handleStripeIntroStripe(this.currentMission, this.userProgress.getStripeCount());
                break;
            case ACTIVITY_INTRO:
                dialog = handleActivityIntroStripe(this.currentActivity, this.userProgress.getStripeCount());
                break;
            case DEMO:
                dialog = handleDemoState();
                break;
            case READY_PHASE:
                dialog = handleReadyToStartState();
                break;
            case LOSE:
                dialog = handleLoseState();
                break;
            case WIN:
                dialog = handleWinState(this.currentMission, this.userProgress.getStripeCount());
                break;
            case STRIPE_OUTRO:
                dialog = handleStripeOutroState(this.currentMission);
                break;
            case MISSION_OUTRO:
                dialog = handleMissionOutroState(this.currentMission);
                break;
            default:
                dialog = handleActivePhaseState();
                break;
        }

        return checkOnAdditions(dialog);
    }

    private DialogItem handleMultipleResponses() {

        String speech = phraseManager.getValueByKey(SEVERAL_VALUES_PHRASE) + String.join(", ", this.getUserMultipleReplies());

        return DialogItem.builder().withSlotName(actionSlotName).withResponse(ofText(speech)).build();
    }

    protected void resetActivityProgress() {
        this.statePhase = ACTIVITY_INTRO;
        this.activityProgress.reset();
    }

    protected abstract DialogItem handleActivePhaseState();

    private DialogItem handleMissionIntroState(UserMission currentMission) {

        logger.debug("Handling " + this.statePhase + ". Moving to " + STRIPE_INTRO);

        this.statePhase = STRIPE_INTRO;

        String dialog = missionManager.getMissionIntro(currentMission);

        return DialogItem.builder().withResponse(ofText(dialog)).withSlotName(actionSlotName).build();
    }

    private DialogItem handleStripeIntroStripe(UserMission currentMission, int number) {

        logger.debug("Handling " + this.statePhase + ". Moving to " + ACTIVITY_INTRO);

        this.statePhase = ACTIVITY_INTRO;

        String dialog = missionManager.getStripeIntroByMission(currentMission, number);

        return DialogItem.builder().withResponse(ofText(dialog)).withSlotName(actionSlotName).build();
    }

    private DialogItem handleActivityIntroStripe(Activities activity, int number) {

        Speech speech = activityManager.getSpeechForActivityByStripeNumber(activity, number);

        StringBuilder dialog = new StringBuilder();

        for (String partOfSpeech : speech.getIntro()) {
            dialog.append(partOfSpeech);
            dialog.append(" ");
        }

        if (speech.isShouldRunDemo()) {

            logger.debug("Handling " + this.statePhase + ". Moving to " + DEMO);

            this.statePhase = DEMO;
            dialog = appendShouldRunDemo(dialog);
        }
        else {

            logger.debug("Handling " + this.statePhase + ". Moving to " + READY_PHASE);

            this.statePhase = READY_PHASE;
            dialog = appendReadyToStart(dialog);
        }

        return DialogItem.builder().withResponse(ofText(dialog.toString())).withSlotName(actionSlotName).build();
    }

    private DialogItem handleDemoState() {

        logger.debug("Handling " + this.statePhase + ". Moving to " + READY_PHASE);

        this.statePhase = READY_PHASE;

        String dialog;
        String rePromptDialog;

        if (UserReplyComparator.compare(getUserReply(), UserReplies.NO)) {

            dialog = phraseManager.getValueByKey(READY_TO_START_PHRASE);
            rePromptDialog = phraseManager.getValueByKey(READY_TO_START_REPROMPT_PHRASE);
        }
        else {

            Speech speech = activityManager.getSpeechForActivityByStripeNumber(this.currentActivity, this.userProgress.getStripeCount());

            StringBuilder dialogBuilder = new StringBuilder();

            for (String partOfSpeech : speech.getDemo()) {
                dialogBuilder.append(partOfSpeech);
                dialogBuilder.append(" ");
            }

            dialog = appendReadyToStart(dialogBuilder).toString();

            rePromptDialog = phraseManager.getValueByKey(READY_TO_START_REPROMPT_PHRASE);
        }

        return DialogItem.builder().withResponse(ofText(dialog)).withSlotName(actionSlotName).withReprompt(rePromptDialog).build();
    }

    private DialogItem handleReadyToStartState() {

        String speechText = nextIngredient();

        logger.debug("Handling " + this.statePhase + ". Moving to " + PHASE_1);

        this.statePhase = PHASE_1;

        return DialogItem.builder().withResponse(ofText(speechText)).withSlotName(actionSlotName).build();
    }

    private DialogItem handleWinState(UserMission currentMission, int number) {

        DialogItem dialog;

        calculateActivityProgress();

        resetActivityProgress();

        if (stripeIsComplete) {

            logger.debug("Handling " + this.statePhase + ". Moving to " + STRIPE_OUTRO);

            this.statePhase = STRIPE_OUTRO;
            String dialogPhrase = missionManager.getStripeOutroByMission(currentMission, number);
            dialog = DialogItem.builder().withResponse(ofText(dialogPhrase)).withSlotName(actionSlotName).build();
        }
        else {
            Activities nextActivity = missionManager.getNextActivity(this.currentActivity, currentMission);

            boolean invalidCondition = this.userProgress.getFinishedActivities().contains(nextActivity.name());

            while (invalidCondition) {
                nextActivity = missionManager.getNextActivity(nextActivity, currentMission);
                invalidCondition = this.userProgress.getFinishedActivities().contains(nextActivity.name());
            }

            currentActivity = nextActivity;
            this.userProgress.setCurrentActivity(nextActivity.name());
            dialog = handleActivityIntroStripe(nextActivity, number);
        }

        savePersistentAttributes();

        return dialog;
    }

    private DialogItem handleLoseState() {

        DialogItem dialog;

        if (UserReplyComparator.compare(getUserReply(), UserReplies.AGAIN) || UserReplyComparator.compare(getUserReply(), UserReplies.YES)) {
            resetActivityProgress();
            dialog = handleActivityIntroStripe(this.currentActivity, this.userProgress.getStripeCount());
        }
        else {
            dialog = getSelectMissionDialog();
        }
        savePersistentAttributes();

        return dialog;
    }

    private DialogItem getSelectMissionDialog() {

        logger.debug("Handling " + this.statePhase + ". Moving to " + MISSION_INTO);

        isLeaveMission = true;

        this.statePhase = MISSION_INTO;

        this.getSessionAttributes().remove(CURRENT_MISSION);

        return DialogItem.builder().withSlotName(actionSlotName).withResponse(ofText(phraseManager.getValueByKey(SELECT_MISSION_PHRASE))).build();
    }

    private DialogItem handleStripeOutroState(UserMission currentMission) {

        calculateStripeProgress();

        String dialog;

        if (missionIsComplete) {

            logger.debug("Handling " + this.statePhase + ". Moving to " + MISSION_OUTRO);

            this.statePhase = MISSION_OUTRO;
            dialog = missionManager.getMissionOutro(currentMission);
        }
        else {
            return handleStripeIntroStripe(this.currentMission, this.userProgress.getStripeCount());
        }

        return DialogItem.builder().withResponse(ofText(dialog)).withSlotName(actionSlotName).build();
    }

    private DialogItem handleMissionOutroState(UserMission currentMission) {

        // change mission dialog

        isLeaveMission = true;

        logger.debug("Handling " + this.statePhase + ". Moving to " + MISSION_INTO);

        this.statePhase = MISSION_INTO;

        String dialog = missionManager.getMissionOutro(currentMission);

        return DialogItem.builder().withResponse(ofText(dialog)).withSlotName(actionSlotName).build();
    }

    private DialogItem checkOnAdditions(DialogItem dialog) {
        if (dialogPrefix != null) {
            dialog.addResponseToBegining(ofText(dialogPrefix));
        }
        return dialog;
    }

    private void calculateStripeProgress() {

        if (this.userProgress.getStripeCount() == missionManager.getContainer().getStripesAtMissionCount()) {
            this.userProgress.addFinishedMission(this.currentMission.name());
            this.missionIsComplete = true;
        }

        if (this.userProgress.getStarCount() == missionManager.getContainer().getMaxStarCount()) {
            this.gameIsComplete = true;
        }
    }

    private void calculateActivityProgress() {

        this.userProgress.addFinishedActivities(this.currentActivity.name());

        if (this.userProgress.getFinishedActivities().size() == Activities.values().length) {

            this.userProgress.iterateStripeCount();
            this.userProgress.iterateStarCount();
            this.userProgress.resetFinishRounds();

            this.currentActivity = missionManager.getFirstActivityForLevel(currentMission);

            this.stripeIsComplete = true;
        }
    }

    private StringBuilder appendShouldRunDemo(StringBuilder dialog) {

        Speech speech = activityManager.getSpeechForActivityByStripeNumber(this.currentActivity, this.userProgress.getStripeCount());

        dialog.append(" ").append(speech.getShouldRunDemoPhrase());

        return dialog;
    }

    private StringBuilder appendReadyToStart(StringBuilder dialog) {

        Speech speech = activityManager.getSpeechForActivityByStripeNumber(this.currentActivity, this.userProgress.getStripeCount());

        dialog.append(" ").append(speech.getReadyToStartPhrase());

        return dialog;
    }

    DialogItem getWinDialog() {
        this.statePhase = WIN;
        return DialogItem.builder().withResponse(ofText(phraseManager.getValueByKey(WON_PHRASE))).withSlotName(actionSlotName).withReprompt(phraseManager.getValueByKey(WON_REPROMPT_PHRASE)).build();
    }

    DialogItem getRePromptSuccessDialog() {
        return DialogItem.builder()
                .withResponse(ofText(phraseManager.getValueByKey(TRY_AGAIN_PHRASE) + " " + this.activityProgress.getPreviousIngredient()))
                .withSlotName(actionSlotName)
                .build();
    }

    DialogItem getSuccessDialog() {
        String ingredient = nextIngredient();
        return DialogItem.builder().withResponse(ofText(ingredient)).withSlotName(actionSlotName).build();
    }

    DialogItem getFailureDialog(String speechText) {
        String ingredient = nextIngredient();
        return DialogItem.builder().addResponse(ofText(speechText)).addResponse(ofText(ingredient)).withSlotName(actionSlotName).build();
    }

    DialogItem getLoseRoundDialog(String value) {
        this.statePhase = LOSE;
        return DialogItem.builder()
                .addResponse(ofText(phraseManager.getValueByKey(value)))
                .addResponse(ofText(phraseManager.getValueByKey(FAILURE_PHRASE)))
                .withSlotName(actionSlotName)
                .withReprompt(phraseManager.getValueByKey(FAILURE_REPROMPT_PHRASE))
                .build();
    }

    private String nextIngredient() {

        IngredientReaction nextIngredient = activityManager.getNextIngredient(this.stripe, this.activityProgress.getPreviousIngredient());

        this.activityProgress.setCurrentIngredientReaction(nextIngredient.getUserReply());
        this.activityProgress.setPreviousIngredient(nextIngredient.getIngredient());

        return nextIngredient.getIngredient();
    }
}
