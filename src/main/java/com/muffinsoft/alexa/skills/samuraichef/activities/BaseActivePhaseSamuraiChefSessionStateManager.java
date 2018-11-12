package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivityManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.MissionManager;

import java.util.Map;
import java.util.Objects;

import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.USED_EQUIPMENT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WRONG_PHRASE;

public abstract class BaseActivePhaseSamuraiChefSessionStateManager extends BaseSamuraiChefSessionStateManager {

    BaseActivePhaseSamuraiChefSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivityManager activityManager, PowerUpsManager powerUpsManager, MissionManager missionManager) {
        super(slots, attributesManager, phraseManager, activityManager, powerUpsManager, missionManager);
    }

    @Override
    protected DialogItem handleActivePhaseState() {

        DialogItem dialog;

        if (Objects.equals(this.activityProgress.getCurrentIngredientReaction(), userReply)) {
            dialog = handleSuccess();
        }
        else {
            dialog = handleMistake();
        }

        if (this.activityProgress.getSuccessCount() == stripe.getWonSuccessCount()) {
            dialog = getWinDialog();
        }

        return dialog;
    }

    protected DialogItem handleMistake() {

        this.activityProgress.iterateMistakeCount();

        if (this.activityProgress.getMistakesCount() < stripe.getMaxMistakeCount()) {
            return getFailureDialog(phraseManager.getValueByKey(WRONG_PHRASE));
        }
        else {
            return getLoseRoundDialog();
        }
    }

    protected DialogItem handleMistakeWithSecondChance() {

        if (this.userProgress.isPowerUpEquipped()) {

            this.userProgress.removePowerUp();
            this.dialogPrefix = phraseManager.getValueByKey(USED_EQUIPMENT_PHRASE);

            return getRepromptSuccessDialog();
        }
        else {
            this.activityProgress.iterateMistakeCount();
            if (this.activityProgress.getMistakesCount() < stripe.getMaxMistakeCount()) {
                return getFailureDialog(phraseManager.getValueByKey(WRONG_PHRASE));
            }
            else {
                return getLoseRoundDialog();
            }
        }
    }

    protected DialogItem handleMistakeWithCorrectAnswer() {

        if (this.userProgress.isPowerUpEquipped()) {

            this.userProgress.removePowerUp();
            this.dialogPrefix = phraseManager.getValueByKey(USED_EQUIPMENT_PHRASE);

            return getSuccessDialog();
        }
        else {

            this.activityProgress.iterateMistakeCount();

            if (this.activityProgress.getMistakesCount() < stripe.getMaxMistakeCount()) {
                return getFailureDialog(phraseManager.getValueByKey(WRONG_PHRASE));
            }
            else {
                return getLoseRoundDialog();
            }
        }
    }

    protected DialogItem handleSuccess() {

        this.activityProgress.iterateSuccessCount();

        return getSuccessDialog();
    }
}
