package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.activities.StateManager;
import com.muffinsoft.alexa.sdk.components.DialogTranslator;
import com.muffinsoft.alexa.sdk.enums.IntentType;
import com.muffinsoft.alexa.sdk.handlers.StopIntentHandler;
import com.muffinsoft.alexa.sdk.model.BasePhraseContainer;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.PhraseContainer;
import com.muffinsoft.alexa.skills.samuraichef.constants.GreetingsPhraseConstants;
import com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.GreetingsPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;

import java.util.List;

public class SamuraiStopIntentHandler extends StopIntentHandler {

    private final GreetingsPhraseManager greetingsPhraseManager;
    private final DialogTranslator dialogTranslator;

    public SamuraiStopIntentHandler(SettingsDependencyContainer configurationContainer, PhraseDependencyContainer phraseDependencyContainer) {
        super();
        this.greetingsPhraseManager = phraseDependencyContainer.getGreetingsPhraseManager();
        this.dialogTranslator = configurationContainer.getDialogTranslator();
    }

    @Override
    protected List<PhraseContainer> getPhrase() {
        return null;
    }

    @Override
    public StateManager nextTurn(HandlerInput handlerInput) {
        return new BaseStateManager(getSlotsFromInput(handlerInput), handlerInput.getAttributesManager(), dialogTranslator) {

            private void buildExit(DialogItem.Builder builder) {

                List<BasePhraseContainer> dialog = greetingsPhraseManager.getValueByKey(GreetingsPhraseConstants.EXIT_PHRASE);

                int userReplyBreakpointPosition = 0;

                for (BasePhraseContainer BasePhraseContainer : dialog) {

                    if (BasePhraseContainer.isUserResponse()) {
                        this.getSessionAttributes().put(SessionConstants.USER_REPLY_BREAKPOINT, userReplyBreakpointPosition + 1);
                        this.getSessionAttributes().put(SessionConstants.INTENT, IntentType.EXIT_CONFIRMATION);
                        break;
                    }
                    builder.addResponse(getDialogTranslator().translate(BasePhraseContainer));
                    userReplyBreakpointPosition++;
                }
            }

            @Override
            public DialogItem nextResponse() {

                DialogItem.Builder builder = DialogItem.builder();

                buildExit(builder);

                return builder.shouldEnd().build();
            }
        };
    }
}
