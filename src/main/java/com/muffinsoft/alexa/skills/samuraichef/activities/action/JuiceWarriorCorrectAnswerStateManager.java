package com.muffinsoft.alexa.skills.samuraichef.activities.action;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;

import java.util.Map;

public class JuiceWarriorCorrectAnswerStateManager extends JuiceWarriorStateManager {

    public JuiceWarriorCorrectAnswerStateManager(Map<String, Slot> slots, AttributesManager attributesManager, ConfigContainer configContainer) {
        super(slots, attributesManager, configContainer);
    }

    @Override
    protected DialogItem handleMistake() {
        return super.handleMistakeWithCorrectAnswer();
    }

    @Override
    protected DialogItem handleTooLongMistake() {
        return super.handleMistakeWithCorrectAnswer();
    }
}
