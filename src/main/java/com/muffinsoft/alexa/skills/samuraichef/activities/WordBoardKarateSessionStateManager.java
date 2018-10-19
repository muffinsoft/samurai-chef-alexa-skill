package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivitiesManager;
import com.muffinsoft.alexa.skills.samuraichef.content.IngredientsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;

import java.util.Map;
import java.util.Objects;

import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.WORD_BOARD_KARATE;

public class WordBoardKarateSessionStateManager extends BaseSamuraiChefSessionStateManager {

    public WordBoardKarateSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, IngredientsManager ingredientsManager, ActivitiesManager activitiesManager) {
        super(slots, attributesManager, phraseManager, ingredientsManager, activitiesManager);
        this.currentActivity = WORD_BOARD_KARATE;
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
}
