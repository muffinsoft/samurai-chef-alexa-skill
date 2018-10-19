package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivitiesManager;
import com.muffinsoft.alexa.skills.samuraichef.content.IngredientsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;

import java.util.Map;

import static com.muffinsoft.alexa.sdk.content.BaseConstants.USERNAME;
import static com.muffinsoft.alexa.sdk.model.SlotName.ACTION;
import static com.muffinsoft.alexa.skills.samuraichef.content.SamuraiChefSessionConstants.ACTIVITY;
import static com.muffinsoft.alexa.skills.samuraichef.content.SamuraiChefSessionConstants.FIRST_TIME_ASKING;
import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.NAME_HANDLER;

public class NameHandlerSessionStateManager extends BaseSamuraiChefSessionStateManager {

    private boolean firstTimeAsking;

    public NameHandlerSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, IngredientsManager ingredientsManager, ActivitiesManager activitiesManager) {
        super(slots, attributesManager, phraseManager, ingredientsManager, activitiesManager);
        this.currentActivity = NAME_HANDLER;
    }

    @Override
    protected DialogItem getActivePhaseDialog() {
        return new DialogItem(phraseManager.getValueByKey("fallback"), false);
    }

    @Override
    protected void populateActivityVariables() {
        super.populateActivityVariables();
        this.firstTimeAsking = (boolean) sessionAttributes.getOrDefault(FIRST_TIME_ASKING, true);
    }

    @Override
    protected void updateSessionAttributes() {
        super.updateSessionAttributes();
        sessionAttributes.put(FIRST_TIME_ASKING, firstTimeAsking);
    }

    @Override
    public DialogItem nextResponse() {

        DialogItem dialogItem;

        if (userName == null) {
            sessionAttributes.put(ACTIVITY, NAME_HANDLER);
            if (firstTimeAsking) {
                dialogItem = new DialogItem(phraseManager.getValueByKey("intro" + 0), false, ACTION.text);
                firstTimeAsking = false;
            }
            else {
                dialogItem = new DialogItem(phraseManager.getValueByKey("intro" + 1), false, ACTION.text);
            }
        }
        else {
            sessionAttributes.put(USERNAME, userName);
            sessionAttributes.put(ACTIVITY, activitiesManager.getNextActivity(NAME_HANDLER));
            dialogItem = new DialogItem(phraseManager.getValueByKey("intro" + 2), false, ACTION.text);
        }

        return dialogItem;
    }
}
