package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseSessionStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.components.UserReplyComparator;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivitiesManager;
import com.muffinsoft.alexa.skills.samuraichef.content.LevelManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;
import com.muffinsoft.alexa.skills.samuraichef.models.IngredientReaction;
import com.muffinsoft.alexa.skills.samuraichef.models.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.muffinsoft.alexa.sdk.content.BaseConstants.USERNAME_PLACEHOLDER;
import static com.muffinsoft.alexa.sdk.model.SlotName.ACTION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.DEMO_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.DEMO_PHRASE_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.DEMO_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.FAILURE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.FAILURE_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.INTRO_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.INTRO_PHRASE_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.READY_TO_START_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.READY_TO_START_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WON_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WON_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.FINISHED_ROUNDS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INGREDIENT_REACTION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.LEVEL_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.MISTAKES_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.PREVIOUS_INGREDIENT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STRIPE_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.SUCCESS_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.DEMO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.INTRO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.LOSE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_0;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_1;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.WIN;

abstract class BaseSamuraiChefSessionStateManager extends BaseSessionStateManager {

    private static final Logger logger = LoggerFactory.getLogger(BaseSessionStateManager.class);
    protected final PhraseManager phraseManager;
    final ActivitiesManager activitiesManager;
    protected final LevelManager levelManager;
    Activities currentActivity;
    StatePhase statePhase;
    String currentIngredientReaction;
    int successCount;
    int mistakesCount;
    private String previousIngredient;
    private Set<String> finishedRounds;
    private int stripeCount;
    int currentLevel;
    Level level;

    BaseSamuraiChefSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivitiesManager activitiesManager, LevelManager levelManager) {
        super(slots, attributesManager);
        this.phraseManager = phraseManager;
        this.activitiesManager = activitiesManager;
        this.levelManager = levelManager;
    }

    @Override
    protected void populateActivityVariables() {
        //noinspection unchecked
        Collection<String> rounds = (Collection<String>) sessionAttributes.get(FINISHED_ROUNDS);
        finishedRounds = rounds == null ? new HashSet<>() : new HashSet<>(rounds);
        previousIngredient = String.valueOf(sessionAttributes.get(PREVIOUS_INGREDIENT));
        statePhase = StatePhase.valueOf(String.valueOf(sessionAttributes.getOrDefault(STATE_PHASE, INTRO)));
        successCount = (int) sessionAttributes.getOrDefault(SUCCESS_COUNT, 0);
        mistakesCount = (int) sessionAttributes.getOrDefault(MISTAKES_COUNT, 0);
        stripeCount = (int) sessionAttributes.getOrDefault(STRIPE_COUNT, 0);
        currentLevel = (int) sessionAttributes.getOrDefault(LEVEL_COUNT, 0);
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
        sessionAttributes.put(LEVEL_COUNT, level);
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

            dialog = getIntroDialog(this.currentActivity);
        }

        else if (this.statePhase == DEMO) {

            if (UserReplyComparator.compare(userReply, UserReplies.NO)) {
                dialog = getReadyToStartDialog();
            }
            else {
                dialog = getDemoDialog(this.currentActivity);
            }
        }

        else if (this.statePhase == PHASE_0) {

            String speechText = nextIngredient();
            this.statePhase = PHASE_1;
            dialog = new DialogItem(speechText, false, ACTION.text);
        }

        else if (this.statePhase == LOSE) {
            if (UserReplyComparator.compare(userReply, UserReplies.AGAIN)) {
                resetRoundProgress();
                dialog = getIntroDialog(this.currentActivity);
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
            calculateProgress();
            resetRoundProgress();
            dialog = startNewMission();
        }

        else {

            dialog = getActivePhaseDialog();
        }

        String responseText = dialog.getResponseText().replace(USERNAME_PLACEHOLDER, userName);
        dialog.setResponseText(responseText);

        return dialog;
    }

    private void calculateProgress() {
        finishedRounds.add(this.currentActivity.name());
        if (finishedRounds.size() == Activities.values().length - 2) {
            this.stripeCount += 1;
            this.finishedRounds = new HashSet<>();
        }
    }

    private DialogItem startNewMission() {
        Activities nextActivity = activitiesManager.getNextActivity(this.currentActivity);
        sessionAttributes.put(ACTIVITY, nextActivity);
        return getIntroDialog(nextActivity);
    }

    private DialogItem getIntroDialog(Activities activity) {

        String countKey = activity.getTitle() + INTRO_PHRASE_COUNT;

        String countValue = phraseManager.getValueByKey(countKey);

        int count = Integer.parseInt(countValue);

        StringBuilder dialog = new StringBuilder();

        for (int i = 0; i < count; i++) {
            dialog.append(phraseManager.getValueByKey(activity.getTitle() + INTRO_PHRASE + i));
            dialog.append(" ");
        }

        this.statePhase = DEMO;

        return new DialogItem(dialog.toString(), false, actionSlotName);
    }

    DialogItem getWinDialog() {
        this.statePhase = WIN;
        return new DialogItem(phraseManager.getValueByKey(WON_PHRASE), false, actionSlotName, true, phraseManager.getValueByKey(WON_REPROMPT_PHRASE));
    }

    private DialogItem getDemoDialog(Activities activity) {

        String countKey = activity.getTitle() + DEMO_PHRASE_COUNT;

        int count = Integer.parseInt(phraseManager.getValueByKey(countKey));

        StringBuilder dialog = new StringBuilder();

        for (int i = 0; i < count; i++) {
            dialog.append(phraseManager.getValueByKey(activity.getTitle() + DEMO_PHRASE + i));
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

    protected DialogItem getFailureDialog() {
        return getFailureDialog("");
    }

    DialogItem getFailureDialog(String speechText) {
        String ingredient = nextIngredient();
        speechText = speechText + " " + ingredient;
        return new DialogItem(speechText, false, actionSlotName);
    }

    DialogItem getLoseRoundDialog() {
        this.statePhase = LOSE;
        return new DialogItem(phraseManager.getValueByKey(FAILURE_PHRASE), false, actionSlotName, true, phraseManager.getValueByKey(FAILURE_REPROMPT_PHRASE));
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
