package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.activities.StateManager;
import com.muffinsoft.alexa.sdk.handlers.CancelIntentHandler;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Intents;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseSettings;

import static com.muffinsoft.alexa.skills.samuraichef.components.VoiceTranslator.translate;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WANT_START_MISSION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;

public class SamuraiCancelIntentHandler extends CancelIntentHandler {

    private final PhraseManager phraseManager;

    public SamuraiCancelIntentHandler(ConfigContainer configurationContainer) {
        super();
        this.phraseManager = configurationContainer.getPhraseManager();
    }

    @Override
    public StateManager nextTurn(HandlerInput handlerInput) {
        return new BaseStateManager(getSlotsFromInput(handlerInput), handlerInput.getAttributesManager()) {

            @Override
            public DialogItem nextResponse() {

                logger.debug("Available session attributes: " + getSessionAttributes());

                PhraseSettings dialog = phraseManager.getValueByKey(WANT_START_MISSION_PHRASE);
                getSessionAttributes().put(INTENT, Intents.CANCEL);

                DialogItem.Builder builder = DialogItem.builder().addResponse(translate(dialog));

                return builder.build();
            }
        };
    }
}
