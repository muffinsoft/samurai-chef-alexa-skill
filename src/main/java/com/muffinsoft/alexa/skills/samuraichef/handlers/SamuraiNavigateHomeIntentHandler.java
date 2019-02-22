package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.activities.StateManager;
import com.muffinsoft.alexa.sdk.components.DialogTranslator;
import com.muffinsoft.alexa.sdk.enums.IntentType;
import com.muffinsoft.alexa.sdk.handlers.NavigateHomeIntentHandler;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.PhraseContainer;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.RegularPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;

import java.util.List;

import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.MISSION_SELECTION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;

public class SamuraiNavigateHomeIntentHandler extends NavigateHomeIntentHandler {

    private final RegularPhraseManager regularPhraseManager;
    private final DialogTranslator dialogTranslator;


    public SamuraiNavigateHomeIntentHandler(SettingsDependencyContainer configurationContainer, PhraseDependencyContainer phraseDependencyContainer) {
        super();
        this.regularPhraseManager = phraseDependencyContainer.getRegularPhraseManager();
        this.dialogTranslator = configurationContainer.getDialogTranslator();
    }

    @Override
    public StateManager nextTurn(HandlerInput handlerInput) {
        return new BaseStateManager(getSlotsFromInput(handlerInput), handlerInput.getAttributesManager(), dialogTranslator) {

            @Override
            public DialogItem nextResponse() {

                List<PhraseContainer> dialog = regularPhraseManager.getValueByKey(MISSION_SELECTION_PHRASE);
                getSessionAttributes().put(INTENT, IntentType.CANCEL);

                DialogItem.Builder builder = DialogItem.builder().addResponse(getDialogTranslator().translate(dialog));

                return builder.build();
            }
        };
    }

    @Override
    protected List<PhraseContainer> getPhrase() {
        return null;
    }

    @Override
    protected IntentType getIntentType() {
        return IntentType.CANCEL;
    }
}
