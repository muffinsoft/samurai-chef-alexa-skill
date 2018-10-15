package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Request;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.game.SessionStateManager;
import com.muffinsoft.alexa.sdk.handlers.ActionIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.game.SushiSliceSessionStateManager;

import java.util.Map;

import static com.amazon.ask.request.Predicates.intentName;

public class SushiSliceIntentHandler extends ActionIntentHandler {

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("SushiSliceIntent"));
    }

    @Override
    public SessionStateManager nextTurn(HandlerInput input) {

        Request request = input.getRequestEnvelope().getRequest();
        IntentRequest intentRequest = (IntentRequest) request;

        Map<String, Slot> slots = intentRequest.getIntent().getSlots();

        return new SushiSliceSessionStateManager(slots, input.getAttributesManager());
    }

    @Override
    public String getPhrase() {
        // not used in current version
        return null;
    }

    @Override
    public String getSimpleCard() {
        return PhraseManager.getPhrase("welcomeCard");
    }
}
