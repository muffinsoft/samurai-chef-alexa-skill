package com.muffinsoft.alexa.skills.samuraichef.activities.action;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;

import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.QUESTION_TIME;
import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.SUSHI_SLICE;

public class SushiSliceStateManager extends BaseActivePhaseSamuraiChefStateManager {

    private Long questionTime;

    public SushiSliceStateManager(Map<String, Slot> slots, AttributesManager attributesManager, SettingsDependencyContainer settingsDependencyContainer, PhraseDependencyContainer phraseDependencyContainer) {
        super(slots, attributesManager, settingsDependencyContainer, phraseDependencyContainer);
        currentActivity = SUSHI_SLICE;
    }

    @Override
    protected DialogItem.Builder handleSuccess(DialogItem.Builder builder) {

        long answerTime = System.currentTimeMillis();

        long answerLimit = this.stripe.getTimeLimitPhaseOneInMillis();

        if (questionTime == null || answerTime - questionTime < answerLimit) {

            builder = super.handleSuccess(builder);
        }
        else {
            builder = handleTooLongMistake(builder);
        }

        return builder;
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
        if (getSessionAttributes().containsKey(QUESTION_TIME)) {
            if ((Long) getSessionAttributes().get(QUESTION_TIME) < System.currentTimeMillis()) {
                getSessionAttributes().put(QUESTION_TIME, System.currentTimeMillis());
            }
        }
    }
}
