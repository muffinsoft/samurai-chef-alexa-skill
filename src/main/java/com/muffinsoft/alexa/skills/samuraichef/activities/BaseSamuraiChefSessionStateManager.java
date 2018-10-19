package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseSessionStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivitiesManager;
import com.muffinsoft.alexa.skills.samuraichef.content.IngredientsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.muffinsoft.alexa.sdk.content.BaseConstants.USERNAME_PLACEHOLDER;
import static com.muffinsoft.alexa.sdk.model.SlotName.ACTION;
import static com.muffinsoft.alexa.skills.samuraichef.content.SamuraiChefSessionConstants.ACTIVITY;
import static com.muffinsoft.alexa.skills.samuraichef.content.SamuraiChefSessionConstants.FINISHED_ROUNDS;
import static com.muffinsoft.alexa.skills.samuraichef.content.SamuraiChefSessionConstants.INGREDIENT_REACTION;
import static com.muffinsoft.alexa.skills.samuraichef.content.SamuraiChefSessionConstants.MISTAKES_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.content.SamuraiChefSessionConstants.PREVIOUS_INGREDIENT;
import static com.muffinsoft.alexa.skills.samuraichef.content.SamuraiChefSessionConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.content.SamuraiChefSessionConstants.STRIPE_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.content.SamuraiChefSessionConstants.SUCCESS_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.DEMO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.INTRO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.LOSE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_0;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_1;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.WIN;

abstract class BaseSamuraiChefSessionStateManager extends BaseSessionStateManager {

    protected final PhraseManager phraseManager;
    final ActivitiesManager activitiesManager;
    private final IngredientsManager ingredientsManager;
    Activities currentActivity;
    StatePhase statePhase;
    String currentIngredientReaction;
    int successCount;
    int mistakesCount;
    private String previousIngredient;
    private Set<String> finishedRounds;
    private int stripeCount;

    BaseSamuraiChefSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, IngredientsManager ingredientsManager, ActivitiesManager activitiesManager) {
        super(slots, attributesManager);
        this.phraseManager = phraseManager;
        this.ingredientsManager = ingredientsManager;
        this.activitiesManager = activitiesManager;
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
        Object ingredient = sessionAttributes.getOrDefault(INGREDIENT_REACTION, null);
        currentIngredientReaction = ingredient != null ? String.valueOf(ingredient) : null;
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

        if (this.statePhase == INTRO) {

            dialog = getIntroDialog(this.currentActivity);
        }

        else if (this.statePhase == DEMO) {

            if (userReply.equals("no")) {
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
            if (userReply.contains("again")) {
                resetRoundProgress();
                dialog = getIntroDialog(this.currentActivity);
            }
            else if (userReply.contains("mission")) {
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
        if (finishedRounds.size() == Activities.values().length - 1) {
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

        String countKey = activity.getTitle() + "IntroPhraseCount";

        String countValue = phraseManager.getValueByKey(countKey);

        int count = Integer.parseInt(countValue);

        StringBuilder dialog = new StringBuilder();

        for (int i = 0; i < count; i++) {
            dialog.append(phraseManager.getValueByKey(activity.getTitle() + "IntroPhrase" + i));
            dialog.append(" ");
        }

        this.statePhase = DEMO;

        return new DialogItem(dialog.toString(), false, actionSlotName);
    }

    DialogItem getWinDialog() {
        this.statePhase = WIN;
        return new DialogItem(phraseManager.getValueByKey("wonPhrase"), false, actionSlotName, true);
    }

    private DialogItem getDemoDialog(Activities activity) {

        String countKey = activity.getTitle() + "DemoPhraseCount";

        int count = Integer.parseInt(phraseManager.getValueByKey(countKey));

        StringBuilder dialog = new StringBuilder();

        for (int i = 0; i < count; i++) {
            dialog.append(phraseManager.getValueByKey(activity.getTitle() + "DemoPhrase" + i));
            dialog.append(" ");
        }
        this.statePhase = PHASE_0;
        return new DialogItem(dialog.toString(), false, actionSlotName, true);
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
        return new DialogItem(phraseManager.getValueByKey("failurePhrase"), false, actionSlotName, true);
    }

    private DialogItem getReadyToStartDialog() {
        this.statePhase = PHASE_0;
        return new DialogItem(phraseManager.getValueByKey("readyToStart"), false, actionSlotName, true);
    }

    private String nextIngredient() {

        String nextIngredient = ingredientsManager.getNextIngredient(this.currentActivity, this.previousIngredient);
        sessionAttributes.put(PREVIOUS_INGREDIENT, nextIngredient);

        Map<String, String> valueByKey = ingredientsManager.getValueByKey(currentActivity.name());
        String reaction = valueByKey.get(nextIngredient);
        sessionAttributes.put(INGREDIENT_REACTION, reaction);

        return nextIngredient + "!";
    }
}
