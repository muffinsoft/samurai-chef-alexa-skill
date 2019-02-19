package com.muffinsoft.alexa.skills.samuraichef.activities.action;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.PhraseContainer;
import com.muffinsoft.alexa.skills.samuraichef.enums.PowerUps;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;

import java.util.List;
import java.util.Map;

import static com.muffinsoft.alexa.sdk.model.SlotName.ACTION;
import static com.muffinsoft.alexa.sdk.model.SlotName.ACTION_ALTERNATIVE;
import static com.muffinsoft.alexa.skills.samuraichef.components.PowerUpFabric.getNextPowerUp;
import static com.muffinsoft.alexa.skills.samuraichef.components.UserReplyComparator.compare;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.JUST_EARN_CORRECT_ANSWER_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.JUST_EARN_SECOND_CHANCE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.JUST_USE_CORRECT_ANSWER_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.JUST_USE_SECOND_CHANCE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.LAST_MISTAKE_COMPETITION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.LAST_MISTAKE_COMPETITION_TOO_LONG_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.LAST_MISTAKE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.LAST_MISTAKE_TOO_LONG_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.ONE_MISTAKE_LEFT_COMPETITION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.ONE_MISTAKE_LEFT_COMPETITION_TOO_LONG_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.ONE_MISTAKE_LEFT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.ONE_MISTAKE_LEFT_TOO_LONG_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.TWO_MISTAKES_LEFT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.TWO_MISTAKES_LEFT_TOO_LONG_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.QUESTION_TIME;

public abstract class BaseActivePhaseSamuraiChefStateManager extends BaseSamuraiChefStateManager {

    BaseActivePhaseSamuraiChefStateManager(Map<String, Slot> slots, AttributesManager attributesManager, SettingsDependencyContainer settingsDependencyContainer, PhraseDependencyContainer phraseDependencyContainer) {
        super(slots, attributesManager, settingsDependencyContainer, phraseDependencyContainer);
    }

    @Override
    protected DialogItem.Builder handleActivePhaseState(DialogItem.Builder builder) {

        String reaction = this.activityProgress.getCurrentIngredientReaction();

        if (compare(getUserReply(ACTION), reaction)
                || compare(getUserReply(ACTION_ALTERNATIVE), reaction)) {
            builder = handleSuccess(builder);
        }
        else {
            builder = handleMistake(builder);
        }

        if (this.activityProgress.getSuccessCount() >= stripe.getWonSuccessCount()) {
            builder = handleWiningEnd(builder);
        }

        return builder;
    }

    DialogItem.Builder handleTooLongMistake(DialogItem.Builder builder) {
        this.activityProgress.iterateMistakeCount();
        this.activityProgress.resetSuccessInRow();

        return getTooLongMistakeDialog(builder);
    }

    DialogItem.Builder handleMistake(DialogItem.Builder builder) {

        this.activityProgress.iterateMistakeCount();
        this.activityProgress.resetSuccessInRow();

        return getMistakeDialog(builder);
    }

    DialogItem.Builder handleMistakeWithSecondChance(DialogItem.Builder builder) {

        this.activityProgress.resetSuccessInRow();

        if (this.activityProgress.isPowerUpEquipped()) {

            logger.debug("Was removed power up: " + this.activityProgress.getActivePowerUp());

            builder.addBackgroundImageUrl(cardManager.getValueByKey("powerup_" + PowerUps.valueOf(this.activityProgress.getActivePowerUp()) + "-2"));

            this.activityProgress.removePowerUp();

            builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(JUST_USE_SECOND_CHANCE_PHRASE)));

            this.activityProgress.equipIfAvailable();

            if (this.activityProgress.isPowerUpEquipped()) {
                addAdditionalTimeToAnswer(15);
            }
            else {
                addAdditionalTimeToAnswer(10);
            }

            logger.debug("User have another chance to chose right answer");

            builder.withAplDocument(aplManager.getContainer());

