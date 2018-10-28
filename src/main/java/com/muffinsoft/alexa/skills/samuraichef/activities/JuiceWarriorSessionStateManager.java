package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivitiesManager;
import com.muffinsoft.alexa.skills.samuraichef.content.LevelManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.ProgressManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.TOO_LONG_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.QUESTION_TIME;
import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.JUICE_WARRIOR;

public class JuiceWarriorSessionStateManager extends BaseActivePhaseSamuraiChefSessionStateManager {

    private static final Logger logger = LoggerFactory.getLogger(JuiceWarriorSessionStateManager.class);

    protected Long questionTime;

    public JuiceWarriorSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivitiesManager activitiesManager, LevelManager levelManager, PowerUpsManager powerUpsManager, ProgressManager progressManager) {
        super(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, progressManager);
        this.currentActivity = JUICE_WARRIOR;
    }

    @Override
    protected DialogItem handleSuccess() {

        long answerTime = System.currentTimeMillis();

        long answerLimit = level.getTimeLimitPhaseOneInMillis();

        if (questionTime == null || answerTime - questionTime < answerLimit) {

            this.activityProgress.iterateSuccessCount();

            return getSuccessDialog();
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
