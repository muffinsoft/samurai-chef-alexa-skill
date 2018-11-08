package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseSessionStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;

import java.util.HashMap;
import java.util.Map;

public class SelectLevelStateManager extends BaseSessionStateManager {

    public SelectLevelStateManager(Map<String, Slot> slots, AttributesManager attributesManager) {
        super(slots, attributesManager);
    }

    @Override
    protected void initializeSessionAttributes() {
        this.sessionAttributes = new HashMap<>();
    }

    @Override
    protected void populateActivityVariables() {

    }

    @Override
    protected void updateSessionAttributes() {

    }

    @Override
    protected void updatePersistentAttributes() {

    }

    @Override
    public DialogItem nextResponse() {
        String dialog = "Please, select the level";
        return new DialogItem(dialog, false, actionSlotName);
    }
}
