package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.muffinsoft.alexa.sdk.activities.StateManager;
import com.muffinsoft.alexa.sdk.handlers.SessionEndedRequestHandler;

public class SamuraiSessionEndedRequestHandler extends SessionEndedRequestHandler {

    @Override
    public StateManager nextTurn(HandlerInput handlerInput) {
        return null;
    }
}
