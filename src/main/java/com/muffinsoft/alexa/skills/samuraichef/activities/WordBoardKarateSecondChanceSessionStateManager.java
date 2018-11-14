package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivityManager;
import com.muffinsoft.alexa.skills.samuraichef.content.AliasManager;
import com.muffinsoft.alexa.skills.samuraichef.content.MissionManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;

import java.util.Map;

public class WordBoardKarateSecondChanceSessionStateManager extends WordBoardKarateSessionStateManager {

    public WordBoardKarateSecondChanceSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivityManager activityManager, AliasManager aliasManager, MissionManager missionManager, String userId) {
        super(slots, attributesManager, phraseManager, activityManager, aliasManager, missionManager, userId);
    }

    @Override
    protected DialogItem handleMistake() {
        return super.handleMistakeWithSecondChance();
    }
}
