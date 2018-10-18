package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
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

public class NameHandlerSessionStateManager extends BaseSamuraiChefSessionStateManager {

    private final ActivitiesManager activitiesManager;

    public NameHandlerSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivitiesManager activitiesManager) {
        super(slots, attributesManager, phraseManager, null);
        this.activitiesManager = activitiesManager;
        this.currentActivity = Activities.NAME_HANDLER;
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

        if (userName == null && userReply == null) {
            sessionAttributes.put(ACTIVITY, Activities.NAME_HANDLER);
            dialogItem = new DialogItem(phraseManager.getValueByKey(INTRO_PHRASE + 0), false, SlotName.NAME.text);
        }
        else {
            if (userReply != null) {
                sessionAttributes.put(USERNAME, userReply);
            }
            else {
                sessionAttributes.put(USERNAME, userName);
            }
            sessionAttributes.put(ACTIVITY, activitiesManager.getNextActivity(Activities.NAME_HANDLER));
            dialogItem = new DialogItem(phraseManager.getValueByKey(INTRO_PHRASE + 1), false, SlotName.ACTION.text);
        }

        return dialogItem;
    }
}
