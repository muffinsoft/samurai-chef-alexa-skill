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
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.QUESTION_TIME;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.SUCCESS_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_2;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.WIN;

public class JuiceWarriorSessionStateManager extends BaseSamuraiChefSessionStateManager {

    private Long questionTime;

    public JuiceWarriorSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, IngredientsManager ingredientsManager) {
        super(slots, attributesManager, phraseManager, ingredientsManager);
        this.currentActivity = Activities.JUICE_WARRIOR;
    }

    @Override
    protected DialogItem getActivePhaseDialog() {

        DialogItem dialog;

        long answerTime = System.currentTimeMillis();

        if (Objects.equals(currentIngredientReaction, userReply)) {

            if (questionTime == null || questionTime - answerTime < 3000) {

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
                dialog = getFailureDialog("Too long!");
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
        statePhase = StatePhase.valueOf(String.valueOf(sessionAttributes.get(STATE_PHASE)));
        successCount = (int) sessionAttributes.get(SUCCESS_COUNT);
        mistakesCount = (int) sessionAttributes.get(MISTAKES_COUNT);
        questionTime = (Long) sessionAttributes.get(QUESTION_TIME);
        Object ingredient = sessionAttributes.get(INGREDIENT_REACTION);
        currentIngredientReaction = ingredient != null ? String.valueOf(ingredient) : null;
    }

    @Override
    protected void updateSessionAttributes() {
        sessionAttributes.put(MISTAKES_COUNT, mistakesCount);
        sessionAttributes.put(SUCCESS_COUNT, successCount);
        sessionAttributes.put(STATE_PHASE, statePhase);
        sessionAttributes.put(QUESTION_TIME, System.currentTimeMillis());
    }
}
