package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivityManager;
import com.muffinsoft.alexa.skills.samuraichef.content.MissionManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.SUSHI_SLICE;

public class SushiSliceSessionStateManager extends BaseActivePhaseSamuraiChefSessionStateManager {

    private static final Logger logger = LoggerFactory.getLogger(SushiSliceSessionStateManager.class);

    public SushiSliceSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivityManager activityManager, PowerUpsManager powerUpsManager, MissionManager missionManager) {
        super(slots, attributesManager, phraseManager, activityManager, powerUpsManager, missionManager);
        currentActivity = SUSHI_SLICE;
    }

    @Override
    protected DialogItem handleSuccess() {

        this.activityProgress.iterateSuccessCount();

//        if (this.activityProgress.getSuccessCount() == stripe.getPhaseTwoSuccessCount()) {
//            this.statePhase = PHASE_2;
//            Speech speech = activityManager.getSpeechForActivityByStripeNumber(this.currentActivity, this.userProgress.getStripeCount());
//            return getSuccessDialog(speech.getMoveToPhaseTwo());
//        }
//        else {
        return getSuccessDialog();
//        }
    }
}
