package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivitiesManager;
import com.muffinsoft.alexa.skills.samuraichef.content.IngredientsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;

import java.util.Map;
import java.util.Objects;

import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_2;

public class SushiSliceSessionStateManager extends BaseSamuraiChefSessionStateManager {

    public SushiSliceSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, IngredientsManager ingredientsManager, ActivitiesManager activitiesManager) {
        super(slots, attributesManager, phraseManager, ingredientsManager, activitiesManager);
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
                dialog = getFailureDialog("Wrong!");
            }
            else {
                dialog = getLoseRoundDialog();
            }
        }

        if (this.successCount == 5) {
            dialog = getWinDialog();
        }

        return dialog;
    }

    @Override
    protected void calculateProgress() {
        this.roundCount += 1;
        if (roundCount == 4) {
            this.roundCount = 0;
            this.stripeCount += 1;
        }
    }

    @Override
    protected void populateActivityVariables() {
        super.populateActivityVariables();
    }

    @Override
    protected void updateSessionAttributes() {
        super.updateSessionAttributes();
    }
}
