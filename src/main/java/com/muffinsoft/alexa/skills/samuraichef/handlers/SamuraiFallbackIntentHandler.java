package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.activities.StateManager;
import com.muffinsoft.alexa.sdk.components.DialogTranslator;
import com.muffinsoft.alexa.sdk.handlers.FallbackIntentHandler;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.PhraseContainer;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.RegularPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;

import java.util.List;

import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.FALLBACK_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.FALLBACK_EVOKED;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STATE_PHASE;

public class SamuraiFallbackIntentHandler extends FallbackIntentHandler {

    private final RegularPhraseManager regularPhraseManager;
    private final DialogTranslator dialogTranslator;

    public SamuraiFallbackIntentHandler(SettingsDependencyContainer configurationContainer, PhraseDependencyContainer phraseDependencyContainer) {
        super();
        this.regularPhraseManager = phraseDependencyContainer.getRegularPhraseManager();
        this.dialogTranslator = configurationContainer.getDialogTranslator();
    }

    @Override
    protected List<PhraseContainer> getPhrase() {
        return null;
    }

    @Override
    public StateManager nextTurn(HandlerInput handlerInput) {
        return new BaseStateManager(getSlotsFromInput(handlerInput), handlerInput.getAttributesManager(), dialogTranslator) {

            @Override
            protected void updateSessionAttributes() {
                getSessionAttributes().put(FALLBACK_EVOKED, true);
            }

            @Override
            public DialogItem nextResponse() {
                return DialogItem.builder()
                        .addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(FALLBACK_PHRASE)))
                        .build();
            }
        };
    }
}
