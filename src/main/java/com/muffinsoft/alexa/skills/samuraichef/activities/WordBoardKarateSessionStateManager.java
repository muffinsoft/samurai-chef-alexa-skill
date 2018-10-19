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

public class WordBoardKarateSessionStateManager extends BaseSamuraiChefSessionStateManager {

    WordBoardKarateSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, IngredientsManager ingredientsManager, ActivitiesManager activitiesManager) {
        super(slots, attributesManager, phraseManager, ingredientsManager, activitiesManager);
        this.currentActivity = Activities.WORD_BOARD_KARATE;
    }

    @Override
    protected DialogItem getActivePhaseDialog() {

        DialogItem dialog;

        if (Objects.equals(currentIngredientReaction, userReply)) {

            this.successCount++;

            dialog = getSuccessDialog();
        }
        else {
            this.mistakesCount++;
            if (this.mistakesCount < 2) {
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
}
