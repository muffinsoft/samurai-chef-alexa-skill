package com.muffinsoft.alexa.skills.samuraichef.activities.action;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;

import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.WORD_BOARD_KARATE;

public class WordBoardKarateStateManager extends BaseActivityStateManager {

    public WordBoardKarateStateManager(Map<String, Slot> slots, AttributesManager attributesManager, SettingsDependencyContainer settingsDependencyContainer, PhraseDependencyContainer phraseDependencyContainer) {
        super(slots, attributesManager, settingsDependencyContainer, phraseDependencyContainer);
        this.currentActivity = WORD_BOARD_KARATE;
    }
}
