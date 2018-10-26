package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseSessionStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.components.ActivityEquipmentFilter;
import com.muffinsoft.alexa.skills.samuraichef.components.UserReplyComparator;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivitiesManager;
import com.muffinsoft.alexa.skills.samuraichef.content.LevelManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.RewardManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.Equipments;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;
import com.muffinsoft.alexa.skills.samuraichef.models.ActivityProgress;
import com.muffinsoft.alexa.skills.samuraichef.models.IngredientReaction;
import com.muffinsoft.alexa.skills.samuraichef.models.Level;
import com.muffinsoft.alexa.skills.samuraichef.models.Speech;
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
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.FAILURE_PHRASE_RETRY_ONLY;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.FAILURE_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.FAILURE_REPROMPT_PHRASE_RETRY_ONLY;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.JUST_WEAR_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.READY_TO_START_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.READY_TO_START_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WON_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WON_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.DEBUG_MESSAGE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.DEMO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.EQUIPMENT_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.INTRO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.LOSE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.LOSE_RETRY_ONLY;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_1;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.READY_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.WIN;

abstract class BaseSamuraiChefSessionStateManager extends BaseSessionStateManager {

    private static final Logger logger = LoggerFactory.getLogger(BaseSessionStateManager.class);

    protected final PhraseManager phraseManager;
    protected final LevelManager levelManager;

    private final PowerUpsManager powerUpsManager;
    private final RewardManager rewardManager;
    private final ActivitiesManager activitiesManager;

    protected Activities currentActivity;
    protected StatePhase statePhase;
    protected Level level;
    protected UserProgress userProgress;
    protected ActivityProgress activityProgress;

    BaseSamuraiChefSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivitiesManager activitiesManager, LevelManager levelManager, PowerUpsManager powerUpsManager, RewardManager rewardManager) {
        super(slots, attributesManager);
        this.phraseManager = phraseManager;
        this.activitiesManager = activitiesManager;
        this.levelManager = levelManager;
        this.powerUpsManager = powerUpsManager;
        this.rewardManager = rewardManager;
    }

    @Override
    protected void populateActivityVariables() {

        statePhase = StatePhase.valueOf(String.valueOf(sessionAttributes.getOrDefault(STATE_PHASE, INTRO)));

        LinkedHashMap rawUserProgress = (LinkedHashMap) sessionAttributes.get(USER_PROGRESS);
        userProgress = rawUserProgress != null ? mapper.convertValue(rawUserProgress, UserProgress.class) : new UserProgress();

        LinkedHashMap rawActivityProgress = (LinkedHashMap) sessionAttributes.get(ACTIVITY_PROGRESS);
        activityProgress = rawActivityProgress != null ? mapper.convertValue(rawActivityProgress, ActivityProgress.class) : new ActivityProgress();

        logger.debug("Session attributes on the start of handling: " + this.sessionAttributes.toString());
    }

    @Override
    protected void initializeSessionAttributes() {
        this.sessionAttributes = new HashMap<>();
    }

