package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.muffinsoft.alexa.sdk.activities.BaseSessionStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.components.UserReplyComparator;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivityManager;
import com.muffinsoft.alexa.skills.samuraichef.content.MissionManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;
import com.muffinsoft.alexa.skills.samuraichef.models.ActivityProgress;
import com.muffinsoft.alexa.skills.samuraichef.models.IngredientReaction;
import com.muffinsoft.alexa.skills.samuraichef.models.Speech;
import com.muffinsoft.alexa.skills.samuraichef.models.Stripe;
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.muffinsoft.alexa.sdk.model.SlotName.ACTION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.DEMO_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.FAILURE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.FAILURE_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.READY_TO_START_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.READY_TO_START_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WON_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WON_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.DEBUG_MESSAGE;
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

    private static final Logger logger = LoggerFactory.getLogger(BaseSessionStateManager.class);

    protected final PhraseManager phraseManager;
    protected final ActivityManager activityManager;

    private final PowerUpsManager powerUpsManager;
    private final MissionManager missionManager;

    protected Activities currentActivity;
    protected StatePhase statePhase;
    protected Stripe stripe;
    protected UserProgress userProgress;
    protected ActivityProgress activityProgress;

    protected String dialogPrefix = null;
    protected UserMission currentMission;
    private boolean gameIsComplete = false;
    private boolean missionIsComplete = false;
    private boolean stripeIsComplete = false;

    BaseSamuraiChefSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivityManager activityManager, PowerUpsManager powerUpsManager, MissionManager missionManager) {
        super(slots, attributesManager);
        this.phraseManager = phraseManager;
        this.activityManager = activityManager;
        this.powerUpsManager = powerUpsManager;
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
                case LOW:
                    persistentAttributes.put(USER_LOW_PROGRESS_DB, json);
                    break;
                case MEDIUM:
                    persistentAttributes.put(USER_MID_PROGRESS_DB, json);
                    break;
                case HIGH:
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

        if (!debugMessage.toString().isEmpty()) {
            sessionAttributes.put(DEBUG_MESSAGE, debugMessage.toString());
        }

        logger.debug("Session attributes on the end of handling: " + this.sessionAttributes.toString());
    }

    @Override
    public DialogItem nextResponse() {

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

        dialog = checkOnAdditions(dialog);

        return dialog;
    }

    protected void resetActivityProgress() {
        this.statePhase = ACTIVITY_INTRO;
        this.activityProgress.reset();
    }

    protected abstract DialogItem handleActivePhaseState();

    private DialogItem handleMissionIntroState(UserMission currentMission) {

        this.statePhase = STRIPE_INTRO;

        String dialog = missionManager.getMissionIntro(currentMission);

        return new DialogItem(dialog, false, actionSlotName);
    }

    private DialogItem handleStripeIntroStripe(UserMission currentMission, int number) {

        this.statePhase = ACTIVITY_INTRO;

        String dialog = missionManager.getStripeIntroByMission(currentMission, number);

        return new DialogItem(dialog, false, actionSlotName);
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

        return new DialogItem(dialog.toString(), false, actionSlotName);
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

            rePromptDialog = phraseManager.getValueByKey(this.currentActivity.getTitle() + DEMO_REPROMPT_PHRASE);
        }

        return new DialogItem(dialog, false, actionSlotName, true, rePromptDialog);
    }

    private DialogItem handleReadyToStartState() {

        String speechText = nextIngredient();

        this.statePhase = PHASE_1;

        return new DialogItem(speechText, false, ACTION.text);
    }

    private DialogItem handleWinState(UserMission currentMission, int number) {

        DialogItem dialog;

        calculateActivityProgress();

        resetActivityProgress();

        if (stripeIsComplete) {
            this.statePhase = STRIPE_OUTRO;
            String dialogPhrase = missionManager.getStripeOutroByMission(currentMission, number);
            dialog = new DialogItem(dialogPhrase, true, actionSlotName);
        }
        else {
            Activities nextActivity = missionManager.getNextActivity(this.currentActivity, currentMission);

            boolean invalidCondition = this.userProgress.getFinishedActivities().contains(nextActivity.name());

            while (invalidCondition) {
                nextActivity = missionManager.getNextActivity(nextActivity, currentMission);
                invalidCondition = this.userProgress.getFinishedActivities().contains(nextActivity.getTitle());
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

        return new DialogItem(dialog, false, actionSlotName);
    }

    private DialogItem handleMissionOutroState(UserMission currentMission) {

        // change mission dialog

        sessionAttributes.remove(CURRENT_MISSION);

        String dialog = missionManager.getMissionOutro(currentMission);

        return new DialogItem(dialog, false, actionSlotName);
    }

    private DialogItem checkOnAdditions(DialogItem dialog) {

        if (dialogPrefix != null) {

            String responseText = dialog.getResponseText();

            dialog.setResponseText(dialogPrefix + responseText);
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
        this.userProgress.removePowerUp();
        this.statePhase = WIN;
        return new DialogItem(phraseManager.getValueByKey(WON_PHRASE), false, actionSlotName, true, phraseManager.getValueByKey(WON_REPROMPT_PHRASE));
    }

    DialogItem getRepromptSuccessDialog() {
        return new DialogItem(this.activityProgress.getPreviousIngredient(), false, actionSlotName);
    }

    DialogItem getSuccessDialog() {
        return getSuccessDialog("");
    }

    DialogItem getSuccessDialog(String speechText) {
        String ingredient = nextIngredient();
        speechText = speechText + " " + ingredient;
        return new DialogItem(speechText, false, actionSlotName);
    }

    DialogItem getFailureDialog(String speechText) {
        String ingredient = nextIngredient();
        speechText = speechText + " " + ingredient;
        return new DialogItem(speechText, false, actionSlotName);
    }

    DialogItem getLoseRoundDialog() {

        this.userProgress.removePowerUp();

        this.statePhase = LOSE;
        return new DialogItem(phraseManager.getValueByKey(FAILURE_PHRASE), false, actionSlotName, true, phraseManager.getValueByKey(FAILURE_REPROMPT_PHRASE));
    }

    private String nextIngredient() {

        IngredientReaction nextIngredient = activityManager.getNextIngredient(this.stripe, this.activityProgress.getPreviousIngredient());

        this.activityProgress.setCurrentIngredientReaction(nextIngredient.getUserReply());
        this.activityProgress.setPreviousIngredient(nextIngredient.getIngredient());

        return nextIngredient.getIngredient();
    }
}
