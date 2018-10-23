package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivitiesManager;
import com.muffinsoft.alexa.skills.samuraichef.content.LevelManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
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

    private Long questionTime;

    public FoodTasterSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivitiesManager activitiesManager, LevelManager levelManager) {
        super(slots, attributesManager, phraseManager, activitiesManager, levelManager);
        this.currentActivity = FOOD_TASTER;
    }

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
                dialog = getFailureDialog(TOO_LONG_PHRASE);
            }
        }
        else {
            this.mistakesCount++;
            if (this.mistakesCount < this.level.getMaxMistakeCount()) {
                dialog = getFailureDialog(WRONG_PHRASE);
            }
            else {
                dialog = getLoseRoundDialog();
            }
        }

        if (this.successCount == this.level.getWonSuccessCount()) {
            this.statePhase = WIN;
            dialog = getWinDialog();
        }

        return dialog;
    }

    @Override
    protected void resetRoundProgress() {
        super.resetRoundProgress();
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
