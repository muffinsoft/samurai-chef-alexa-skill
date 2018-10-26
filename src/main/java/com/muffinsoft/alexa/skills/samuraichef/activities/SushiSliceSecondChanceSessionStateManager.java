package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivitiesManager;
import com.muffinsoft.alexa.skills.samuraichef.content.LevelManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.RewardManager;
import com.muffinsoft.alexa.skills.samuraichef.models.Speech;

import java.util.Map;
import java.util.Objects;

import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WRONG_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_2;

public class SushiSliceSecondChanceSessionStateManager extends SushiSliceSessionStateManager {

    public SushiSliceSecondChanceSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivitiesManager activitiesManager, LevelManager levelManager, PowerUpsManager powerUpsManager, RewardManager rewardManager) {
        super(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, rewardManager);
    }

    @SuppressWarnings("Duplicates")
    @Override
    protected DialogItem getActivePhaseDialog() {

        DialogItem dialog;

        if (Objects.equals(this.activityProgress.getCurrentIngredientReaction(), userReply)) {

            this.activityProgress.iterateSuccessCount();

            if (this.activityProgress.getSuccessCount() == level.getPhaseTwoSuccessCount()) {
                this.statePhase = PHASE_2;
                Speech speech = levelManager.getSpeechForActivityByNumber(this.currentActivity, this.userProgress.getCurrentLevel());
                dialog = getSuccessDialog(speech.getMoveToPhaseTwo());
            }
            else {
                dialog = getSuccessDialog();
            }
        }
        else {
            if (this.userProgress.isPowerUpEquipped()) {
                this.userProgress.removePowerUp();
                dialog = getRepromptSuccessDialog();
            }
            else {
                this.activityProgress.iterateMistakeCount();
                if (this.activityProgress.getMistakesCount() < level.getMaxMistakeCount()) {
                    dialog = getFailureDialog(phraseManager.getValueByKey(WRONG_PHRASE));
                }
                else {
                    dialog = getLoseRoundDialog();
                }
            }
        }

        if (this.activityProgress.getSuccessCount() == level.getWonSuccessCount()) {
            dialog = getWinDialog();
        }

        return dialog;
    }
}
