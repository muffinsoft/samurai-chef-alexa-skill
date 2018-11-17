package com.muffinsoft.alexa.skills.samuraichef.activities.action;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;

import java.util.Map;

public class SushiSliceCorrectAnswerStateManager extends SushiSliceStateManager {

    public SushiSliceCorrectAnswerStateManager(Map<String, Slot> slots, AttributesManager attributesManager, ConfigContainer configContainer) {
        super(slots, attributesManager, configContainer);
    }

    @Override
    protected DialogItem.Builder handleMistake(DialogItem.Builder builder) {
        return super.handleMistakeWithCorrectAnswer(builder);
    }

    @Override
    protected DialogItem.Builder handleTooLongMistake(DialogItem.Builder builder) {
        return super.handleMistakeWithCorrectAnswer(builder);
    }
}
