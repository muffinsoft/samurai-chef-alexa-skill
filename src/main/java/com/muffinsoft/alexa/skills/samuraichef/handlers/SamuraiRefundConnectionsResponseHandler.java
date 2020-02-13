package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.connections.ConnectionsResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.muffinsoft.alexa.skills.samuraichef.IoC;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiConnectionsResponseHandler.getSessionAttributes;

public class SamuraiRefundConnectionsResponseHandler implements com.amazon.ask.dispatcher.request.handler.impl.ConnectionsResponseHandler {

    @Override
    public boolean canHandle(HandlerInput input, ConnectionsResponse connectionsResponse) {
        String name = input.getRequestEnvelopeJson().get("request").get("name").asText();
        return name.equalsIgnoreCase("Cancel");
    }

    @Override
    public Optional<Response> handle(HandlerInput input, ConnectionsResponse connectionsResponse) {
        JsonNode token = input.getRequestEnvelopeJson().get("request").get("token");
        AttributesManager attributesManager = input.getAttributesManager();
        Map<String, Object> sessionAttributes = token != null ? getSessionAttributes(token) :
                verifyMap(attributesManager.getSessionAttributes());
        attributesManager.setSessionAttributes(sessionAttributes);
        return new SamuraiActionIntentHandler(IoC.provideIntentFactory()).handle(input);
    }

    static Map<String, Object> verifyMap(Map<String, Object> map) {
        if (map != null) {
            return map;
        }
        return new HashMap<>();
    }
}
