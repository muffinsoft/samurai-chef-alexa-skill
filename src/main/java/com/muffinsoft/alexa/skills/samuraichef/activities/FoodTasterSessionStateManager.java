package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.LevelManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.ProgressManager;
import com.muffinsoft.alexa.skills.samuraichef.models.Speech;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.TOO_LONG_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.QUESTION_TIME;
import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.FOOD_TASTER;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_1;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_2;

public class FoodTasterSessionStateManager extends BaseActivePhaseSamuraiChefSessionStateManager {

    private static final Logger logger = LoggerFactory.getLogger(FoodTasterSessionStateManager.class);

    protected Long questionTime;

    public FoodTasterSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, LevelManager levelManager, PowerUpsManager powerUpsManager, ProgressManager progressManager) {
        super(slots, attributesManager, phraseManager, levelManager, powerUpsManager, progressManager);
        this.currentActivity = FOOD_TASTER;
    }

    @Override
    protected DialogItem handleSuccess() {

        long answerTime = System.currentTimeMillis();

        long answerLimit = this.statePhase == PHASE_1 ? this.stripe.getTimeLimitPhaseOneInMillis() : this.stripe.getTimeLimitPhaseTwoInMillis();

        if (questionTime == null || answerTime - questionTime < answerLimit) {

            this.activityProgress.iterateSuccessCount();

            if (this.activityProgress.getSuccessCount() == this.stripe.getPhaseTwoSuccessCount()) {
                this.statePhase = PHASE_2;
                Speech speech = levelManager.getSpeechForActivityByStripeNumber(this.currentActivity, this.userProgress.getStripeCount());
                return getSuccessDialog(speech.getMoveToPhaseTwo());
            }
            else {
                return getSuccessDialog();
            }
        }
        else {
            return getFailureDialog(phraseManager.getValueByKey(TOO_LONG_PHRASE));
        }
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
