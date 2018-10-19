package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseSessionStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.SlotName;
import com.muffinsoft.alexa.skills.samuraichef.content.IngredientsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static com.muffinsoft.alexa.sdk.content.BaseConstants.USERNAME_PLACEHOLDER;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.FAILURE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.INGREDIENT_REACTION;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.MISTAKES_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.PREVIOUS_INGREDIENT;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.SUCCESS_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.DEMO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.INTRO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_0;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_1;

abstract class BaseSamuraiChefSessionStateManager extends BaseSessionStateManager {

    protected final PhraseManager phraseManager;
    private final IngredientsManager ingredientsManager;

    protected String currentIngredientReaction;
    protected String previousIngredient;
    protected StatePhase statePhase;
    protected int successCount;
    protected int mistakesCount;

    protected Activities currentActivity;


    BaseSamuraiChefSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, IngredientsManager ingredientsManager) {
        super(slots, attributesManager);
        this.phraseManager = phraseManager;
        this.ingredientsManager = ingredientsManager;
    }

    @Override
    protected void initializeSessionAttributes() {
        sessionAttributes = new HashMap<>();
        sessionAttributes.put(STATE_PHASE, StatePhase.INTRO);
        sessionAttributes.put(MISTAKES_COUNT, 0);
        sessionAttributes.put(SUCCESS_COUNT, 0);
        sessionAttributes.put(PREVIOUS_INGREDIENT, new LinkedList<String>());
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
        else {
            dialog = getActivePhaseDialog();
        }

        String responseText = dialog.getResponseText().replace(USERNAME_PLACEHOLDER, userName);
        dialog.setResponseText(responseText);

        return dialog;
    }

    protected DialogItem getIntroDialog(Activities activity) {

        String countKey = activity.getTitle() + "IntroPhraseCount";

        String countValue = phraseManager.getValueByKey(countKey);

        int count = Integer.parseInt(countValue);

        StringBuilder dialog = new StringBuilder();

        for (int i = 4; i < count; i++) {
//        for (int i = 0; i < count; i++) {
            dialog.append(phraseManager.getValueByKey(activity.getTitle() + "IntroPhrase" + i));
            dialog.append(" ");
        }

        this.statePhase = StatePhase.DEMO;

        return new DialogItem(dialog.toString(), false, actionSlotName);
    }

    protected DialogItem getWinDialog() {
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
