package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.activities.StateManager;
import com.muffinsoft.alexa.sdk.components.DialogTranslator;
import com.muffinsoft.alexa.sdk.enums.IntentType;
import com.muffinsoft.alexa.sdk.handlers.StartOverIntentHandler;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.PhraseContainer;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.RegularPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;

import java.util.List;

import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.WANT_RESET_PROGRESS_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;

public class SamuraiStartOverIntentHandler extends StartOverIntentHandler {

    private final RegularPhraseManager regularPhraseManager;
    private final DialogTranslator dialogTranslator;

    public SamuraiStartOverIntentHandler(SettingsDependencyContainer configurationContainer, PhraseDependencyContainer phraseDependencyContainer) {
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
            public DialogItem nextResponse() {

                logger.debug("Available session attributes: " + getSessionAttributes());

                List<PhraseContainer> dialog = regularPhraseManager.getValueByKey(WANT_RESET_PROGRESS_PHRASE);
                getSessionAttributes().put(INTENT, IntentType.RESET);

                DialogItem.Builder builder = DialogItem.builder().addResponse(getDialogTranslator().translate(dialog));

                return builder.build();
            }
        };
    }
}
