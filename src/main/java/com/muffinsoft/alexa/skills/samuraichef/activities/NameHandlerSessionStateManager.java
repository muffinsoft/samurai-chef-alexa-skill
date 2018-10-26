package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivitiesManager;
import com.muffinsoft.alexa.skills.samuraichef.content.LevelManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.RewardManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.muffinsoft.alexa.sdk.content.BaseConstants.USERNAME;
import static com.muffinsoft.alexa.sdk.model.SlotName.ACTION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.FALLBACK_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.INTRO_PHASE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.INTRO_PHASE_REPROMPT_PHRASE;
//import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.NAME_HANDLER;

public class NameHandlerSessionStateManager extends BaseSamuraiChefSessionStateManager {

    private static final Logger logger = LoggerFactory.getLogger(NameHandlerSessionStateManager.class);

    private String userName;

    public NameHandlerSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivitiesManager activitiesManager, LevelManager levelManager, PowerUpsManager powerUpsManager, RewardManager rewardManager) {
        super(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, rewardManager);
//        this.currentActivity = NAME_HANDLER;
    }

    @Override
    protected DialogItem getActivePhaseDialog() {
        return new DialogItem(phraseManager.getValueByKey(FALLBACK_PHRASE), false);
    }

    @Override
    protected void populateActivityVariables() {
        super.populateActivityVariables();
    }

    @Override
    protected void updateSessionAttributes() {
        super.updateSessionAttributes();
    }

    @Override
    public DialogItem nextResponse() {

        DialogItem dialogItem;

        if (userName == null) {
            dialogItem = new DialogItem(phraseManager.getValueByKey(INTRO_PHASE_PHRASE + 0), false, ACTION.text, true, phraseManager.getValueByKey(INTRO_PHASE_REPROMPT_PHRASE + 0));
        }
        else {
            sessionAttributes.put(USERNAME, userName);
            dialogItem = new DialogItem(phraseManager.getValueByKey(INTRO_PHASE_PHRASE + 1), false, ACTION.text);
        }

        return dialogItem;
    }
}
