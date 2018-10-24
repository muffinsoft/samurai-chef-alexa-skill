package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseSessionStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.components.UserReplyComparator;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivitiesManager;
import com.muffinsoft.alexa.skills.samuraichef.content.LevelManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;
import com.muffinsoft.alexa.skills.samuraichef.models.IngredientReaction;
import com.muffinsoft.alexa.skills.samuraichef.models.Level;
import com.muffinsoft.alexa.skills.samuraichef.models.Speech;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.muffinsoft.alexa.sdk.model.SlotName.ACTION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.CONGRATULATION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.DEMO_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.FAILURE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.FAILURE_PHRASE_RETRY_ONLY;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.FAILURE_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.FAILURE_REPROMPT_PHRASE_RETRY_ONLY;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.READY_TO_START_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.READY_TO_START_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WON_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WON_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.FINISHED_ROUNDS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INGREDIENT_REACTION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.LEVEL_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.MISTAKES_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.POWER_UPS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.PREVIOUS_INGREDIENT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STAR_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STRIPE_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.SUCCESS_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.WIN_IN_A_ROW_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.DEMO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.INTRO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.LOSE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_0;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_1;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.WIN;

abstract class BaseSamuraiChefSessionStateManager extends BaseSessionStateManager {

    private static final Logger logger = LoggerFactory.getLogger(BaseSessionStateManager.class);
    protected final PhraseManager phraseManager;
    protected final PowerUpsManager powerUpsManager;
    protected final LevelManager levelManager;
    final ActivitiesManager activitiesManager;
    Activities currentActivity;
    StatePhase statePhase;
    String currentIngredientReaction;
    int successCount;
    int mistakesCount;
    int currentLevel;
    Level level;
    private String previousIngredient;
    private Set<String> finishedRounds;
    private Set<String> earnedPowerUps;
    private int stripeCount;
    private int starCount;
    private int winInARowCount;
    private boolean isJustStripeUp = false;

    BaseSamuraiChefSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivitiesManager activitiesManager, LevelManager levelManager, PowerUpsManager powerUpsManager) {
        super(slots, attributesManager);
        this.phraseManager = phraseManager;
        this.activitiesManager = activitiesManager;
        this.levelManager = levelManager;
        this.powerUpsManager = powerUpsManager;
    }

    @Override
    protected void populateActivityVariables() {
        //noinspection unchecked
        Collection<String> rounds = (Collection<String>) sessionAttributes.get(FINISHED_ROUNDS);
        //noinspection unchecked
        Collection<String> powerUps = (Collection<String>) sessionAttributes.get(POWER_UPS);
        finishedRounds = rounds == null ? new HashSet<>() : new HashSet<>(rounds);
        earnedPowerUps = powerUps == null ? new HashSet<>() : new HashSet<>(powerUps);
        previousIngredient = String.valueOf(sessionAttributes.get(PREVIOUS_INGREDIENT));
        statePhase = StatePhase.valueOf(String.valueOf(sessionAttributes.getOrDefault(STATE_PHASE, INTRO)));
        successCount = (int) sessionAttributes.getOrDefault(SUCCESS_COUNT, 0);
        mistakesCount = (int) sessionAttributes.getOrDefault(MISTAKES_COUNT, 0);
        stripeCount = (int) sessionAttributes.getOrDefault(STRIPE_COUNT, 0);
        starCount = (int) sessionAttributes.getOrDefault(STAR_COUNT, 0);
        currentLevel = (int) sessionAttributes.getOrDefault(LEVEL_COUNT, 0);
        winInARowCount = (int) sessionAttributes.getOrDefault(WIN_IN_A_ROW_COUNT, 0);
        Object ingredient = sessionAttributes.getOrDefault(INGREDIENT_REACTION, null);
        currentIngredientReaction = ingredient != null ? String.valueOf(ingredient) : null;
        logger.debug("Session attributes on the start of handling: " + this.sessionAttributes.toString());
    }

    @Override
    protected void initializeSessionAttributes() {
        this.sessionAttributes = new HashMap<>();
    }

    @Override
    protected void updateSessionAttributes() {
        sessionAttributes.put(MISTAKES_COUNT, mistakesCount);
        sessionAttributes.put(SUCCESS_COUNT, successCount);
        sessionAttributes.put(STATE_PHASE, statePhase);
        sessionAttributes.put(FINISHED_ROUNDS, finishedRounds);
        sessionAttributes.put(STRIPE_COUNT, stripeCount);
        sessionAttributes.put(STAR_COUNT, starCount);
        sessionAttributes.put(LEVEL_COUNT, currentLevel);
        sessionAttributes.put(WIN_IN_A_ROW_COUNT, winInARowCount);
        logger.debug("Session attributes on the end of handling: " + this.sessionAttributes.toString());
    }

    protected void resetRoundProgress() {
        this.statePhase = INTRO;
        this.mistakesCount = 0;
        this.successCount = 0;
        this.previousIngredient = null;
        this.currentIngredientReaction = null;
    }

    protected abstract DialogItem getActivePhaseDialog();

    @Override
    public DialogItem nextResponse() {

        DialogItem dialog;

        level = levelManager.getLevelForActivity(this.currentActivity, currentLevel);

        if (this.statePhase == INTRO) {

            dialog = getIntroDialog(this.currentActivity, currentLevel);
        }

        else if (this.statePhase == DEMO) {

            if (UserReplyComparator.compare(userReply, UserReplies.NO)) {
                dialog = getReadyToStartDialog();
            }
            else {
                dialog = getDemoDialog(this.currentActivity, this.currentLevel);
            }
        }

        else if (this.statePhase == PHASE_0) {

            String speechText = nextIngredient();
            this.statePhase = PHASE_1;
            dialog = new DialogItem(speechText, false, ACTION.text);
        }

        else if (this.statePhase == LOSE) {
            resetWinInARow();
            if (UserReplyComparator.compare(userReply, UserReplies.AGAIN)) {
                resetRoundProgress();
                dialog = getIntroDialog(this.currentActivity, currentLevel);
            }
            else if (UserReplyComparator.compare(userReply, UserReplies.MISSION)) {
                resetRoundProgress();
                dialog = startNewMission();
            }
            else {
                dialog = getLoseRoundDialog();
            }
        }

        else if (this.statePhase == WIN) {
            addWinInARow();
            calculateLevelProgress();
            calculatePowerUpsProgress();
            resetRoundProgress();
            dialog = startNewMission();
        }

        else {

            dialog = getActivePhaseDialog();
        }

//        String responseText = dialog.getResponseText().replace(USERNAME_PLACEHOLDER, userName);
        dialog.setResponseText(dialog.getResponseText());

        return dialog;
    }

    private void calculatePowerUpsProgress() {
        if (this.winInARowCount % 3 == 0) {
            String powerUps = powerUpsManager.getNextRandomForActivity(this.currentActivity);
            this.earnedPowerUps.add(powerUps);
        }
    }

    private void resetWinInARow() {
        this.winInARowCount = 0;
    }

    private void addWinInARow() {
        this.winInARowCount += 1;
    }

    private void calculateLevelProgress() {
        finishedRounds.add(this.currentActivity.name());
        if (finishedRounds.size() == Activities.values().length) {
            this.isJustStripeUp = true;
            this.stripeCount += 1;
            this.finishedRounds = new HashSet<>();
            if (stripeCount % 2 == 0) {
                this.currentLevel += 1;
            }
            if (this.stripeCount % 3 == 0) {
                this.starCount += 1;
            }
        }
    }

    private DialogItem startNewMission() {
        Activities nextActivity = activitiesManager.getNextActivity(this.currentActivity);
        boolean invalidCondition = finishedRounds.contains(nextActivity.name());
        while (invalidCondition) {
            nextActivity = activitiesManager.getNextActivity(nextActivity);
            invalidCondition = finishedRounds.contains(nextActivity.getTitle());
        }
        sessionAttributes.put(ACTIVITY, nextActivity);
        if (isJustStripeUp) {
            return getCongratulationDialog(nextActivity, currentLevel);
        }
        else {
            return getIntroDialog(nextActivity, currentLevel);
        }
    }

    private DialogItem getCongratulationDialog(Activities activity, int number) {

        String congrats = phraseManager.getValueByKey(CONGRATULATION_PHRASE);

        StringBuilder dialog = new StringBuilder(congrats);

        Speech speech = levelManager.getSpeechForActivityByNumber(activity, number);

        for (String partOfSpeech : speech.getIntro()) {
            dialog.append(partOfSpeech);
            dialog.append(" ");
        }

        if (speech.isShouldRunDemo()) {
            this.statePhase = DEMO;
        }
        else {
            this.statePhase = PHASE_0;
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
        }
        else {
            this.statePhase = PHASE_0;
        }

        return new DialogItem(dialog.toString(), false, actionSlotName);
    }

    DialogItem getWinDialog() {
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

        this.statePhase = PHASE_0;

        return new DialogItem(dialog.toString(), false, actionSlotName, true, phraseManager.getValueByKey(activity.getTitle() + DEMO_REPROMPT_PHRASE));
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
        this.statePhase = LOSE;
        if (this.finishedRounds.size() == Activities.values().length - 1) {
            return new DialogItem(phraseManager.getValueByKey(FAILURE_PHRASE_RETRY_ONLY), false, actionSlotName, true, phraseManager.getValueByKey(FAILURE_REPROMPT_PHRASE_RETRY_ONLY));
        }
        else {
            return new DialogItem(phraseManager.getValueByKey(FAILURE_PHRASE), false, actionSlotName, true, phraseManager.getValueByKey(FAILURE_REPROMPT_PHRASE));
        }
    }

    private DialogItem getReadyToStartDialog() {
        this.statePhase = PHASE_0;
        return new DialogItem(phraseManager.getValueByKey(READY_TO_START_PHRASE), false, actionSlotName, true, phraseManager.getValueByKey(READY_TO_START_REPROMPT_PHRASE));
    }

    private String nextIngredient() {

        IngredientReaction nextIngredient = levelManager.getNextIngredient(this.level, this.previousIngredient);

        sessionAttributes.put(PREVIOUS_INGREDIENT, nextIngredient.getIngredient());
        sessionAttributes.put(INGREDIENT_REACTION, nextIngredient.getUserReply());

        return nextIngredient.getIngredient();
    }
}
