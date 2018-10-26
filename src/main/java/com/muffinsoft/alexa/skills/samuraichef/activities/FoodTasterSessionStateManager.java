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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.TOO_LONG_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WRONG_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.QUESTION_TIME;
import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.FOOD_TASTER;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_1;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_2;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.WIN;

public class FoodTasterSessionStateManager extends BaseSamuraiChefSessionStateManager {

    private static final Logger logger = LoggerFactory.getLogger(FoodTasterSessionStateManager.class);

    protected Long questionTime;

    public FoodTasterSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivitiesManager activitiesManager, LevelManager levelManager, PowerUpsManager powerUpsManager, RewardManager rewardManager) {
        super(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, rewardManager);
        this.currentActivity = FOOD_TASTER;
    }

    @SuppressWarnings("Duplicates")
    @Override
    protected DialogItem getActivePhaseDialog() {

        DialogItem dialog;

        long answerTime = System.currentTimeMillis();

        if (Objects.equals(this.activityProgress.getCurrentIngredientReaction(), userReply)) {

            long answerLimit = this.statePhase == PHASE_1 ? this.level.getTimeLimitPhaseOneInMillis() : this.level.getTimeLimitPhaseTwoInMillis();

            if (questionTime == null || answerTime - questionTime < answerLimit) {

                this.activityProgress.iterateSuccessCount();

                if (this.activityProgress.getSuccessCount() == this.level.getPhaseTwoSuccessCount()) {
                    this.statePhase = PHASE_2;
                    Speech speech = levelManager.getSpeechForActivityByNumber(this.currentActivity, this.userProgress.getCurrentLevel());
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
            this.activityProgress.iterateMistakeCount();
            if (this.activityProgress.getMistakesCount() < this.level.getMaxMistakeCount()) {
                dialog = getFailureDialog(phraseManager.getValueByKey(WRONG_PHRASE));
            }
            else {
                dialog = getLoseRoundDialog();
            }
        }

        if (this.activityProgress.getSuccessCount() == this.level.getWonSuccessCount()) {
            this.statePhase = WIN;
            dialog = getWinDialog();
        }

        return dialog;
    }

    @Override
    protected void resetActivityProgress() {
        super.resetActivityProgress();
        this.questionTime = null;
    }

    @Override
    protected void populateActivityVariables() {
        super.populateActivityVariables();
        questionTime = (Long) sessionAttributes.get(QUESTION_TIME);
    }

    @Override
    protected void updateSessionAttributes() {
        super.updateSessionAttributes();
        sessionAttributes.put(QUESTION_TIME, System.currentTimeMillis());
    }
}
