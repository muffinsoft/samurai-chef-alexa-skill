package com.muffinsoft.alexa.skills.samuraichef.activities.action;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;

import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.QUESTION_TIME;
import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.JUICE_WARRIOR;

public class JuiceWarriorStateManager extends BaseActivePhaseSamuraiChefStateManager {

    protected Long questionTime;

    public JuiceWarriorStateManager(Map<String, Slot> slots, AttributesManager attributesManager, ConfigContainer configContainer) {
        super(slots, attributesManager, configContainer);
        this.currentActivity = JUICE_WARRIOR;
    }

    @Override
    protected DialogItem handleSuccess() {

        long answerTime = System.currentTimeMillis();

        long answerLimit = stripe.getTimeLimitPhaseOneInMillis();

        if (questionTime == null || answerTime - questionTime < answerLimit) {

            return super.handleSuccess();
        }
        else {
            return this.handleMistake();
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
