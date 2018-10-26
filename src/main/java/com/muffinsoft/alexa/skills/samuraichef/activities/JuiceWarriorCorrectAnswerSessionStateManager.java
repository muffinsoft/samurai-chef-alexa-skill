package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivitiesManager;
import com.muffinsoft.alexa.skills.samuraichef.content.LevelManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.RewardManager;

import java.util.Map;
import java.util.Objects;

import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.TOO_LONG_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WRONG_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.WIN;

public class JuiceWarriorCorrectAnswerSessionStateManager extends JuiceWarriorSessionStateManager {

    public JuiceWarriorCorrectAnswerSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivitiesManager activitiesManager, LevelManager levelManager, PowerUpsManager powerUpsManager, RewardManager rewardManager) {
        super(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, rewardManager);
    }

    @SuppressWarnings("Duplicates")
    @Override
    protected DialogItem getActivePhaseDialog() {

        DialogItem dialog;

        long answerTime = System.currentTimeMillis();

        if (Objects.equals(this.activityProgress.getCurrentIngredientReaction(), userReply)) {

            long answerLimit = level.getTimeLimitPhaseOneInMillis();

            if (questionTime == null || answerTime - questionTime < answerLimit) {

                this.activityProgress.iterateSuccessCount();

                dialog = getSuccessDialog();
            }
            else {
                dialog = getFailureDialog(phraseManager.getValueByKey(TOO_LONG_PHRASE));
            }
        }
        else {
            if (this.userProgress.isPowerUpEquipped()) {
                this.userProgress.removePowerUp();
                dialog = getSuccessDialog();
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
            this.statePhase = WIN;
            dialog = getWinDialog();
        }

        return dialog;
    }
}
