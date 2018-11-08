package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.LevelManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.ProgressManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.muffinsoft.alexa.sdk.model.SlotName.ACTION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.FALLBACK_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.INTRO_PHASE_PHRASE;

public class NameHandlerSessionStateManager extends BaseSamuraiChefSessionStateManager {

    private static final Logger logger = LoggerFactory.getLogger(NameHandlerSessionStateManager.class);

    public NameHandlerSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, LevelManager levelManager, PowerUpsManager powerUpsManager, ProgressManager progressManager) {
        super(slots, attributesManager, phraseManager, levelManager, powerUpsManager, progressManager);
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

        return new DialogItem(phraseManager.getValueByKey(INTRO_PHASE_PHRASE + 1), false, ACTION.text);
    }
}
