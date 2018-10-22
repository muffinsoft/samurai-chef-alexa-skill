package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivitiesManager;
import com.muffinsoft.alexa.skills.samuraichef.content.LevelManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.SUCHI_SLICE_MOVE_TO_SECOND_PHASE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WRONG_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.SUSHI_SLICE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_2;

public class SushiSliceSessionStateManager extends BaseSamuraiChefSessionStateManager {

    private static final Logger logger = LoggerFactory.getLogger(SushiSliceSessionStateManager.class);

    public SushiSliceSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivitiesManager activitiesManager, LevelManager levelManager) {
        super(slots, attributesManager, phraseManager, activitiesManager, levelManager);
        currentActivity = SUSHI_SLICE;
    }

    @Override
    protected DialogItem getActivePhaseDialog() {

        DialogItem dialog;

        if (Objects.equals(currentIngredientReaction, userReply)) {

            this.successCount++;

            if (this.successCount == level.getPhaseTwoSuccessCount()) {
                this.statePhase = PHASE_2;
                dialog = getSuccessDialog(phraseManager.getValueByKey(SUCHI_SLICE_MOVE_TO_SECOND_PHASE_PHRASE));
            }
            else {
                dialog = getSuccessDialog();
            }
        }
        else {
            this.mistakesCount++;
            if (this.mistakesCount < level.getMaxMistakeCount()) {
                dialog = getFailureDialog(WRONG_PHRASE);
            }
            else {
                dialog = getLoseRoundDialog();
            }
        }

        if (this.successCount == level.getWonSuccessCount()) {
            dialog = getWinDialog();
        }

        return dialog;
    }
}
