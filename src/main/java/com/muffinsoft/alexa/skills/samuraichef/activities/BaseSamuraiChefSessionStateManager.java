package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseSessionStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.SlotName;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivitiesManager;
import com.muffinsoft.alexa.skills.samuraichef.content.IngredientsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

import static com.muffinsoft.alexa.sdk.content.BaseConstants.USERNAME_PLACEHOLDER;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.ACTIVITY;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.FAILURE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.INGREDIENT_REACTION;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.MISTAKES_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.PREVIOUS_INGREDIENT;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.ROUND_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.STAR_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.STRIPE_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.SUCCESS_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.DEMO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.INTRO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.LOSE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_0;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_1;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.WIN;

abstract class BaseSamuraiChefSessionStateManager extends BaseSessionStateManager {

    protected final PhraseManager phraseManager;
    protected final ActivitiesManager activitiesManager;
    private final IngredientsManager ingredientsManager;

    protected String currentIngredientReaction;
    protected String previousIngredient;
    protected StatePhase statePhase;
    protected int successCount;
    protected int mistakesCount;
    protected int roundCount;
    protected int stripeCount;
    protected int starCount;

    protected Activities currentActivity;


    BaseSamuraiChefSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, IngredientsManager ingredientsManager, ActivitiesManager activitiesManager) {
        super(slots, attributesManager);
        this.phraseManager = phraseManager;
        this.ingredientsManager = ingredientsManager;
        this.activitiesManager = activitiesManager;
    }

    @Override
    protected void populateActivityVariables() {
        previousIngredient = String.valueOf(sessionAttributes.get(PREVIOUS_INGREDIENT));
        statePhase = StatePhase.valueOf(String.valueOf(sessionAttributes.getOrDefault(STATE_PHASE, INTRO)));
        successCount = (int) sessionAttributes.getOrDefault(SUCCESS_COUNT, 0);
        mistakesCount = (int) sessionAttributes.getOrDefault(MISTAKES_COUNT, 0);
        starCount = (int) sessionAttributes.getOrDefault(STAR_COUNT, 0);
        roundCount = (int) sessionAttributes.getOrDefault(ROUND_COUNT, 0);
        stripeCount = (int) sessionAttributes.getOrDefault(STRIPE_COUNT, 0);
        Object ingredient = sessionAttributes.getOrDefault(INGREDIENT_REACTION, null);
        currentIngredientReaction = ingredient != null ? String.valueOf(ingredient) : null;
    }

    @Override
    protected void initializeSessionAttributes() {
        sessionAttributes = new HashMap<>();
        sessionAttributes.put(STATE_PHASE, StatePhase.INTRO);
        sessionAttributes.put(MISTAKES_COUNT, 0);
        sessionAttributes.put(SUCCESS_COUNT, 0);
        sessionAttributes.put(PREVIOUS_INGREDIENT, new LinkedList<String>());
    }

    @Override
    protected void updateSessionAttributes() {
        sessionAttributes.put(MISTAKES_COUNT, mistakesCount);
        sessionAttributes.put(SUCCESS_COUNT, successCount);
        sessionAttributes.put(STATE_PHASE, statePhase);
        sessionAttributes.put(ROUND_COUNT, roundCount);
        sessionAttributes.put(STRIPE_COUNT, stripeCount);
        sessionAttributes.put(STAR_COUNT, starCount);
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
            dialog = new DialogItem(speechText, false, SlotName.ACTION.text);
        }

        else if (this.statePhase == LOSE) {
            if (Objects.equals("again", userReply)) {
                resetRoundProgress();
                dialog = getIntroDialog(this.currentActivity);
            }
            else if (Objects.equals("mission", userReply)) {
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

    protected abstract void calculateProgress();

    protected DialogItem startNewMission() {
        Activities nextActivity = activitiesManager.getNextActivity(Activities.NAME_HANDLER);
        sessionAttributes.put(ACTIVITY, nextActivity);
        return getIntroDialog(nextActivity);
    }

    protected void resetRoundProgress() {
        this.statePhase = INTRO;
        this.mistakesCount = 0;
        this.successCount = 0;
        this.previousIngredient = null;
        this.currentIngredientReaction = null;
    }

    protected DialogItem getIntroDialog(Activities activity) {

        String countKey = activity.getTitle() + "IntroPhraseCount";

        String countValue = phraseManager.getValueByKey(countKey);

        int count = Integer.parseInt(countValue);

        StringBuilder dialog = new StringBuilder();

        for (int i = 0; i < count; i++) {
            dialog.append(phraseManager.getValueByKey(activity.getTitle() + "IntroPhrase" + i));
            dialog.append(" ");
        }

        this.statePhase = StatePhase.DEMO;

        return new DialogItem(dialog.toString(), false, actionSlotName);
    }

    protected DialogItem getWinDialog() {
        this.statePhase = WIN;
        return new DialogItem(phraseManager.getValueByKey("wonPhrase"), true, actionSlotName, true);
    }

    protected DialogItem getDemoDialog(Activities activity) {

        String countKey = activity.getTitle() + "DemoPhraseCount";

        int count = Integer.parseInt(phraseManager.getValueByKey(countKey));

        StringBuilder dialog = new StringBuilder();

        for (int i = 0; i < count; i++) {
            dialog.append(phraseManager.getValueByKey(activity.getTitle() + "DemoPhrase" + i));
            dialog.append(" ");
        }
        this.statePhase = StatePhase.PHASE_0;
        return new DialogItem(dialog.toString(), false, actionSlotName, true);
    }

    protected DialogItem getSuccessDialog() {
        return getSuccessDialog("");
    }

    protected DialogItem getSuccessDialog(String speechText) {
        String ingredient = nextIngredient();
        speechText = speechText + " " + ingredient;
        return new DialogItem(speechText, false, actionSlotName);
    }

    protected DialogItem getFailureDialog() {
        return getFailureDialog("");
    }

    protected DialogItem getFailureDialog(String speechText) {
        String ingredient = nextIngredient();
        speechText = speechText + " " + ingredient;
        return new DialogItem(speechText, false, actionSlotName);
    }

    protected DialogItem getLoseRoundDialog() {
        return new DialogItem(phraseManager.getValueByKey(FAILURE_PHRASE), false, actionSlotName);
    }

    protected DialogItem getReadyToStartDialog() {
        this.statePhase = PHASE_0;
        return new DialogItem(phraseManager.getValueByKey("readyToStart"), false, actionSlotName, true);
    }

    protected String nextIngredient() {
        return nextIngredient("");
    }

    protected String nextIngredient(String speechText) {

        String nextIngredient = ingredientsManager.getNextIngredient(this.currentActivity, this.previousIngredient);
        sessionAttributes.put(PREVIOUS_INGREDIENT, nextIngredient);

        Map<String, String> valueByKey = ingredientsManager.getValueByKey(currentActivity.name());
        String reaction = valueByKey.get(nextIngredient);
        sessionAttributes.put(INGREDIENT_REACTION, reaction);

        return speechText + " " + nextIngredient + "!";
    }
}
