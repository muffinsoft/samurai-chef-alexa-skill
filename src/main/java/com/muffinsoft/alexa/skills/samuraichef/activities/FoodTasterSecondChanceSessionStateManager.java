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

public class FoodTasterSecondChanceSessionStateManager extends FoodTasterSessionStateManager {

    private static final Logger logger = LoggerFactory.getLogger(FoodTasterSecondChanceSessionStateManager.class);

    public FoodTasterSecondChanceSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivitiesManager activitiesManager, LevelManager levelManager, PowerUpsManager powerUpsManager, ProgressManager progressManager) {
        super(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, progressManager);
    }

    @Override
    protected DialogItem handleMistake() {
        return super.handleMistakeWithSecondChance();
    }
}
