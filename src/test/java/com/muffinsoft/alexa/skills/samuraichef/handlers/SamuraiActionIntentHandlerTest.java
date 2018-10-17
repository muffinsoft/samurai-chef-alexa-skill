package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Session;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.skills.samuraichef.DependenciesContainer;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class SamuraiActionIntentHandlerTest {

    private HandlerInput createInputWithSlots(Map<String, Slot> slots) {
        return HandlerInput.builder()
                .withRequestEnvelope(RequestEnvelope.builder()
                        .withSession(Session.builder()
                                .withSessionId("test session")
                                .build()
                        )
                        .withRequest(IntentRequest.builder().withIntent(
                                Intent.builder()
                                        .withSlots(slots)
                                        .build()
                        ).build())
                        .build())
                .build();
    }

    @Test
    void handlerRunning() {
        SamuraiActionIntentHandler handler = new SamuraiActionIntentHandler(DependenciesContainer.providePhraseManager(), DependenciesContainer.provideIngredientsManager(), DependenciesContainer.provideActivitiesManager());

        Map<String, Slot> slots = new HashMap<>();

        slots.put("action", Slot.builder().withValue("test").build());

        HandlerInput input = createInputWithSlots(slots);

        Optional<Response> handle = handler.handle(input);

        handle.isPresent();
    }
}
