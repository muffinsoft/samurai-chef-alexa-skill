package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.components.IntentFactory;
import com.muffinsoft.alexa.sdk.model.SlotName;

import java.util.Map;

public class SamuraiYesIntentHandler extends SamuraiGameIntentHandler {

    public SamuraiYesIntentHandler(IntentFactory intentFactory) {
        super(intentFactory);
    }

    @Override
    protected String getIntentName() {
        return "AMAZON.YesIntent";
    }

    @Override
    protected boolean shouldAppendSlotValues() {
        return true;
    }

    @Override
    protected void appendValuesToSlots(Map<String, Slot> slots) {
        Slot.Builder slotBuilder = Slot.builder().withValue("yes");
        slots.put(SlotName.CONFIRMATION.text, slotBuilder.build());
    }
}
