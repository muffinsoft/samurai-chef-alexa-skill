package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Session;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.skills.samuraichef.DependenciesContainer;
import com.muffinsoft.alexa.skills.samuraichef.content.SamuraiChefConstants;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class SamuraiActionIntentHandlerTest {

    private HandlerInput createInputWithSlotsAndSessionAttributes(Map<String, Slot> slots, Map<String, Object> sessionAttributes) {

        if (slots == null) {
            slots = Collections.emptyMap();
        }

        HandlerInput handlerInput = HandlerInput.builder()
                .withRequestEnvelope(RequestEnvelope.builder()
                        .withSession(Session.builder()
                                .withSessionId("test session")
                                .withNew(false)
                                .build()
                        )
                        .withRequest(IntentRequest.builder().withIntent(
                                Intent.builder()
                                        .withSlots(slots)
                                        .build()
                        ).build())
                        .build())
                .build();

        if (sessionAttributes != null && !sessionAttributes.isEmpty()) {
            handlerInput.getAttributesManager().setSessionAttributes(sessionAttributes);
        }

        return handlerInput;
    }

    private SamuraiActionIntentHandler createActionIntentHandlerInstance() {
        return new SamuraiActionIntentHandler(DependenciesContainer.providePhraseManager(), DependenciesContainer.provideIngredientsManager(), DependenciesContainer.provideActivitiesManager());
    }

    @Test
    void emptyRequest() {
        SamuraiActionIntentHandler handler = createActionIntentHandlerInstance();

        Map<String, Slot> slots = new HashMap<>();

        slots.put("action", Slot.builder().withValue("test").build());

        HandlerInput input = createInputWithSlotsAndSessionAttributes(slots, null);

        Optional<Response> handle = handler.handle(input);

        handle.isPresent();
    }

    @Test
    void nameHandlingRequest() {

        SamuraiActionIntentHandler handler = createActionIntentHandlerInstance();

        HandlerInput input = createInputWithSlotsAndSessionAttributes(null, null);

        Optional<Response> intro0response = handler.handle(input);

        Map<String, Slot> slots = Collections.singletonMap("action", Slot.builder().withValue("username").build());

        input = createInputWithSlotsAndSessionAttributes(slots, null);

        Optional<Response> intro1response = handler.handle(input);

        intro1response.isPresent();
    }

    @Test
    void moveFromNameHandlingToFirstActivity() {

        SamuraiActionIntentHandler handler = createActionIntentHandlerInstance();

        Map<String, Slot> slots = new HashMap<>();
        slots.put("action", Slot.builder().withValue("yes").build());
        slots.put("name", Slot.builder().withValue("Alex").build());

        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(SamuraiChefConstants.USERNAME, "test");
        sessionAttributes.put(SamuraiChefConstants.ACTIVITY, Activities.SUSHI_SLICE);

        HandlerInput input = createInputWithSlotsAndSessionAttributes(slots, sessionAttributes);

        Optional<Response> intro1response = handler.handle(input);

        intro1response.isPresent();
    }

    @Test
    void handleIngredientAtPhase0() {

        SamuraiActionIntentHandler handler = createActionIntentHandlerInstance();

        Map<String, Slot> slots = new HashMap<>();
        slots.put("action", Slot.builder().withValue("yes").build());
        slots.put("name", Slot.builder().withValue("Alex").build());

        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(SamuraiChefConstants.USERNAME, "test");
        sessionAttributes.put(SamuraiChefConstants.ACTIVITY, Activities.SUSHI_SLICE);
        sessionAttributes.put(SamuraiChefConstants.STATE_PHASE, StatePhase.PHASE_0);

        HandlerInput input = createInputWithSlotsAndSessionAttributes(slots, sessionAttributes);

        Optional<Response> response = handler.handle(input);

        response.isPresent();
    }

    @Test
    void handleIngredientAtPhase1() {

        SamuraiActionIntentHandler handler = createActionIntentHandlerInstance();

        Map<String, Slot> slots = new HashMap<>();
        slots.put("action", Slot.builder().withValue("no").build());
        slots.put("name", Slot.builder().withValue("Alex").build());

        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(SamuraiChefConstants.ACTIVITY, Activities.SUSHI_SLICE);
        sessionAttributes.put(SamuraiChefConstants.MISTAKES_COUNT, 0);
        sessionAttributes.put(SamuraiChefConstants.SUCCESS_COUNT, 0);
        sessionAttributes.put(SamuraiChefConstants.FIRST_TIME_ASKING, false);
        sessionAttributes.put(SamuraiChefConstants.USERNAME, "Alex");
        sessionAttributes.put(SamuraiChefConstants.PREVIOUS_INGREDIENT, "shoe");
        sessionAttributes.put(SamuraiChefConstants.STATE_PHASE, StatePhase.PHASE_1);
        sessionAttributes.put(SamuraiChefConstants.INGREDIENT_REACTION, "no");

        HandlerInput input = createInputWithSlotsAndSessionAttributes(slots, sessionAttributes);

        Optional<Response> response = handler.handle(input);

        response.isPresent();
    }
}
