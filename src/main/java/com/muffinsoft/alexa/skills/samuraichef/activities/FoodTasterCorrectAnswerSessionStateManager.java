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

public class FoodTasterCorrectAnswerSessionStateManager extends FoodTasterSessionStateManager {

    private static final Logger logger = LoggerFactory.getLogger(FoodTasterCorrectAnswerSessionStateManager.class);

    public FoodTasterCorrectAnswerSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivityManager activityManager, PowerUpsManager powerUpsManager, MissionManager missionManager) {
        super(slots, attributesManager, phraseManager, activityManager, powerUpsManager, missionManager);
    }

    @Override
    protected DialogItem handleMistake() {
        return super.handleMistakeWithCorrectAnswer();
    }
}
