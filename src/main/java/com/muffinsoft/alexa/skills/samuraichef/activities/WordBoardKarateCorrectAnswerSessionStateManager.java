package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.LevelManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.ProgressManager;

import java.util.Map;

public class WordBoardKarateCorrectAnswerSessionStateManager extends WordBoardKarateSessionStateManager {

    public WordBoardKarateCorrectAnswerSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, LevelManager levelManager, PowerUpsManager powerUpsManager, ProgressManager progressManager) {
        super(slots, attributesManager, phraseManager, levelManager, powerUpsManager, progressManager);
    }

    @Override
    protected DialogItem handleMistake() {
        return super.handleMistakeWithCorrectAnswer();
    }
}
