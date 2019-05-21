package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.activities.StateManager;
import com.muffinsoft.alexa.sdk.components.DialogTranslator;
import com.muffinsoft.alexa.sdk.components.IntentFactory;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.RegularPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;

import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.FALLBACK_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.FALLBACK_EVOKED;

public class AlternativeFallbackIntentHandler extends SamuraiGameIntentHandler {

    private final RegularPhraseManager regularPhraseManager;
    private final DialogTranslator dialogTranslator;

    public AlternativeFallbackIntentHandler(IntentFactory intentFactory, SettingsDependencyContainer configurationContainer, PhraseDependencyContainer phraseDependencyContainer) {
        super(intentFactory);
        this.regularPhraseManager = phraseDependencyContainer.getRegularPhraseManager();
        this.dialogTranslator = configurationContainer.getDialogTranslator();
    }

    @Override
    protected String getIntentName() {
        return "AMAZON.FallbackIntent";
    }

    @Override
    public StateManager nextTurn(HandlerInput input) {
        boolean contains = input.getAttributesManager().getSessionAttributes().containsKey("ANY_RESPONSE");

        if (contains) {
            input.getAttributesManager().getSessionAttributes().remove("ANY_RESPONSE");
            return super.nextTurn(input);
        }
        else {
            return new BaseStateManager(getSlotsFromInput(input), input.getAttributesManager(), dialogTranslator) {

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
}
