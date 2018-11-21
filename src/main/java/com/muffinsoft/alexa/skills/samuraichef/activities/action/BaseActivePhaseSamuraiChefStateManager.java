package com.muffinsoft.alexa.skills.samuraichef.activities.action;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.components.PowerUpFabric;
import com.muffinsoft.alexa.skills.samuraichef.enums.PowerUps;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseSettings;

import java.util.Map;
import java.util.Objects;

import static com.muffinsoft.alexa.sdk.model.Speech.ofAlexa;
import static com.muffinsoft.alexa.skills.samuraichef.components.VoiceTranslator.translate;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.JUST_EARN_CORRECT_ANSWER_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.JUST_EARN_SECOND_CHANCE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.JUST_WEAR_CORRECT_ANSWER_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.JUST_WEAR_SECOND_CHANCE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.LAST_MISTAKE_COMPETITION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.LAST_MISTAKE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.ONE_MISTAKE_LEFT_COMPETITION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.ONE_MISTAKE_LEFT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.TOO_LONG_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.TWO_MISTAKES_LEFT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.USED_EQUIPMENT_PHRASE;

public abstract class BaseActivePhaseSamuraiChefStateManager extends BaseSamuraiChefStateManager {

    BaseActivePhaseSamuraiChefStateManager(Map<String, Slot> slots, AttributesManager attributesManager, ConfigContainer configContainer) {
        super(slots, attributesManager, configContainer);
    }

    @Override
    protected DialogItem.Builder handleActivePhaseState(DialogItem.Builder builder) {

        logger.debug("Handling " + this.statePhase);

        if (Objects.equals(this.activityProgress.getCurrentIngredientReaction(), getUserReply())) {
            builder = handleSuccess(builder);
        }
        else {
            builder = handleMistake(builder);
        }

        if (this.activityProgress.getSuccessCount() == stripe.getWonSuccessCount()) {
            builder = handleWiningEnd(builder);
        }

        return builder;
    }

    protected DialogItem.Builder handleTooLongMistake(DialogItem.Builder builder) {
        this.activityProgress.iterateMistakeCount();
        this.activityProgress.resetSuccessInRow();

        return getMistakeDialog(builder, TOO_LONG_PHRASE);
    }

    protected DialogItem.Builder handleMistake(DialogItem.Builder builder) {

        this.activityProgress.iterateMistakeCount();
        this.activityProgress.resetSuccessInRow();

        return getMistakeDialog(builder);
    }

    protected DialogItem.Builder handleMistakeWithSecondChance(DialogItem.Builder builder) {

        this.activityProgress.resetSuccessInRow();

        if (this.activityProgress.isPowerUpEquipped()) {

            logger.debug("Was removed power up: " + this.activityProgress.getActivePowerUp());

            String removedPowerUp = this.activityProgress.removePowerUp();

            builder.addResponse(translate(phraseManager.getValueByKey(USED_EQUIPMENT_PHRASE)));
            builder.addResponse(translate(aliasManager.getValueByKey(removedPowerUp)));

            builder = equipIfAvailable(builder);

            logger.debug("User have another chance to chose right answer");

            return getRePromptSuccessDialog(builder);
        }
        else {
            this.activityProgress.iterateMistakeCount();
            return getMistakeDialog(builder);
        }
    }

    protected DialogItem.Builder handleMistakeWithCorrectAnswer(DialogItem.Builder builder) {

        this.activityProgress.resetSuccessInRow();

        if (this.activityProgress.isPowerUpEquipped()) {

            logger.debug("Was removed power up: " + this.activityProgress.getActivePowerUp());

            String removedPowerUp = this.activityProgress.removePowerUp();

            builder.addResponse(translate(phraseManager.getValueByKey(USED_EQUIPMENT_PHRASE)));
            builder.addResponse(translate(aliasManager.getValueByKey(removedPowerUp)));

            builder = equipIfAvailable(builder);

            logger.debug("Wrong answer was calculated as correct");

            this.activityProgress.iterateSuccessCount();

            return getSuccessDialog(builder);
        }
        else {

            this.activityProgress.iterateMistakeCount();

            return getMistakeDialog(builder);
        }
    }

