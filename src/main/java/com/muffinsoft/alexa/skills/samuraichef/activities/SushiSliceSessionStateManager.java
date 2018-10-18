package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.SlotName;
import com.muffinsoft.alexa.skills.samuraichef.content.IngredientsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;

import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.INGREDIENT_REACTION;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.MISTAKES_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.PREVIOUS_INGREDIENTS;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.QUESTION_TIME;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.SUCCESS_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.USERNAME;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.USERNAME_PLACEHOLDER;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.DEMO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.INTRO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_0;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_1;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_2;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.WIN;

public class SushiSliceSessionStateManager extends BaseSamuraiChefSessionStateManager {

    public SushiSliceSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, IngredientsManager ingredientsManager) {
        super(slots, attributesManager, phraseManager, ingredientsManager);
        currentActivity = Activities.SUSHI_SLICE;
    }

    @Override
    protected void populateActivityVariables() {
        //noinspection unchecked
        previousIngredients = (LinkedList<String>) sessionAttributes.getOrDefault(PREVIOUS_INGREDIENTS, new LinkedList<String>());
        statePhase = StatePhase.valueOf(String.valueOf(sessionAttributes.getOrDefault(STATE_PHASE, INTRO)));
        successCount = (int) sessionAttributes.getOrDefault(SUCCESS_COUNT, 0);
        mistakesCount = (int) sessionAttributes.getOrDefault(MISTAKES_COUNT, 0);
        userName = String.valueOf(sessionAttributes.get(USERNAME));
        Object ingredient = sessionAttributes.getOrDefault(INGREDIENT_REACTION, null);
        currentIngredientReaction = ingredient != null ? String.valueOf(ingredient) : null;
    }

    @Override
    protected void updateSessionAttributes() {
        sessionAttributes.put(MISTAKES_COUNT, mistakesCount);
        sessionAttributes.put(SUCCESS_COUNT, successCount);
        sessionAttributes.put(STATE_PHASE, statePhase);
        sessionAttributes.put(QUESTION_TIME, System.currentTimeMillis());
    }

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

            if (Objects.equals(currentIngredientReaction, userReply)) {
                dialog = getSuccessDialog();
            }
            else {
                dialog = getFailureDialog(null);
            }

            if (this.successCount == 5) {
                this.statePhase = PHASE_2;
            }

            if (this.successCount == 10) {
                this.statePhase = WIN;
                dialog = getWinDialog();
            }
        }

        String responseText = dialog.getResponseText().replace(USERNAME_PLACEHOLDER, userName);
        dialog.setResponseText(responseText);

        return dialog;
    }

    private DialogItem getReadyToStartDialog() {
        this.statePhase = PHASE_0;
        return new DialogItem(phraseManager.getValueByKey("readyToStart"), false, actionSlotName, true);
    }
}
