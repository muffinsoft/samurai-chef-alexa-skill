package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseSessionStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.SlotName;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivitiesManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;

import java.util.HashMap;
import java.util.Map;

import static com.muffinsoft.alexa.sdk.content.BaseConstants.USERNAME;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.ACTIVITY;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.INTRO_PHRASE;

public class NameHandlerSessionStateManager extends BaseSessionStateManager {

    private final PhraseManager phraseManager;
    private final ActivitiesManager activitiesManager;

    public NameHandlerSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivitiesManager activitiesManager) {
        super(slots, attributesManager);
        this.phraseManager = phraseManager;
        this.activitiesManager = activitiesManager;
    }

    @Override
    protected void initializeSessionAttributes() {
        sessionAttributes = new HashMap<>();
    }

    @Override
    protected void populateActivityVariables() {

    }

    @Override
    protected void updateSessionAttributes() {

    }

    @Override
    public DialogItem nextResponse() {

        DialogItem dialogItem;

        if (userReply == null) {
            dialogItem = new DialogItem(phraseManager.getValueByKey(INTRO_PHRASE + 0), false, SlotName.ACTION.text);
        }
        else {
            sessionAttributes.put(USERNAME, userReply);
            sessionAttributes.put(ACTIVITY, activitiesManager.getNextActivity(Activities.NAME_HANDLER));
            dialogItem = new DialogItem(phraseManager.getValueByKey(INTRO_PHRASE + 1), true, SlotName.ACTION.text);
        }
        return dialogItem;
    }
}