    private DialogItem.Builder getMistakeDialog(DialogItem.Builder builder) {
        if (this.activityManager.isActivityCompetition(this.currentActivity)) {
            if (this.activityProgress.getMistakesCount() == 1) {
                return this.getMistakeDialog(builder, ONE_MISTAKE_LEFT_COMPETITION_PHRASE);
            }
            else {
                return this.getMistakeDialog(builder, LAST_MISTAKE_COMPETITION_PHRASE);
            }
        }
        else {
            if (this.activityProgress.getMistakesCount() == 2) {
                return this.getMistakeDialog(builder, ONE_MISTAKE_LEFT_PHRASE);
            }
            else if (this.activityProgress.getMistakesCount() == 1) {
                return this.getMistakeDialog(builder, TWO_MISTAKES_LEFT_PHRASE);
            }
            else {
                return this.getMistakeDialog(builder, LAST_MISTAKE_PHRASE);
            }
        }
    }

    private DialogItem.Builder getMistakeDialog(DialogItem.Builder builder, String value) {

        if (this.activityProgress.getMistakesCount() < stripe.getMaxMistakeCount()) {
            logger.debug("Incorrect answer was found, running failure dialog");
            return getFailureDialog(builder, phraseManager.getValueByKey(value));
        }
        else {
            logger.debug("Last available incorrect answer was found, running lose dialog");
            updateUserMistakeStory();
            savePersistentAttributes();
            return getLoseRoundDialog(builder, value);
        }
    }


    private DialogItem.Builder equipIfAvailable(DialogItem.Builder builder) {
        PowerUps nextPowerUp = this.activityProgress.equipIfAvailable();
        if (nextPowerUp != null) {
            PhraseSettings prependedString;
            if (nextPowerUp == PowerUps.SECOND_CHANCE_SLOT) {
                prependedString = phraseManager.getValueByKey(JUST_WEAR_SECOND_CHANCE_PHRASE);
            }
            else {
                prependedString = phraseManager.getValueByKey(JUST_WEAR_CORRECT_ANSWER_PHRASE);
            }
            builder.addResponse(translate(prependedString));
            logger.debug("Was equipped power up: " + nextPowerUp);
        }
        return builder;
    }

    protected DialogItem.Builder handleSuccess(DialogItem.Builder builder) {

        this.activityProgress.iterateSuccessCount();
        this.activityProgress.iterateSuccessInARow();

        if (this.activityProgress.getSuccessInRow() % missionManager.getSuccessInRowForPowerUp() == 0) {

            logger.debug("Suitable case for earning new equipment. Lets check if user have enough space ...");

            PowerUps nextPowerUp = PowerUpFabric.getNext(this.activityProgress.getExistingPowerUps());
            if (nextPowerUp != null) {
                this.activityProgress.addPowerUp(nextPowerUp);
                logger.debug("Was earned equipment: " + nextPowerUp);
                if (Objects.equals(this.activityProgress.getActivePowerUp(), nextPowerUp.name())) {
                    PhraseSettings prependedString;
                    if (nextPowerUp == PowerUps.SECOND_CHANCE_SLOT) {
                        prependedString = phraseManager.getValueByKey(JUST_WEAR_SECOND_CHANCE_PHRASE);
                    }
                    else {
                        prependedString = phraseManager.getValueByKey(JUST_WEAR_CORRECT_ANSWER_PHRASE);
                    }
                    builder.addResponse(translate(prependedString));
                }
                else {
                    PhraseSettings prependedString;
                    if (nextPowerUp == PowerUps.SECOND_CHANCE_SLOT) {
                        prependedString = phraseManager.getValueByKey(JUST_EARN_SECOND_CHANCE_PHRASE);
                    }
                    else {
                        prependedString = phraseManager.getValueByKey(JUST_EARN_CORRECT_ANSWER_PHRASE);
                    }
                    builder.addResponse(translate(prependedString));
                }
                logger.debug("Was equipped power up: " + nextPowerUp);
            }
            else {
                logger.debug("User have no free space for new equipment");
            }
        }
        logger.debug("Correct answer was found, running success dialog");
        return getSuccessDialog(builder);
    }
}