            return getRePromptSuccessDialog(builder);
        }
        else {
            this.activityProgress.iterateMistakeCount();
            return getMistakeDialog(builder);
        }
    }

    DialogItem.Builder handleMistakeWithCorrectAnswer(DialogItem.Builder builder) {

        this.activityProgress.resetSuccessInRow();

        if (this.activityProgress.isPowerUpEquipped()) {

            logger.debug("Was removed power up: " + this.activityProgress.getActivePowerUp());

            builder.addBackgroundImageUrl(cardManager.getValueByKey("powerup_" + PowerUps.valueOf(this.activityProgress.getActivePowerUp()) + "-2"));

            this.activityProgress.removePowerUp();

            builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(JUST_USE_CORRECT_ANSWER_PHRASE)));

            this.activityProgress.equipIfAvailable();

            if (this.activityProgress.isPowerUpEquipped()) {
                addAdditionalTimeToAnswer(15);
            }
            else {
                addAdditionalTimeToAnswer(10);
            }

            logger.debug("Wrong answer was calculated as correct");

            this.activityProgress.iterateSuccessCount();

            builder.withAplDocument(aplManager.getContainer());

            return getSuccessDialog(builder);
        }
        else {

            this.activityProgress.iterateMistakeCount();

            return getMistakeDialog(builder);
        }
    }

    @SuppressWarnings("Duplicates")
    private DialogItem.Builder getTooLongMistakeDialog(DialogItem.Builder builder) {
        if (this.activityManager.isActivityCompetition(this.currentActivity)) {
            if (this.activityProgress.getMistakesCount() == 1) {
                builder.withAplDocument(aplManager.getContainer())
                        .addBackgroundImageUrl(cardManager.getValueByKey("mistake-1"));
                return this.getMistakeDialog(builder, ONE_MISTAKE_LEFT_COMPETITION_TOO_LONG_PHRASE);
            }
            else {
                builder.withAplDocument(aplManager.getContainer())
                        .addBackgroundImageUrl(cardManager.getValueByKey("mistake-2"));
                return this.getMistakeDialog(builder, LAST_MISTAKE_COMPETITION_TOO_LONG_PHRASE);
            }
        }
        else {
            if (this.activityProgress.getMistakesCount() == 2) {
                builder.withAplDocument(aplManager.getContainer())
                        .addBackgroundImageUrl(cardManager.getValueByKey("mistake-2"));
                return this.getMistakeDialog(builder, ONE_MISTAKE_LEFT_TOO_LONG_PHRASE);
            }
            else if (this.activityProgress.getMistakesCount() == 1) {
                builder.withAplDocument(aplManager.getContainer())
                        .addBackgroundImageUrl(cardManager.getValueByKey("mistake-1"));
                return this.getMistakeDialog(builder, TWO_MISTAKES_LEFT_TOO_LONG_PHRASE);
            }
            else {
                builder.withAplDocument(aplManager.getContainer())
                        .addBackgroundImageUrl(cardManager.getValueByKey("mistake-3"));
                return this.getMistakeDialog(builder, LAST_MISTAKE_TOO_LONG_PHRASE);
            }
        }
    }

    @SuppressWarnings("Duplicates")
    private DialogItem.Builder getMistakeDialog(DialogItem.Builder builder) {
        if (this.activityManager.isActivityCompetition(this.currentActivity)) {
            if (this.activityProgress.getMistakesCount() == 1) {
                builder.withAplDocument(aplManager.getContainer())
                        .addBackgroundImageUrl(cardManager.getValueByKey("mistake-1"));
                return this.getMistakeDialog(builder, ONE_MISTAKE_LEFT_COMPETITION_PHRASE);
            }
            else {
                builder.withAplDocument(aplManager.getContainer())
                        .addBackgroundImageUrl(cardManager.getValueByKey("mistake-2"));
                return this.getMistakeDialog(builder, LAST_MISTAKE_COMPETITION_PHRASE);
            }
        }
        else {
            if (this.activityProgress.getMistakesCount() == 2) {
                builder.withAplDocument(aplManager.getContainer())
                        .addBackgroundImageUrl(cardManager.getValueByKey("mistake-2"));
                return this.getMistakeDialog(builder, ONE_MISTAKE_LEFT_PHRASE);
            }
            else if (this.activityProgress.getMistakesCount() == 1) {
                builder.withAplDocument(aplManager.getContainer())
                        .addBackgroundImageUrl(cardManager.getValueByKey("mistake-1"));
                return this.getMistakeDialog(builder, TWO_MISTAKES_LEFT_PHRASE);
            }
            else {
                builder.withAplDocument(aplManager.getContainer())
                        .addBackgroundImageUrl(cardManager.getValueByKey("mistake-3"));
                return this.getMistakeDialog(builder, LAST_MISTAKE_PHRASE);
            }
        }
    }

    private DialogItem.Builder getMistakeDialog(DialogItem.Builder builder, String value) {

        if (this.activityProgress.getMistakesCount() < stripe.getMaxMistakeCount()) {
            logger.debug("Incorrect answer was found, running failure dialog");
            return getFailureDialog(builder, regularPhraseManager.getValueByKey(value));
        }
        else {
            logger.debug("Last available incorrect answer was found, running lose dialog");
            updateUserMistakeStory();
            savePersistentAttributes();
            return getLoseRoundDialog(builder, value);
        }
    }

    DialogItem.Builder handleSuccess(DialogItem.Builder builder) {

        this.activityProgress.iterateSuccessCount();
        this.activityProgress.iterateSuccessInARow();

        if (this.activityProgress.getSuccessInRow() % missionManager.getSuccessInRowForPowerUp() == 0) {

            logger.debug("Suitable case for earning new equipment. Lets check if user have enough space ...");

            PowerUps nextPowerUp = getNextPowerUp(this.activityProgress.getExistingPowerUps());
            if (nextPowerUp != null) {
                this.activityProgress.addPowerUp(nextPowerUp);
                logger.debug("Was earned equipment: " + nextPowerUp);

                builder.withAplDocument(aplManager.getContainer());

                List<PhraseContainer> prependedString;
                if (nextPowerUp == PowerUps.SECOND_CHANCE_SLOT) {
                    prependedString = regularPhraseManager.getValueByKey(JUST_EARN_SECOND_CHANCE_PHRASE);
                }
                else {
                    prependedString = regularPhraseManager.getValueByKey(JUST_EARN_CORRECT_ANSWER_PHRASE);
                }
                builder.addBackgroundImageUrl(cardManager.getValueByKey("powerup_" + nextPowerUp.name() + "-1"));

                addAdditionalTimeToAnswer(10);

                builder.addResponse(getDialogTranslator().translate(prependedString));
                logger.debug("Was equipped power up: " + nextPowerUp);
            }
            else {
                logger.debug("User have no free space for new equipment");
            }
        }
        logger.debug("Correct answer was found, running success dialog");
        return getSuccessDialog(builder);
    }

    private void addAdditionalTimeToAnswer(long seconds) {
        if (this.stripe.getTimeLimitPhaseOneInMillis() != null && this.stripe.getTimeLimitPhaseOneInMillis() > 0) {
            logger.debug("Add additional time to answer");
            getSessionAttributes().put(QUESTION_TIME, System.currentTimeMillis() + (seconds * 1_000));
        }
    }
}
