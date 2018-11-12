package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivityManager;
import com.muffinsoft.alexa.skills.samuraichef.content.MissionManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.AliasManager;

import java.util.Map;

public class WordBoardKarateCorrectAnswerSessionStateManager extends WordBoardKarateSessionStateManager {

    public WordBoardKarateCorrectAnswerSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivityManager activityManager, AliasManager aliasManager, MissionManager missionManager) {
        super(slots, attributesManager, phraseManager, activityManager, aliasManager, missionManager);
    }

    @Override
    protected DialogItem handleMistake() {
        return super.handleMistakeWithCorrectAnswer();
    }
}
