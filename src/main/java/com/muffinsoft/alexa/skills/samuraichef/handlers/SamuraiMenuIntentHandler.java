package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.activities.StateManager;
import com.muffinsoft.alexa.sdk.components.DialogTranslator;
import com.muffinsoft.alexa.sdk.enums.IntentType;
import com.muffinsoft.alexa.sdk.handlers.BaseRedirectionIntentHandler;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.PhraseContainer;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.RegularPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static com.amazon.ask.request.Predicates.intentName;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.MISSION_SELECTION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.FINISHED_MISSIONS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;

public class SamuraiMenuIntentHandler extends BaseRedirectionIntentHandler {

    private final RegularPhraseManager regularPhraseManager;
    private final DialogTranslator dialogTranslator;

    public SamuraiMenuIntentHandler(SettingsDependencyContainer configurationContainer, PhraseDependencyContainer phraseDependencyContainer) {
        super();
        this.regularPhraseManager = phraseDependencyContainer.getRegularPhraseManager();
        this.dialogTranslator = configurationContainer.getDialogTranslator();
    }

    @Override
    public StateManager nextTurn(HandlerInput handlerInput) {
        return new BaseStateManager(getSlotsFromInput(handlerInput), handlerInput.getAttributesManager(), dialogTranslator) {

            @Override
            public DialogItem nextResponse() {

                if (getPersistentAttributes().containsKey(FINISHED_MISSIONS)) {
                    LinkedHashSet finishedMission = (LinkedHashSet) getPersistentAttributes().getOrDefault(FINISHED_MISSIONS, new LinkedHashSet<>());
                    List<String> result = new ArrayList<>();
                    for (Object o : finishedMission) {
                        result.add(String.valueOf(o));
                    }
                    getSessionAttributes().put(FINISHED_MISSIONS, result);
                }
                if (!getSessionAttributes().containsKey(FINISHED_MISSIONS)) {
                    LinkedHashSet finishedMission = (LinkedHashSet) getPersistentAttributes().getOrDefault(FINISHED_MISSIONS, new LinkedHashSet<>());
                    List<String> result = new ArrayList<>();
                    for (Object o : finishedMission) {
                        result.add(String.valueOf(o));
                    }
                    getSessionAttributes().put(FINISHED_MISSIONS, result);
                }

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

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("MenuIntent"));
    }
}
