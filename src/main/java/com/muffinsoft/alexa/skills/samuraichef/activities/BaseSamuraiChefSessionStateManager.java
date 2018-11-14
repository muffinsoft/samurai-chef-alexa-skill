package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.muffinsoft.alexa.sdk.activities.BaseSessionStateManager;
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
import com.muffinsoft.alexa.skills.samuraichef.models.IngredientReaction;
import com.muffinsoft.alexa.skills.samuraichef.models.Speech;
import com.muffinsoft.alexa.skills.samuraichef.models.Stripe;
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.muffinsoft.alexa.sdk.model.Speech.ofText;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.FAILURE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.FAILURE_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.READY_TO_START_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.READY_TO_START_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WON_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WON_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
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

abstract class BaseSamuraiChefSessionStateManager extends BaseSessionStateManager {

    protected static final Logger logger = LogManager.getLogger(BaseSamuraiChefSessionStateManager.class);

    protected final PhraseManager phraseManager;
    protected final ActivityManager activityManager;

    protected final AliasManager aliasManager;
    protected final MissionManager missionManager;

    Activities currentActivity;
    StatePhase statePhase;
    Stripe stripe;
    ActivityProgress activityProgress;
    String dialogPrefix = null;
    private UserProgress userProgress;
    private UserMission currentMission;
    private boolean gameIsComplete = false;
    private boolean missionIsComplete = false;
    private boolean stripeIsComplete = false;

    BaseSamuraiChefSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivityManager activityManager, AliasManager aliasManager, MissionManager missionManager) {
        super(slots, attributesManager);
        this.phraseManager = phraseManager;
        this.activityManager = activityManager;
        this.aliasManager = aliasManager;
        this.missionManager = missionManager;
    }

    @Override
    protected void populateActivityVariables() {

        currentMission = UserMission.valueOf(String.valueOf(sessionAttributes.get(CURRENT_MISSION)));

        statePhase = StatePhase.valueOf(String.valueOf(sessionAttributes.getOrDefault(STATE_PHASE, MISSION_INTO)));

        LinkedHashMap rawUserProgress = (LinkedHashMap) sessionAttributes.get(USER_PROGRESS);
        userProgress = rawUserProgress != null ? mapper.convertValue(rawUserProgress, UserProgress.class) : new UserProgress(true);

        LinkedHashMap rawActivityProgress = (LinkedHashMap) sessionAttributes.get(ACTIVITY_PROGRESS);
        activityProgress = rawActivityProgress != null ? mapper.convertValue(rawActivityProgress, ActivityProgress.class) : new ActivityProgress();

        logger.debug("Session attributes on the start of handling: " + this.sessionAttributes.toString());
    }

    @Override
    protected void initializeSessionAttributes() {
        this.sessionAttributes = new HashMap<>();
    }

    @Override
    protected void updatePersistentAttributes() {
        try {
            String json = mapper.writeValueAsString(this.userProgress);
            switch (currentMission) {
                case LOW_MISSION:
                    persistentAttributes.put(USER_LOW_PROGRESS_DB, json);
                    break;
                case MEDIUM_MISSION:
                    persistentAttributes.put(USER_MID_PROGRESS_DB, json);
                    break;
                case HIGH_MISSION:
                    persistentAttributes.put(USER_HIGH_PROGRESS_DB, json);
                    break;
            }
        }
        catch (JsonProcessingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    protected void updateSessionAttributes() {

        this.userProgress.setLastActivity(this.currentActivity.name());

        sessionAttributes.put(USER_PROGRESS, this.userProgress);
        sessionAttributes.put(ACTIVITY_PROGRESS, this.activityProgress);
        sessionAttributes.put(STATE_PHASE, this.statePhase);
        sessionAttributes.put(ACTIVITY, this.currentActivity);

        logger.debug("Session attributes on the end of handling: " + this.sessionAttributes.toString());
    }

    @Override
    public DialogItem nextResponse() {

        logger.debug("Available slots at current step: " + this.slots.toString());

        DialogItem dialog;

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

    protected void resetActivityProgress() {
        this.statePhase = ACTIVITY_INTRO;
        this.activityProgress.reset();
    }

    protected abstract DialogItem handleActivePhaseState();

    private DialogItem handleMissionIntroState(UserMission currentMission) {

        this.statePhase = STRIPE_INTRO;

        String dialog = missionManager.getMissionIntro(currentMission);

        return DialogItem.builder().withResponse(ofText(dialog)).withSlotName(actionSlotName).build();
    }

    private DialogItem handleStripeIntroStripe(UserMission currentMission, int number) {

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
            this.statePhase = DEMO;
            dialog = appendShouldRunDemo(dialog);
        }
        else {
            this.statePhase = READY_PHASE;
            dialog = appendReadyToStart(dialog);
        }

        return DialogItem.builder().withResponse(ofText(dialog.toString())).withSlotName(actionSlotName).build();
    }

    private DialogItem handleDemoState() {

        this.statePhase = READY_PHASE;

        String dialog;
        String rePromptDialog;

        if (UserReplyComparator.compare(userReply, UserReplies.NO)) {

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

        this.statePhase = PHASE_1;

        return DialogItem.builder().withResponse(ofText(speechText)).withSlotName(actionSlotName).build();
    }

    private DialogItem handleWinState(UserMission currentMission, int number) {

        DialogItem dialog;

        calculateActivityProgress();

        resetActivityProgress();

        if (stripeIsComplete) {
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
            this.userProgress.setLastActivity(nextActivity.name());
            dialog = handleActivityIntroStripe(nextActivity, number);
        }

        savePersistentAttributes();

        return dialog;
    }

    private DialogItem handleLoseState() {

        DialogItem dialog;

        if (UserReplyComparator.compare(userReply, UserReplies.AGAIN) || UserReplyComparator.compare(userReply, UserReplies.YES)) {
            resetActivityProgress();
            dialog = handleActivityIntroStripe(this.currentActivity, this.userProgress.getStripeCount());
        }
        else {
            dialog = getLoseRoundDialog();
        }
        savePersistentAttributes();

        return dialog;
    }

    private DialogItem handleStripeOutroState(UserMission currentMission) {

        calculateStripeProgress();

        String dialog;

        if (missionIsComplete) {
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

        sessionAttributes.remove(CURRENT_MISSION);

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

    DialogItem getRepromptSuccessDialog() {
        return DialogItem.builder().withResponse(ofText(this.activityProgress.getPreviousIngredient())).withSlotName(actionSlotName).build();
    }

    DialogItem getSuccessDialog() {
        String ingredient = nextIngredient();
        return DialogItem.builder().withResponse(ofText(ingredient)).withSlotName(actionSlotName).build();
    }

    DialogItem getFailureDialog(String speechText) {
        String ingredient = nextIngredient();
        return DialogItem.builder().addResponse(ofText(speechText)).addResponse(ofText(ingredient)).withSlotName(actionSlotName).build();
    }

    DialogItem getLoseRoundDialog() {
        this.statePhase = LOSE;
        return DialogItem.builder().withResponse(ofText(phraseManager.getValueByKey(FAILURE_PHRASE))).withSlotName(actionSlotName).withReprompt(phraseManager.getValueByKey(FAILURE_REPROMPT_PHRASE)).build();
    }

    private String nextIngredient() {

        IngredientReaction nextIngredient = activityManager.getNextIngredient(this.stripe, this.activityProgress.getPreviousIngredient());

        this.activityProgress.setCurrentIngredientReaction(nextIngredient.getUserReply());
        this.activityProgress.setPreviousIngredient(nextIngredient.getIngredient());

        return nextIngredient.getIngredient();
    }
}
