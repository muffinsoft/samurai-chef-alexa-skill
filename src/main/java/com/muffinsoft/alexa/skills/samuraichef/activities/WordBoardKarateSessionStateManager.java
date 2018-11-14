package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivityManager;
import com.muffinsoft.alexa.skills.samuraichef.content.AliasManager;
import com.muffinsoft.alexa.skills.samuraichef.content.MissionManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;

import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.WORD_BOARD_KARATE;

public class WordBoardKarateSessionStateManager extends BaseActivePhaseSamuraiChefSessionStateManager {

    public WordBoardKarateSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivityManager activityManager, AliasManager aliasManager, MissionManager missionManager, String userId) {
        super(slots, attributesManager, phraseManager, activityManager, aliasManager, missionManager, userId);
        this.currentActivity = WORD_BOARD_KARATE;
    }
}