//    @Override
//    protected void updatePersistentAttributes() {
//        persistentAttributes.put(USER_PROGRESS, this.userProgress);
//    }

    @Override
    protected void updateSessionAttributes() {
        sessionAttributes.put(USER_PROGRESS, this.userProgress);
        sessionAttributes.put(ACTIVITY_PROGRESS, this.activityProgress);
        sessionAttributes.put(STATE_PHASE, statePhase);
        sessionAttributes.put(ACTIVITY, currentActivity);
        if (!debugMessage.toString().isEmpty()) {
            sessionAttributes.put(DEBUG_MESSAGE, debugMessage.toString());
        }
        logger.debug("Session attributes on the end of handling: " + this.sessionAttributes.toString());
    }

    protected void resetActivityProgress() {
        this.statePhase = INTRO;
        this.activityProgress.reset();
    }

    protected abstract DialogItem getActivePhaseDialog();

    @Override
    public DialogItem nextResponse() {

        DialogItem dialog;

        level = levelManager.getLevelForActivity(this.currentActivity, this.userProgress.getCurrentLevel());

        if (this.statePhase == INTRO) {

            dialog = getIntroDialog(this.currentActivity, this.userProgress.getCurrentLevel());
        }

        else if (this.statePhase == DEMO) {

            if (UserReplyComparator.compare(userReply, UserReplies.NO)) {
                if (isAvailablePowerUpsForActivity()) {
                    dialog = getWearEquipmentDialog();
                }
                else {
                    dialog = getReadyToStartDialog();
                }
            }
            else {
                dialog = getDemoDialog(this.currentActivity, this.userProgress.getCurrentLevel());
            }
        }

        else if (this.statePhase == EQUIPMENT_PHASE) {

            if (UserReplyComparator.compare(userReply, UserReplies.NO)) {
                dialog = getReadyToStartDialog();
            }
            else {
                dialog = wearEquipmentDialog();
            }
        }

        else if (this.statePhase == READY_PHASE) {

            String speechText = nextIngredient();
            this.statePhase = PHASE_1;
            dialog = new DialogItem(speechText, false, ACTION.text);
        }

        else if (this.statePhase == LOSE || this.statePhase == LOSE_RETRY_ONLY) {
            resetWinInARow();
            if (UserReplyComparator.compare(userReply, UserReplies.AGAIN)) {
                resetActivityProgress();
                dialog = getIntroDialog(this.currentActivity, this.userProgress.getCurrentLevel());
            }
            else if (this.statePhase == LOSE && UserReplyComparator.compare(userReply, UserReplies.MISSION)) {
                resetActivityProgress();
                dialog = startNewMissionDialog();
            }
            else {
                dialog = getLoseRoundDialog();
            }
        }

        else if (this.statePhase == WIN) {
            addWinInARow();
            calculateLevelProgress();
            calculatePowerUpsProgress();
            resetActivityProgress();
            dialog = startNewMissionDialog();
        }

        else {

            dialog = getActivePhaseDialog();
        }

        dialog.setResponseText(dialog.getResponseText());

        return dialog;
    }

    private void calculatePowerUpsProgress() {

        int winInARowCount = rewardManager.getContainer().getWinInARowCount();

        if (this.userProgress.getWinInARowCount() % winInARowCount == 0) {
            Equipments equipment = powerUpsManager.getNextRandomItem(this.userProgress.getEarnedPowerUps());
            this.userProgress.getEarnedPowerUps().add(equipment.name());
        }
    }

    private void resetWinInARow() {
        this.userProgress.setWinInARowCount(0);
    }

    private void addWinInARow() {
        this.userProgress.increaseWinInARow();
    }

    private void calculateLevelProgress() {
        this.userProgress.getFinishedRounds().add(this.currentActivity.name());

        if (this.userProgress.getFinishedRounds().size() == Activities.values().length) {

            this.activityProgress.setJustStripeUp(true);
            this.userProgress.iterateStripeCount();
            this.userProgress.resetFinishRounds();

            int stripesToLevelCount = rewardManager.getContainer().getStripesToLevelCount();

            if (this.userProgress.getStripeCount() % stripesToLevelCount == 0) {
                this.userProgress.iterateLevel();
            }

            int stripesToStarCount = rewardManager.getContainer().getStripesToStarCount();

            if (this.userProgress.getStripeCount() % stripesToStarCount == 0) {
                this.userProgress.iterateStarCount();
            }
        }
    }

    private DialogItem startNewMissionDialog() {

        Activities nextActivity = activitiesManager.getNextActivity(this.currentActivity);

        boolean invalidCondition = this.userProgress.getFinishedRounds().contains(nextActivity.name());

        while (invalidCondition) {
            nextActivity = activitiesManager.getNextActivity(nextActivity);
            invalidCondition = this.userProgress.getFinishedRounds().contains(nextActivity.getTitle());
        }

        currentActivity = nextActivity;

        if (this.activityProgress.isJustStripeUp()) {
            return getCongratulationDialog(this.userProgress.getCurrentLevel());
        }
        else {
            return getIntroDialog(nextActivity, this.userProgress.getCurrentLevel());
        }
    }

    private DialogItem getCongratulationDialog(int number) {

        String congrats = phraseManager.getValueByKey(CONGRATULATION_PHRASE);

        StringBuilder dialog = new StringBuilder(congrats);
        dialog.append(" ");

        Speech speech = levelManager.getSpeechForActivityByNumber(Activities.SUSHI_SLICE, number);

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

        Speech speech = levelManager.getSpeechForActivityByNumber(activity, number);

        StringBuilder dialog = new StringBuilder();

        for (String partOfSpeech : speech.getIntro()) {
            dialog.append(partOfSpeech);
            dialog.append(" ");
        }

        if (speech.isShouldRunDemo()) {
            this.statePhase = DEMO;
            dialog = appendDemo(dialog);
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

    private StringBuilder appendDemo(StringBuilder dialog) {

        Speech speech = levelManager.getSpeechForActivityByNumber(this.currentActivity, this.userProgress.getCurrentLevel());

        dialog.append(" ").append(speech.getShouldRunDemoPhrase());

        return dialog;
    }

    private StringBuilder appendReadyToStart(StringBuilder dialog) {

        Speech speech = levelManager.getSpeechForActivityByNumber(this.currentActivity, this.userProgress.getCurrentLevel());

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

        Speech speech = levelManager.getSpeechForActivityByNumber(this.currentActivity, this.userProgress.getCurrentLevel());

        dialog.append(" ").append(speech.getListOfEquipmentPhrase());

        Set<String> filteredEquipment = ActivityEquipmentFilter.filterAllAvailableForActivity(this.userProgress.getEarnedPowerUps(), this.currentActivity);

        for (String powerUp : filteredEquipment) {
            String description = powerUpsManager.getValueByKey(powerUp);
            dialog.append(description).append(" ");
        }

        dialog.append(speech.getWantWearEquipmentPhrase());

        return dialog;
    }

    DialogItem getWinDialog() {
        this.userProgress.removePowerUp();
        this.statePhase = WIN;
        return new DialogItem(phraseManager.getValueByKey(WON_PHRASE), false, actionSlotName, true, phraseManager.getValueByKey(WON_REPROMPT_PHRASE));
    }

    private DialogItem getDemoDialog(Activities activity, int number) {

        Speech speech = levelManager.getSpeechForActivityByNumber(activity, number);

        StringBuilder dialog = new StringBuilder();

        for (String partOfSpeech : speech.getDemo()) {
            dialog.append(partOfSpeech);
            dialog.append(" ");
        }

        this.statePhase = READY_PHASE;
        dialog = appendReadyToStart(dialog);

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

        if (this.userProgress.getFinishedRounds().size() == Activities.values().length - 1) {
            this.statePhase = LOSE_RETRY_ONLY;
            return new DialogItem(phraseManager.getValueByKey(FAILURE_PHRASE_RETRY_ONLY), false, actionSlotName, true, phraseManager.getValueByKey(FAILURE_REPROMPT_PHRASE_RETRY_ONLY));
        }
        else {
            this.statePhase = LOSE;
            return new DialogItem(phraseManager.getValueByKey(FAILURE_PHRASE), false, actionSlotName, true, phraseManager.getValueByKey(FAILURE_REPROMPT_PHRASE));
        }
    }

    private DialogItem wearEquipmentDialog() {

        String equipment = getEquipmentFromUserRequest();

        String readyToStartPhrase = phraseManager.getValueByKey(READY_TO_START_PHRASE);

        if (equipment == null) {
            this.statePhase = READY_PHASE;
            return new DialogItem(readyToStartPhrase, false, actionSlotName, true, phraseManager.getValueByKey(READY_TO_START_REPROMPT_PHRASE));
        }

        this.userProgress.getEarnedPowerUps().remove(equipment);

        this.userProgress.setEquippedPowerUp(equipment);

        this.statePhase = READY_PHASE;

        String justWear = phraseManager.getValueByKey(JUST_WEAR_PHRASE);

        return new DialogItem(justWear + " " + powerUpsManager.getValueByKey(equipment) + " " + readyToStartPhrase, false, actionSlotName, true, phraseManager.getValueByKey(READY_TO_START_REPROMPT_PHRASE));
    }

    private String getEquipmentFromUserRequest() {
        if (UserReplyComparator.compare(userReply, UserReplies.SUSHI_BLADE)) {
            return Equipments.SUSHI_BLADE.name();
        }
        else if (UserReplyComparator.compare(userReply, UserReplies.CHEF_HAT)) {
            return Equipments.CHEF_HAT.name();
        }
        else if (UserReplyComparator.compare(userReply, UserReplies.CUISINE_KATANA)) {
            return Equipments.CUISINE_KATANA.name();
        }
        else if (UserReplyComparator.compare(userReply, UserReplies.SUPER_SPATULE)) {
            return Equipments.SUPER_SPATULE.name();
        }
        else if (UserReplyComparator.compare(userReply, UserReplies.SECRET_SAUCE)) {
            return Equipments.SECRET_SAUCE.name();
        }
        else if (UserReplyComparator.compare(userReply, UserReplies.KARATE_GI)) {
            return Equipments.KARATE_GI.name();
        }
        else if (UserReplyComparator.compare(userReply, UserReplies.HACHIMAKI)) {
            return Equipments.HACHIMAKI.name();
        }
        else if (UserReplyComparator.compare(userReply, UserReplies.SUMO_MAWASHI)) {
            return Equipments.SUMO_MAWASHI.name();
        }
        else {
            return null;
        }
    }

    private DialogItem getWearEquipmentDialog() {

        Speech speech = levelManager.getSpeechForActivityByNumber(this.currentActivity, this.userProgress.getCurrentLevel());

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

        IngredientReaction nextIngredient = levelManager.getNextIngredient(this.level, this.activityProgress.getPreviousIngredient());

        this.activityProgress.setCurrentIngredientReaction(nextIngredient.getUserReply());
        this.activityProgress.setPreviousIngredient(nextIngredient.getIngredient());

        return nextIngredient.getIngredient();
    }
}
