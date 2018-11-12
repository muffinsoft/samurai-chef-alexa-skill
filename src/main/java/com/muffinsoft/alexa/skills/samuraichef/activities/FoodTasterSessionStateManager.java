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

import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.TOO_LONG_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.QUESTION_TIME;
import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.FOOD_TASTER;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_1;

public class FoodTasterSessionStateManager extends BaseActivePhaseSamuraiChefSessionStateManager {

    private static final Logger logger = LoggerFactory.getLogger(FoodTasterSessionStateManager.class);

    public FoodTasterSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivityManager activityManager, PowerUpsManager powerUpsManager, MissionManager missionManager) {
        super(slots, attributesManager, phraseManager, activityManager, powerUpsManager, missionManager);
        this.currentActivity = FOOD_TASTER;
    }
}
