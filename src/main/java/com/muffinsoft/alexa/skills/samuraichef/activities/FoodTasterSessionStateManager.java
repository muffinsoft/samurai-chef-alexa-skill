package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivityManager;
import com.muffinsoft.alexa.skills.samuraichef.content.MissionManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.AliasManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.FOOD_TASTER;

public class FoodTasterSessionStateManager extends BaseActivePhaseSamuraiChefSessionStateManager {

    public FoodTasterSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivityManager activityManager, AliasManager aliasManager, MissionManager missionManager) {
        super(slots, attributesManager, phraseManager, activityManager, aliasManager, missionManager);
        this.currentActivity = FOOD_TASTER;
    }
}
