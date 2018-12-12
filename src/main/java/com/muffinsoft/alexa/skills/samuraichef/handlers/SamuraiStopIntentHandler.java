package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.activities.StateManager;
import com.muffinsoft.alexa.sdk.handlers.StopIntentHandler;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.constants.GreetingsPhraseConstants;
import com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.GreetingsPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Intents;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseSettings;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;

import java.util.List;

import static com.muffinsoft.alexa.skills.samuraichef.components.VoiceTranslator.translate;

public class SamuraiStopIntentHandler extends StopIntentHandler {

    private final GreetingsPhraseManager greetingsPhraseManager;

    public SamuraiStopIntentHandler(SettingsDependencyContainer configurationContainer, PhraseDependencyContainer phraseDependencyContainer) {
        super();
        this.greetingsPhraseManager = phraseDependencyContainer.getGreetingsPhraseManager();
    }

    @Override
    public StateManager nextTurn(HandlerInput handlerInput) {
        return new BaseStateManager(getSlotsFromInput(handlerInput), handlerInput.getAttributesManager()) {

            @SuppressWarnings("Duplicates")
            private void buildExit(DialogItem.Builder builder) {

                List<PhraseSettings> dialog = greetingsPhraseManager.getValueByKey(GreetingsPhraseConstants.EXIT_PHRASE);

                int userReplyBreakpointPosition = 0;

                for (PhraseSettings phraseSettings : dialog) {

                    if (phraseSettings.isUserResponse()) {
                        this.getSessionAttributes().put(SessionConstants.USER_REPLY_BREAKPOINT, userReplyBreakpointPosition + 1);
                        this.getSessionAttributes().put(SessionConstants.INTENT, Intents.EXIT_CONFIRMATION);
                        break;
                    }
                    builder.addResponse(translate(phraseSettings));
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
