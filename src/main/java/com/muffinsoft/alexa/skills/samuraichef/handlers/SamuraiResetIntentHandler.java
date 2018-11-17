package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.activities.StateManager;
import com.muffinsoft.alexa.sdk.handlers.ResetIntentHandler;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Intents;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;

import static com.muffinsoft.alexa.sdk.model.Speech.ofText;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WANT_RESET_PROGRESS_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;

public class SamuraiResetIntentHandler extends ResetIntentHandler {

    private final PhraseManager phraseManager;

    public SamuraiResetIntentHandler(ConfigContainer configurationContainer) {
        super();
        this.phraseManager = configurationContainer.getPhraseManager();
    }

    @Override
    public StateManager nextTurn(HandlerInput handlerInput) {
        return new BaseStateManager(getSlotsFromInput(handlerInput), handlerInput.getAttributesManager()) {

            @Override
            public DialogItem nextResponse() {

                logger.debug("Available session attributes: " + getSessionAttributes());

                String dialog = phraseManager.getValueByKey(WANT_RESET_PROGRESS_PHRASE);
                getSessionAttributes().put(INTENT, Intents.RESET);

                DialogItem.Builder builder = DialogItem.builder().addResponse(ofText(dialog));

                return builder.build();
            }
        };
    }
}
