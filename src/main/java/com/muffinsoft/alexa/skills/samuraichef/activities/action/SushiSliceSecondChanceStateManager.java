package com.muffinsoft.alexa.skills.samuraichef.activities.action;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;

import java.util.Map;

public class SushiSliceSecondChanceStateManager extends SushiSliceStateManager {

    public SushiSliceSecondChanceStateManager(Map<String, Slot> slots, AttributesManager attributesManager, SettingsDependencyContainer settingsDependencyContainer, PhraseDependencyContainer phraseDependencyContainer) {
        super(slots, attributesManager, settingsDependencyContainer, phraseDependencyContainer);
    }

    @Override
    protected DialogItem.Builder handleMistake(DialogItem.Builder builder) {
        return super.handleMistakeWithSecondChance(builder);
    }

    @Override
    protected DialogItem.Builder handleTooLongMistake(DialogItem.Builder builder) {
        return super.handleMistakeWithSecondChance(builder);
    }
}
