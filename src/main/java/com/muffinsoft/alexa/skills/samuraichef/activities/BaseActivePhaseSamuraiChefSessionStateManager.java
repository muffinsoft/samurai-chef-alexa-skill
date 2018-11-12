package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.components.PowerUpFabric;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivityManager;
import com.muffinsoft.alexa.skills.samuraichef.content.MissionManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.AliasManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.PowerUps;

import java.util.Map;
import java.util.Objects;

import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.JUST_EARN_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.USED_EQUIPMENT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WRONG_PHRASE;

public abstract class BaseActivePhaseSamuraiChefSessionStateManager extends BaseSamuraiChefSessionStateManager {

    BaseActivePhaseSamuraiChefSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivityManager activityManager, AliasManager aliasManager, MissionManager missionManager) {
        super(slots, attributesManager, phraseManager, activityManager, aliasManager, missionManager);
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
        this.activityProgress.resetSuccessInRow();

        if (this.activityProgress.getMistakesCount() < stripe.getMaxMistakeCount()) {
            return getFailureDialog(phraseManager.getValueByKey(WRONG_PHRASE));
        }
        else {
            return getLoseRoundDialog();
        }
    }

    protected DialogItem handleMistakeWithSecondChance() {

        this.activityProgress.resetSuccessInRow();

        if (this.activityProgress.isPowerUpEquipped()) {

            this.activityProgress.removePowerUp();
            this.dialogPrefix = phraseManager.getValueByKey(USED_EQUIPMENT_PHRASE);

            equipIfAvailable();

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

        this.activityProgress.resetSuccessInRow();

        if (this.activityProgress.isPowerUpEquipped()) {

            this.activityProgress.removePowerUp();

            this.dialogPrefix = phraseManager.getValueByKey(USED_EQUIPMENT_PHRASE);

            equipIfAvailable();

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

    private void equipIfAvailable() {
        PowerUps nextPowerUp = this.activityProgress.equipIfAvailable();
        if (nextPowerUp != null) {
            dialogPrefix += " " + phraseManager.getValueByKey(JUST_EARN_PHRASE) + aliasManager.getValueByKey(nextPowerUp.name()) + "! ";
        }
    }

    protected DialogItem handleSuccess() {

        this.activityProgress.iterateSuccessCount();
        this.activityProgress.iterateSuccessInARow();

        if (this.activityProgress.getSuccessInRow() % missionManager.getSuccessInRowForPowerUp() == 0) {

            PowerUps nextPowerUp = PowerUpFabric.getNext(this.activityProgress.getExistingPowerUps());
            if (nextPowerUp != null) {
                this.activityProgress.addPowerUp(nextPowerUp);
                dialogPrefix = phraseManager.getValueByKey(JUST_EARN_PHRASE) + " " + aliasManager.getValueByKey(nextPowerUp.name()) + "! ";
            }
        }

        return getSuccessDialog();
    }
}
