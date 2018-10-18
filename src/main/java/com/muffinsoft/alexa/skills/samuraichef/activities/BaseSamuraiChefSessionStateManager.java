package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseSessionStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.IngredientsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.FAILURE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.INGREDIENT_REACTION;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.MISTAKES_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.PREVIOUS_INGREDIENTS;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.SUCCESS_COUNT;

abstract class BaseSamuraiChefSessionStateManager extends BaseSessionStateManager {

    protected final PhraseManager phraseManager;
    private final IngredientsManager ingredientsManager;

    String currentIngredientReaction;
    String userName;
    LinkedList<String> previousIngredients;
    StatePhase statePhase;
    int successCount;
    int mistakesCount;

    Activities currentActivity;


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
        sessionAttributes.put(PREVIOUS_INGREDIENTS, new LinkedList<String>());
    }

    DialogItem getIntroDialog(Activities activity) {

        String countKey = activity.getTitle() + "IntroPhraseCount";

        String countValue = phraseManager.getValueByKey(countKey);

        int count = Integer.parseInt(countValue);

        StringBuilder dialog = new StringBuilder();


        for (int i = 0; i < count; i++) {
            dialog.append(phraseManager.getValueByKey(activity.getTitle() + "IntroPhrase" + i));
        }

        this.statePhase = StatePhase.DEMO;

        return new DialogItem(dialog.toString(), false, actionSlotName, true);
    }

    DialogItem getWinDialog() {
        return new DialogItem(phraseManager.getValueByKey("wonPhrase"), true, actionSlotName, true);
    }

    DialogItem getDemoDialog(Activities activity) {

        String countKey = activity.getTitle() + "DemoPhraseCount";

        int count = Integer.parseInt(phraseManager.getValueByKey(countKey));

        StringBuilder dialog = new StringBuilder();

        for (int i = 0; i < count; i++) {
            dialog.append(phraseManager.getValueByKey(activity.getTitle() + "DemoPhrase" + i));
        }
        this.statePhase = StatePhase.PHASE_1;
        return new DialogItem(dialog.toString(), false, actionSlotName, true);
    }

    DialogItem getSuccessDialog() {
        String speechText = "<emphasis level=\"reduced\">";
        this.successCount++;
        speechText = nextIngredient(speechText);
        speechText += "</emphasis>";
        return new DialogItem(speechText, false, actionSlotName);
    }

    DialogItem getFailureDialog(String speechText) {
        if (speechText == null) {
            speechText = "<emphasis level=\"reduced\">Wrong!";
        }
        this.mistakesCount++;
        if (this.mistakesCount < 3) {
            speechText = nextIngredient(speechText);
            speechText += "</emphasis>";
            return new DialogItem(speechText, false, actionSlotName);
        }
        else {
            return new DialogItem(phraseManager.getValueByKey(FAILURE_PHRASE), false, actionSlotName);
        }
    }

    private String nextIngredient(String speechText) {
        String nextIngredient = ingredientsManager.getNextIngredient(this.currentActivity, this.previousIngredients);
        this.previousIngredients.addFirst(nextIngredient);
        if (this.previousIngredients.size() > 2) {
            this.previousIngredients.removeLast();
        }
        sessionAttributes.put(PREVIOUS_INGREDIENTS, this.previousIngredients);
        sessionAttributes.put(INGREDIENT_REACTION, ingredientsManager.getValueByKey(nextIngredient));
        return speechText + " " + nextIngredient + "!";
    }
}
