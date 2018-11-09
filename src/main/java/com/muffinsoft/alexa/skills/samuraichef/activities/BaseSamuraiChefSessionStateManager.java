package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.muffinsoft.alexa.sdk.activities.BaseSessionStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.components.ActivityEquipmentFilter;
import com.muffinsoft.alexa.skills.samuraichef.components.UserReplyComparator;
import com.muffinsoft.alexa.skills.samuraichef.content.LevelManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.ProgressManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.Equipments;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserLevel;
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
import java.util.Set;

import static com.muffinsoft.alexa.sdk.model.SlotName.ACTION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.CONGRATULATION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.DEMO_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.FAILURE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.FAILURE_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.GAME_FINISHED_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.JUST_WEAR_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.READY_TO_START_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.READY_TO_START_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WON_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WON_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.DEBUG_MESSAGE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_HIGH_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_LEVEL;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_LOW_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_MID_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.DEMO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.EQUIPMENT_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.INTRO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.LOSE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_1;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.READY_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.WIN;

abstract class BaseSamuraiChefSessionStateManager extends BaseSessionStateManager {

    private static final Logger logger = LoggerFactory.getLogger(BaseSessionStateManager.class);

    protected final PhraseManager phraseManager;
    protected final LevelManager levelManager;

    private final PowerUpsManager powerUpsManager;
    private final ProgressManager progressManager;

    protected Activities currentActivity;
    protected StatePhase statePhase;
    protected Stripe stripe;
    protected UserProgress userProgress;
    protected ActivityProgress activityProgress;

    protected String dialogPrefix = null;
    protected UserLevel currentLevel;
    private boolean gameIsComplete = false;
    private boolean levelIsComplete = false;

    BaseSamuraiChefSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, LevelManager levelManager, PowerUpsManager powerUpsManager, ProgressManager progressManager) {
        super(slots, attributesManager);
        this.phraseManager = phraseManager;
        this.levelManager = levelManager;
        this.powerUpsManager = powerUpsManager;
        this.progressManager = progressManager;
    }

    @Override
    protected void populateActivityVariables() {

        currentLevel = UserLevel.valueOf(String.valueOf(sessionAttributes.get(USER_LEVEL)));

        statePhase = StatePhase.valueOf(String.valueOf(sessionAttributes.getOrDefault(STATE_PHASE, INTRO)));

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
            switch (currentLevel) {
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
        sessionAttributes.put(STATE_PHASE, statePhase);
        sessionAttributes.put(ACTIVITY, currentActivity);
        if (!debugMessage.toString().isEmpty()) {
            sessionAttributes.put(DEBUG_MESSAGE, debugMessage.toString());
        }
        logger.debug("Session attributes on the end of handling: " + this.sessionAttributes.toString());
    }

    @Override
    public DialogItem nextResponse() {

        DialogItem dialog;

        stripe = levelManager.getLevelForActivity(this.currentActivity, this.userProgress.getStripeCount());

        switch (this.statePhase) {
            case INTRO:
                dialog = getIntroDialog(this.currentActivity, this.userProgress.getStripeCount());
                break;
            case DEMO:
                dialog = handleDemoPhase();
                break;
            case EQUIPMENT_PHASE:
                dialog = handleEquipmentPhase();
                break;
            case READY_PHASE:
                dialog = handleReadyToStartPhase();
                break;
            case LOSE:
                dialog = handleLoseOrChangeMission();
                break;
            case WIN:
                dialog = handleWinPhase();
                break;
            default:
                dialog = getActivePhaseDialog();
        }

        dialog = checkOnAdditions(dialog);

        return dialog;
    }

    protected void resetActivityProgress() {
        this.statePhase = INTRO;
        this.activityProgress.reset();
    }

    protected abstract DialogItem getActivePhaseDialog();

    private DialogItem checkOnAdditions(DialogItem dialog) {

        if (dialogPrefix != null) {

            String responseText = dialog.getResponseText();

            dialog.setResponseText(dialogPrefix + responseText);
        }
        return dialog;
    }

    private DialogItem handleWinPhase() {

        DialogItem dialog;

        calculateLevelProgress();

        resetActivityProgress();

        if (gameIsComplete) {
            dialog = gameIsFinishedDialog();
        }
        else {
            dialog = startNewMissionDialog();
        }
        savePersistentAttributes();

        return dialog;
    }

    private DialogItem handleLoseOrChangeMission() {

        DialogItem dialog;

        if (UserReplyComparator.compare(userReply, UserReplies.AGAIN) || UserReplyComparator.compare(userReply, UserReplies.YES)) {
            resetActivityProgress();
            dialog = getIntroDialog(this.currentActivity, this.userProgress.getStripeCount());
        }
        else {
            dialog = getLoseRoundDialog();
        }
        savePersistentAttributes();

        return dialog;
    }

    private DialogItem handleReadyToStartPhase() {

        String speechText = nextIngredient();

        this.statePhase = PHASE_1;

        return new DialogItem(speechText, false, ACTION.text);
    }

    private DialogItem handleEquipmentPhase() {

        if (UserReplyComparator.compare(userReply, UserReplies.NO)) {
            return getReadyToStartDialog();
        }
        else {
            return wearEquipmentDialog();
        }
    }

    private DialogItem handleDemoPhase() {

        if (UserReplyComparator.compare(userReply, UserReplies.NO)) {

            if (isAvailablePowerUpsForActivity()) {
                return getWearEquipmentDialog();
            }
            else {
                return getReadyToStartDialog();
            }
        }
        else {
            return getDemoDialog(this.currentActivity, this.userProgress.getStripeCount());
        }
    }

    private void calculateLevelProgress() {

        this.userProgress.addFinishedRound(this.currentActivity.name());

        if (this.userProgress.getFinishedRounds().size() == Activities.values().length) {

            this.activityProgress.setJustStripeUp(true);

            this.userProgress.iterateStripeCount();
            this.userProgress.iterateStarCount();
            this.userProgress.resetFinishRounds();

            this.currentActivity = progressManager.getFirstActivityForLevel(currentLevel);

            if (this.userProgress.getStarCount() == progressManager.getContainer().getStripesAtLevelCount()) {
                this.levelIsComplete = true;
            }

            if (this.userProgress.getStarCount() == progressManager.getContainer().getMaxStarCount()) {
                this.gameIsComplete = true;
            }
        }
    }

    private DialogItem gameIsFinishedDialog() {
        return new DialogItem(phraseManager.getValueByKey(GAME_FINISHED_PHRASE), true);
    }

    private DialogItem startNewMissionDialog() {

        Activities nextActivity = progressManager.getNextActivity(this.currentActivity, this.currentLevel);

        boolean invalidCondition = this.userProgress.getFinishedRounds().contains(nextActivity.name());

        while (invalidCondition) {
            nextActivity = progressManager.getNextActivity(nextActivity, this.currentLevel);
            invalidCondition = this.userProgress.getFinishedRounds().contains(nextActivity.getTitle());
        }

        currentActivity = nextActivity;
        this.userProgress.setLastActivity(nextActivity.name());

        if (this.activityProgress.isJustStripeUp()) {
            return getCongratulationDialog(this.userProgress.getStripeCount());
        }
        else {
            return getIntroDialog(nextActivity, this.userProgress.getStripeCount());
        }
    }

    private DialogItem getCongratulationDialog(int number) {

        String congrats = phraseManager.getValueByKey(CONGRATULATION_PHRASE);

        StringBuilder dialog = new StringBuilder(congrats);
        dialog.append(" ");

        Speech speech = levelManager.getSpeechForActivityByStripeNumber(Activities.SUSHI_SLICE, number);

        for (String partOfSpeech : speech.getIntro()) {
            dialog.append(partOfSpeech);
            dialog.append(" ");
        }

        if (speech.isShouldRunDemo()) {
            this.statePhase = DEMO;
        }
        else {
            this.statePhase = READY_PHASE;
        }

        if (this.statePhase == READY_PHASE && isAvailablePowerUpsForActivity()) {
            dialog = appendEquipment(dialog);
        }

        return new DialogItem(dialog.toString(), false, actionSlotName);
    }

    private DialogItem getIntroDialog(Activities activity, int number) {

        Speech speech = levelManager.getSpeechForActivityByStripeNumber(activity, number);

        StringBuilder dialog = new StringBuilder();

        for (String partOfSpeech : speech.getIntro()) {
            dialog.append(partOfSpeech);
            dialog.append(" ");
        }

        if (speech.isShouldRunDemo()) {
            this.statePhase = DEMO;
            dialog = appendShouldRunDemo(dialog);
        }
        else if (isAvailablePowerUpsForActivity()) {
            this.statePhase = EQUIPMENT_PHASE;
            dialog = appendEquipment(dialog);
        }
        else {
            this.statePhase = READY_PHASE;
            dialog = appendReadyToStart(dialog);
        }

        return new DialogItem(dialog.toString(), false, actionSlotName);
    }

    private StringBuilder appendShouldRunDemo(StringBuilder dialog) {

        Speech speech = levelManager.getSpeechForActivityByStripeNumber(this.currentActivity, this.userProgress.getStripeCount());

        dialog.append(" ").append(speech.getShouldRunDemoPhrase());

        return dialog;
    }

    private StringBuilder appendReadyToStart(StringBuilder dialog) {

        Speech speech = levelManager.getSpeechForActivityByStripeNumber(this.currentActivity, this.userProgress.getStripeCount());

        dialog.append(" ").append(speech.getReadyToStartPhrase());

        return dialog;
    }

    private boolean isAvailablePowerUpsForActivity() {
        if (this.userProgress.getEarnedPowerUps().isEmpty()) {
            return false;
        }
        Set<String> filteredEquipment = ActivityEquipmentFilter.filterAllAvailableForActivity(this.userProgress.getEarnedPowerUps(), this.currentActivity);
        return !filteredEquipment.isEmpty();
    }

    private StringBuilder appendEquipment(StringBuilder dialog) {

        Speech speech = levelManager.getSpeechForActivityByStripeNumber(this.currentActivity, this.userProgress.getStripeCount());

        dialog.append(" ").append(speech.getListOfEquipmentPhrase());

        Set<String> filteredEquipment = ActivityEquipmentFilter.filterAllAvailableForActivity(this.userProgress.getEarnedPowerUps(), this.currentActivity);

        for (String powerUp : filteredEquipment) {
            String description = powerUpsManager.getValueByKey(powerUp);
            dialog.append(description).append(" ");
        }

        dialog.append(". ").append(speech.getWantWearEquipmentPhrase());

        return dialog;
    }

    DialogItem getWinDialog() {
        this.userProgress.removePowerUp();
        this.statePhase = WIN;
        return new DialogItem(phraseManager.getValueByKey(WON_PHRASE), false, actionSlotName, true, phraseManager.getValueByKey(WON_REPROMPT_PHRASE));
    }

    private DialogItem getDemoDialog(Activities activity, int number) {

        Speech speech = levelManager.getSpeechForActivityByStripeNumber(activity, number);

        StringBuilder dialog = new StringBuilder();

        for (String partOfSpeech : speech.getDemo()) {
            dialog.append(partOfSpeech);
            dialog.append(" ");
        }

        if (isAvailablePowerUpsForActivity()) {
            this.statePhase = EQUIPMENT_PHASE;
            dialog = appendEquipment(dialog);
        }
        else {
            this.statePhase = READY_PHASE;
            dialog = appendReadyToStart(dialog);
        }

        return new DialogItem(dialog.toString(), false, actionSlotName, true, phraseManager.getValueByKey(activity.getTitle() + DEMO_REPROMPT_PHRASE));
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

    private DialogItem wearEquipmentDialog() {

        String equipment = getEquipmentFromUserRequest();

        String readyToStartPhrase = phraseManager.getValueByKey(READY_TO_START_PHRASE);

        if (equipment == null) {
            this.statePhase = READY_PHASE;
            return new DialogItem(readyToStartPhrase, false, actionSlotName, true, phraseManager.getValueByKey(READY_TO_START_REPROMPT_PHRASE));
        }

        this.userProgress.equipPowerUp(equipment);

        this.statePhase = READY_PHASE;

        String justWear = phraseManager.getValueByKey(JUST_WEAR_PHRASE);

        return new DialogItem(justWear + " " + powerUpsManager.getValueByKey(equipment) + ". " + readyToStartPhrase, false, actionSlotName, true, phraseManager.getValueByKey(READY_TO_START_REPROMPT_PHRASE));
    }

    private String getEquipmentFromUserRequest() {
        if (UserReplyComparator.compare(userReply, UserReplies.CHEF_HAT)) {
            return Equipments.CHEF_HAT.name();
        }
        else if (UserReplyComparator.compare(userReply, UserReplies.SECRET_SAUCE)) {
            return Equipments.SECRET_SAUCE.name();
        }
        else {
            return null;
        }
    }

    private DialogItem getWearEquipmentDialog() {

        Speech speech = levelManager.getSpeechForActivityByStripeNumber(this.currentActivity, this.userProgress.getStripeCount());

        StringBuilder dialog = new StringBuilder();

        dialog.append(" ").append(speech.getListOfEquipmentPhrase());

        Set<String> filteredEquipment = ActivityEquipmentFilter.filterAllAvailableForActivity(this.userProgress.getEarnedPowerUps(), this.currentActivity);

        for (String powerUp : filteredEquipment) {
            String description = powerUpsManager.getValueByKey(powerUp);
            dialog.append(description).append(" ");
        }

        dialog.append(speech.getWantWearEquipmentPhrase());

        this.statePhase = EQUIPMENT_PHASE;

        return new DialogItem(dialog.toString(), false, actionSlotName, true, dialog.toString());
    }

    private DialogItem getReadyToStartDialog() {
        this.statePhase = READY_PHASE;
        return new DialogItem(phraseManager.getValueByKey(READY_TO_START_PHRASE), false, actionSlotName, true, phraseManager.getValueByKey(READY_TO_START_REPROMPT_PHRASE));
    }

    private String nextIngredient() {

        IngredientReaction nextIngredient = levelManager.getNextIngredient(this.stripe, this.activityProgress.getPreviousIngredient());

        this.activityProgress.setCurrentIngredientReaction(nextIngredient.getUserReply());
        this.activityProgress.setPreviousIngredient(nextIngredient.getIngredient());

        return nextIngredient.getIngredient();
    }
}
