package com.muffinsoft.alexa.skills.samuraichef.game;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.game.BaseSessionStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;

import java.util.Map;

public class SushiSliceSessionStateManager extends BaseSessionStateManager {

    public SushiSliceSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager) {
        super(slots, attributesManager);
    }

    @Override
    protected void initializeGame() {

    }

    @Override
    protected void populateFields() {

    }

    @Override
    public DialogItem nextResponse() {
        return null;
    }
}
