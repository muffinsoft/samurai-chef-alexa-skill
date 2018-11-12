package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivityManager;
import com.muffinsoft.alexa.skills.samuraichef.content.MissionManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;

import java.util.Map;

public class JuiceWarriorSecondChanceSessionStateManager extends JuiceWarriorSessionStateManager {

    public JuiceWarriorSecondChanceSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivityManager activityManager, PowerUpsManager powerUpsManager, MissionManager missionManager) {
        super(slots, attributesManager, phraseManager, activityManager, powerUpsManager, missionManager);
    }

    @Override
    protected DialogItem handleMistake() {
        return super.handleMistakeWithSecondChance();
    }
}
