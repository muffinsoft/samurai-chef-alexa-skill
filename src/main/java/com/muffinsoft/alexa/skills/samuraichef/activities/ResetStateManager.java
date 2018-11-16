package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.Speech;
import com.muffinsoft.alexa.skills.samuraichef.components.UserReplyComparator;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Intents;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.REPEAT_LAST_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.RETURN_TO_GAME_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WANT_MISSION_FROM_BEGINNING_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;

public class ResetStateManager extends BaseStateManager {

    private static final Logger logger = LogManager.getLogger(CancelStateManager.class);

    private final PhraseManager phraseManager;

    public ResetStateManager(Map<String, Slot> inputSlots, AttributesManager attributesManager, ConfigContainer configContainer) {
        super(inputSlots, attributesManager);
        this.phraseManager = configContainer.getPhraseManager();
    }

    @Override
    public DialogItem nextResponse() {
        logger.debug("Available session attributes: " + getSessionAttributes());

        logger.debug("User reply: " + getUserReply());

        String dialog;

        if (UserReplyComparator.compare(getUserReply(), UserReplies.NO)) {
            dialog = phraseManager.getValueByKey(RETURN_TO_GAME_PHRASE);
            getSessionAttributes().put(INTENT, Intents.GAME);
        }
        else if (UserReplyComparator.compare(getUserReply(), UserReplies.YES)) {
            dialog = phraseManager.getValueByKey(WANT_MISSION_FROM_BEGINNING_PHRASE);
            getSessionAttributes().put(INTENT, Intents.RESET_CONFIRMATION);
        }
        else {
            dialog = phraseManager.getValueByKey(REPEAT_LAST_PHRASE);
        }

        DialogItem.Builder builder = DialogItem.builder().withResponse(Speech.ofText(dialog));

        return builder.build();
    }
}
