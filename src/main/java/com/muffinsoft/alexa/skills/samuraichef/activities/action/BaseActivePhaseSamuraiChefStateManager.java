package com.muffinsoft.alexa.skills.samuraichef.activities.action;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.components.PowerUpFabric;
import com.muffinsoft.alexa.skills.samuraichef.enums.PowerUps;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;

import java.util.Map;
import java.util.Objects;

import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.JUST_EARN_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.JUST_WEAR_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.TOO_LONG_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.USED_EQUIPMENT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WRONG_PHRASE;

public abstract class BaseActivePhaseSamuraiChefStateManager extends BaseSamuraiChefStateManager {

    BaseActivePhaseSamuraiChefStateManager(Map<String, Slot> slots, AttributesManager attributesManager, ConfigContainer configContainer) {
        super(slots, attributesManager, configContainer);
    }

    @Override
    protected DialogItem handleActivePhaseState() {

        logger.debug("Handling " + this.statePhase);

        DialogItem dialog;

        if (Objects.equals(this.activityProgress.getCurrentIngredientReaction(), getUserReply())) {
            dialog = handleSuccess();
        }
        else {
            dialog = handleMistake();
        }

        if (this.activityProgress.getSuccessCount() == stripe.getWonSuccessCount()) {
            savePersistentAttributes();
            dialog = getWinDialog();
        }

        return dialog;
    }

    protected DialogItem handleTooLongMistake() {
        this.activityProgress.iterateMistakeCount();
        this.activityProgress.resetSuccessInRow();

        return getMistakeDialog(TOO_LONG_PHRASE);
    }

    protected DialogItem handleMistake() {

        this.activityProgress.iterateMistakeCount();
        this.activityProgress.resetSuccessInRow();

        return getMistakeDialog();
    }

    protected DialogItem handleMistakeWithSecondChance() {

        this.activityProgress.resetSuccessInRow();

        if (this.activityProgress.isPowerUpEquipped()) {

            logger.debug("Was removed power up: " + this.activityProgress.getActivePowerUp());

            String removedPowerUp = this.activityProgress.removePowerUp();

            this.dialogPrefix = phraseManager.getValueByKey(USED_EQUIPMENT_PHRASE) + " " + aliasManager.getValueByKey(removedPowerUp) + "! ";

            equipIfAvailable();

            logger.debug("User have another chance to chose right answer");

            return getRePromptSuccessDialog();
        }
        else {
            this.activityProgress.iterateMistakeCount();
            return getMistakeDialog();
        }
    }

    protected DialogItem handleMistakeWithCorrectAnswer() {

        this.activityProgress.resetSuccessInRow();

        if (this.activityProgress.isPowerUpEquipped()) {

            logger.debug("Was removed power up: " + this.activityProgress.getActivePowerUp());

            String removedPowerUp = this.activityProgress.removePowerUp();

            this.dialogPrefix = phraseManager.getValueByKey(USED_EQUIPMENT_PHRASE) + " " + aliasManager.getValueByKey(removedPowerUp) + "! ";

            equipIfAvailable();

            logger.debug("Wrong answer was calculated as correct");

            this.activityProgress.iterateSuccessCount();

            return getSuccessDialog();
        }
        else {

            this.activityProgress.iterateMistakeCount();

            return getMistakeDialog();
        }
    }

    private DialogItem getMistakeDialog() {
        return this.getMistakeDialog(WRONG_PHRASE);
    }

    private DialogItem getMistakeDialog(String value) {
        if (this.activityProgress.getMistakesCount() < stripe.getMaxMistakeCount()) {
            logger.debug("Incorrect answer was found, running failure dialog");
            return getFailureDialog(phraseManager.getValueByKey(value));
        }
        else {
            logger.debug("Last available incorrect answer was found, running lose dialog");
            savePersistentAttributes();
            return getLoseRoundDialog(value);
        }
    }

    private void equipIfAvailable() {
        PowerUps nextPowerUp = this.activityProgress.equipIfAvailable();
        if (nextPowerUp != null) {
            dialogPrefix += " " + phraseManager.getValueByKey(JUST_WEAR_PHRASE) + aliasManager.getValueByKey(nextPowerUp.name()) + "! ";
            logger.debug("Was equipped power up: " + nextPowerUp);
        }
    }

    protected DialogItem handleSuccess() {

        this.activityProgress.iterateSuccessCount();
        this.activityProgress.iterateSuccessInARow();

        if (this.activityProgress.getSuccessInRow() % missionManager.getSuccessInRowForPowerUp() == 0) {

            logger.debug("Suitable case for earning new equipment. Lets check if user have enough space ...");

            PowerUps nextPowerUp = PowerUpFabric.getNext(this.activityProgress.getExistingPowerUps());
            if (nextPowerUp != null) {
                this.activityProgress.addPowerUp(nextPowerUp);
                logger.debug("Was earned equipment: " + nextPowerUp);
                if (Objects.equals(this.activityProgress.getActivePowerUp(), nextPowerUp.name())) {
                    dialogPrefix = phraseManager.getValueByKey(JUST_WEAR_PHRASE) + " " + aliasManager.getValueByKey(nextPowerUp.name()) + "! ";
                }
                else {
                    dialogPrefix = phraseManager.getValueByKey(JUST_EARN_PHRASE) + " " + aliasManager.getValueByKey(nextPowerUp.name()) + "! ";
                }
                logger.debug("Was equipped power up: " + nextPowerUp);
            }
            else {
                logger.debug("User have no free space for new equipment");
            }
        }
        logger.debug("Correct answer was found, running success dialog");
        return getSuccessDialog();
    }
}
