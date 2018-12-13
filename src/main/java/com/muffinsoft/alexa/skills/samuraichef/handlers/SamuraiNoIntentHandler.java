package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.components.IntentFactory;
import com.muffinsoft.alexa.sdk.model.SlotName;

import java.util.Map;

public class SamuraiNoIntentHandler extends SamuraiGameIntentHandler {

    public SamuraiNoIntentHandler(IntentFactory intentFactory) {
        super(intentFactory);
    }

    @Override
    protected String getIntentName() {
        return "AMAZON.NoIntent";
    }

    @Override
    protected boolean shouldAppendSlotValues() {
        return true;
    }

    @Override
    protected void appendValuesToSlots(Map<String, Slot> slots) {
        Slot.Builder slotBuilder = Slot.builder().withValue("no");
        slots.put(SlotName.CONFIRMATION.text, slotBuilder.build());
    }

}

