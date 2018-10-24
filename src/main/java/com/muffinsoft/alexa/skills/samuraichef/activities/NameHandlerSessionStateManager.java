package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivitiesManager;
import com.muffinsoft.alexa.skills.samuraichef.content.LevelManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.muffinsoft.alexa.sdk.content.BaseConstants.USERNAME;
import static com.muffinsoft.alexa.sdk.model.SlotName.ACTION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.FALLBACK_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.INTRO_PHASE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.INTRO_PHASE_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.FIRST_TIME_ASKING;
//import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.NAME_HANDLER;

public class NameHandlerSessionStateManager extends BaseSamuraiChefSessionStateManager {

    private static final Logger logger = LoggerFactory.getLogger(NameHandlerSessionStateManager.class);

    private boolean firstTimeAsking;

    public NameHandlerSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivitiesManager activitiesManager, LevelManager levelManager, PowerUpsManager powerUpsManager) {
        super(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager);
//        this.currentActivity = NAME_HANDLER;
    }

    @Override
    protected DialogItem getActivePhaseDialog() {
        return new DialogItem(phraseManager.getValueByKey(FALLBACK_PHRASE), false);
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
//            sessionAttributes.put(ACTIVITY, NAME_HANDLER);
            if (firstTimeAsking) {
                dialogItem = new DialogItem(phraseManager.getValueByKey(INTRO_PHASE_PHRASE + 0), false, ACTION.text, true, phraseManager.getValueByKey(INTRO_PHASE_REPROMPT_PHRASE + 0));
                firstTimeAsking = false;
            }
            else {
                dialogItem = new DialogItem(phraseManager.getValueByKey(INTRO_PHASE_PHRASE + 1), false, ACTION.text, true, phraseManager.getValueByKey(INTRO_PHASE_REPROMPT_PHRASE + 0));
            }
        }
        else {
            sessionAttributes.put(USERNAME, userName);
//            sessionAttributes.put(ACTIVITY, activitiesManager.getNextActivity(NAME_HANDLER));
            dialogItem = new DialogItem(phraseManager.getValueByKey(INTRO_PHASE_PHRASE + 2), false, ACTION.text);
        }

        return dialogItem;
    }
}
