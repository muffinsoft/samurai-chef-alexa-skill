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

import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.SUSHI_SLICE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_2;

public class SushiSliceSessionStateManager extends BaseActivePhaseSamuraiChefSessionStateManager {

    private static final Logger logger = LoggerFactory.getLogger(SushiSliceSessionStateManager.class);

    public SushiSliceSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivitiesManager activitiesManager, LevelManager levelManager, PowerUpsManager powerUpsManager, RewardManager rewardManager) {
        super(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, rewardManager);
        currentActivity = SUSHI_SLICE;
    }

    @Override
    protected DialogItem handleSuccess() {

        this.activityProgress.iterateSuccessCount();

        if (this.activityProgress.getSuccessCount() == level.getPhaseTwoSuccessCount()) {
            this.statePhase = PHASE_2;
            Speech speech = levelManager.getSpeechForActivityByNumber(this.currentActivity, this.userProgress.getCurrentLevel());
            return getSuccessDialog(speech.getMoveToPhaseTwo());
        }
        else {
            return getSuccessDialog();
        }
    }
}
