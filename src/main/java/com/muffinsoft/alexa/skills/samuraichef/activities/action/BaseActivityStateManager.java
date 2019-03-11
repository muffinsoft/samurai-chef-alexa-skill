package com.muffinsoft.alexa.skills.samuraichef.activities.action;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;

import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.QUESTION_TIME;

public class BaseActivityStateManager extends BaseActivePhaseSamuraiChefStateManager {

    private Long questionTime;

    BaseActivityStateManager(Map<String, Slot> slots, AttributesManager attributesManager, SettingsDependencyContainer settingsDependencyContainer, PhraseDependencyContainer phraseDependencyContainer) {
        super(slots, attributesManager, settingsDependencyContainer, phraseDependencyContainer);
    }

    @Override
    protected DialogItem.Builder handleSuccess(DialogItem.Builder builder) {

        if (isWithTimer()) {
            if (questionTime == null) {
                logger.warn("Round should be with timer, but there is empty question time value");
                return super.handleSuccess(builder);
            }
            else if ((System.currentTimeMillis() - questionTime) < stripe.getTimeLimitPhaseOneInMillis()) {
                logger.debug("It takes user " + (System.currentTimeMillis() - questionTime) / 1000 + " seconds to answer the question. Limit was " + (stripe.getTimeLimitPhaseOneInMillis() / 1000));
                return super.handleSuccess(builder);
            }
            else {
                return handleTooLongMistake(builder);
            }
        }
        else {
            return super.handleSuccess(builder);
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
        if (!getSessionAttributes().containsKey(QUESTION_TIME) || ((Long) getSessionAttributes().get(QUESTION_TIME) < System.currentTimeMillis())) {
            getSessionAttributes().put(QUESTION_TIME, System.currentTimeMillis());
        }

    }

    private boolean isWithTimer() {
        boolean timer = stripe.isWithTimer() && stripe.getTimeLimitPhaseOneInMillis() != null && stripe.getTimeLimitPhaseOneInMillis() > 0;
        logger.info("Round with timer: " + timer);
        return timer;
    }
}
