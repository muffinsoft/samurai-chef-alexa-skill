package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.IngredientsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;

import java.util.Map;
import java.util.Objects;

import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.INGREDIENT_REACTION;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.MISTAKES_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.PREVIOUS_INGREDIENT;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.SUCCESS_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.INTRO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_2;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.WIN;

public class SushiSliceSessionStateManager extends BaseSamuraiChefSessionStateManager {

    public SushiSliceSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, IngredientsManager ingredientsManager) {
        super(slots, attributesManager, phraseManager, ingredientsManager);
        currentActivity = Activities.SUSHI_SLICE;
    }

    @Override
    protected DialogItem getActivePhaseDialog() {

        DialogItem dialog;

        if (Objects.equals(currentIngredientReaction, userReply)) {

            this.successCount++;

            if (this.successCount == 3) {
                this.statePhase = PHASE_2;
                dialog = getSuccessDialog(phraseManager.getValueByKey("moveToPhase2"));
            }
            else {
                dialog = getSuccessDialog();
            }
        }
        else {
            this.mistakesCount++;
            if (this.mistakesCount < 3) {
                dialog = getFailureDialog();
            }
            else {
                dialog = getLoseRoundDialog();
            }
        }

        if (this.successCount == 5) {
            this.statePhase = WIN;
            dialog = getWinDialog();
        }

        return dialog;
    }

    @Override
    protected void populateActivityVariables() {
        previousIngredient = String.valueOf(sessionAttributes.get(PREVIOUS_INGREDIENT));
        statePhase = StatePhase.valueOf(String.valueOf(sessionAttributes.getOrDefault(STATE_PHASE, INTRO)));
        successCount = (int) sessionAttributes.getOrDefault(SUCCESS_COUNT, 0);
        mistakesCount = (int) sessionAttributes.getOrDefault(MISTAKES_COUNT, 0);
        Object ingredient = sessionAttributes.getOrDefault(INGREDIENT_REACTION, null);
        currentIngredientReaction = ingredient != null ? String.valueOf(ingredient) : null;
    }

    @Override
    protected void updateSessionAttributes() {
        sessionAttributes.put(MISTAKES_COUNT, mistakesCount);
        sessionAttributes.put(SUCCESS_COUNT, successCount);
        sessionAttributes.put(STATE_PHASE, statePhase);
    }
}
