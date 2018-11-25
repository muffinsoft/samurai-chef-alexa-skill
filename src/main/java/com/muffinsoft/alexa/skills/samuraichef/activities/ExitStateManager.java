package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.components.UserReplyComparator;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Intents;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;
import com.muffinsoft.alexa.skills.samuraichef.models.ActivityProgress;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.muffinsoft.alexa.sdk.model.Speech.ofAlexa;
import static com.muffinsoft.alexa.skills.samuraichef.components.VoiceTranslator.translate;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.EXIT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.REPEAT_LAST_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.RETURN_TO_GAME_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.QUESTION_TIME;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.MISSION_INTRO;

public class ExitStateManager extends BaseStateManager {

    private static final Logger logger = LogManager.getLogger(CancelStateManager.class);

    private final PhraseManager phraseManager;

    private StatePhase statePhase;
    private ActivityProgress activityProgress;

    public ExitStateManager(Map<String, Slot> inputSlots, AttributesManager attributesManager, ConfigContainer configContainer) {
        super(inputSlots, attributesManager);
        this.phraseManager = configContainer.getPhraseManager();
    }

    @Override
    protected void populateActivityVariables() {
        statePhase = StatePhase.valueOf(String.valueOf(getSessionAttributes().getOrDefault(STATE_PHASE, MISSION_INTRO)));
        LinkedHashMap rawActivityProgress = (LinkedHashMap) getSessionAttributes().get(ACTIVITY_PROGRESS);
        activityProgress = rawActivityProgress != null ? mapper.convertValue(rawActivityProgress, ActivityProgress.class) : new ActivityProgress();
    }

    @Override
    public DialogItem nextResponse() {

        logger.debug("Available session attributes: " + getSessionAttributes());

        DialogItem.Builder builder = DialogItem.builder();

        if (UserReplyComparator.compare(getUserReply(), UserReplies.YES)) {
            builder.addResponse(translate(phraseManager.getValueByKey(EXIT_PHRASE)));
            builder.withShouldEnd(true);
        }
        else if (UserReplyComparator.compare(getUserReply(), UserReplies.NO)) {
            getSessionAttributes().put(INTENT, Intents.GAME);
            builder.addResponse(translate(phraseManager.getValueByKey(RETURN_TO_GAME_PHRASE)));
            if (statePhase == StatePhase.PHASE_1 || statePhase == StatePhase.PHASE_2) {
                builder.addResponse(ofAlexa(activityProgress.getPreviousIngredient()));
            }
            getSessionAttributes().put(QUESTION_TIME, System.currentTimeMillis());
        }
        else {
            builder.addResponse(translate(phraseManager.getValueByKey(REPEAT_LAST_PHRASE)));
        }

        return builder.build();
    }
}
