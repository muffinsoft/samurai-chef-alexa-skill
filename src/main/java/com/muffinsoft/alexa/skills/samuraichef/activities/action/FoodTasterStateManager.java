package com.muffinsoft.alexa.skills.samuraichef.activities.action;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;

import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.FOOD_TASTER;

public class FoodTasterStateManager extends BaseActivePhaseSamuraiChefStateManager {

    public FoodTasterStateManager(Map<String, Slot> slots, AttributesManager attributesManager, ConfigContainer configContainer) {
        super(slots, attributesManager, configContainer);
        this.currentActivity = FOOD_TASTER;
    }
}
