package com.muffinsoft.alexa.skills.samuraichef.activities.action;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;

import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.QUESTION_TIME;
import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.SUSHI_SLICE;

public class SushiSliceStateManager extends BaseActivePhaseSamuraiChefStateManager {

    protected Long questionTime;

    public SushiSliceStateManager(Map<String, Slot> slots, AttributesManager attributesManager, ConfigContainer configContainer) {
        super(slots, attributesManager, configContainer);
        currentActivity = SUSHI_SLICE;
    }

    @Override
    protected DialogItem handleSuccess() {

        long answerTime = System.currentTimeMillis();

        long answerLimit = this.stripe.getTimeLimitPhaseOneInMillis();

        if (questionTime == null || answerTime - questionTime < answerLimit) {

            return super.handleSuccess();
        }
        else {
            return handleTooLongMistake();
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
        questionTime = (Long) getSessionAttributes().get(QUESTION_TIME);
    }

    @Override
    protected void updateSessionAttributes() {
        super.updateSessionAttributes();
        getSessionAttributes().put(QUESTION_TIME, System.currentTimeMillis());
    }
}
