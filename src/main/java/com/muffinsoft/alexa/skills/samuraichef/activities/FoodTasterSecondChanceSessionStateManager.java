package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivitiesManager;
import com.muffinsoft.alexa.skills.samuraichef.content.LevelManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import com.muffinsoft.alexa.skills.samuraichef.models.Speech;

import java.util.Map;
import java.util.Objects;

import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.TOO_LONG_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WRONG_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.EQUIPED_POWER_UP;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_1;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_2;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.WIN;

public class FoodTasterSecondChanceSessionStateManager extends FoodTasterSessionStateManager {

    public FoodTasterSecondChanceSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivitiesManager activitiesManager, LevelManager levelManager, PowerUpsManager powerUpsManager) {
        super(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager);
    }

    @SuppressWarnings("Duplicates")
    @Override
    protected DialogItem getActivePhaseDialog() {

        DialogItem dialog;

        long answerTime = System.currentTimeMillis();

        if (Objects.equals(currentIngredientReaction, userReply)) {

            long answerLimit = this.statePhase == PHASE_1 ? this.level.getTimeLimitPhaseOneInMillis() : this.level.getTimeLimitPhaseTwoInMillis();

            if (questionTime == null || answerTime - questionTime < answerLimit) {

                this.successCount++;

                if (this.successCount == this.level.getPhaseTwoSuccessCount()) {
                    this.statePhase = PHASE_2;
                    Speech speech = levelManager.getSpeechForActivityByNumber(this.currentActivity, this.currentLevel);
                    dialog = getSuccessDialog(speech.getMoveToPhaseTwo());
                }
                else {
                    dialog = getSuccessDialog();
                }
            }
            else {
                dialog = getFailureDialog(phraseManager.getValueByKey(TOO_LONG_PHRASE));
            }
        }
        else {
            boolean isPresent = sessionAttributes.containsKey(EQUIPED_POWER_UP);
            if (isPresent) {
                sessionAttributes.remove(EQUIPED_POWER_UP);
                dialog = getRepromptSuccessDialog();
            }
            else {
                this.mistakesCount++;
                if (this.mistakesCount < level.getMaxMistakeCount()) {
                    dialog = getFailureDialog(phraseManager.getValueByKey(WRONG_PHRASE));
                }
                else {
                    dialog = getLoseRoundDialog();
                }
            }
        }

        if (this.successCount == this.level.getWonSuccessCount()) {
            this.statePhase = WIN;
            dialog = getWinDialog();
        }

        return dialog;
    }
}
