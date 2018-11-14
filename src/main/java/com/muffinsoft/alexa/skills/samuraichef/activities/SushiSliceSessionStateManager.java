package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivityManager;
import com.muffinsoft.alexa.skills.samuraichef.content.MissionManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.AliasManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.TOO_LONG_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.QUESTION_TIME;
import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.SUSHI_SLICE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_1;

public class SushiSliceSessionStateManager extends BaseActivePhaseSamuraiChefSessionStateManager {

    protected Long questionTime;

    public SushiSliceSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivityManager activityManager, AliasManager aliasManager, MissionManager missionManager) {
        super(slots, attributesManager, phraseManager, activityManager, aliasManager, missionManager);
        currentActivity = SUSHI_SLICE;
    }

    @Override
    protected DialogItem handleSuccess() {

        long answerTime = System.currentTimeMillis();

        long answerLimit = this.statePhase == PHASE_1 ? this.stripe.getTimeLimitPhaseOneInMillis() : this.stripe.getTimeLimitPhaseTwoInMillis();

        if (questionTime == null || answerTime - questionTime < answerLimit) {

            return super.handleSuccess();
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
