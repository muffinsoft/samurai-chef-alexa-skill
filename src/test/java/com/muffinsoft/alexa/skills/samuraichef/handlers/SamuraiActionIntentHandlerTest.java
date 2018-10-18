package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Session;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.skills.samuraichef.DependenciesContainer;
import com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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

        Map<String, Slot> slots = Collections.singletonMap("action", Slot.builder().withValue("yes").build());
        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(SushiSliceConstants.USERNAME, "test");
        sessionAttributes.put(SushiSliceConstants.ACTIVITY, Activities.SUSHI_SLICE);

        HandlerInput input = createInputWithSlotsAndSessionAttributes(slots, sessionAttributes);

        Optional<Response> intro1response = handler.handle(input);

        intro1response.isPresent();
    }

    @Test
    void handleIngredientAtPhase0() {

        SamuraiActionIntentHandler handler = createActionIntentHandlerInstance();

        Map<String, Slot> slots = Collections.singletonMap("action", Slot.builder().withValue("yes").build());

        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(SushiSliceConstants.USERNAME, "test");
        sessionAttributes.put(SushiSliceConstants.ACTIVITY, Activities.SUSHI_SLICE);
        sessionAttributes.put(SushiSliceConstants.STATE_PHASE, StatePhase.PHASE_0);

        HandlerInput input = createInputWithSlotsAndSessionAttributes(slots, sessionAttributes);

        Optional<Response> response = handler.handle(input);

        response.isPresent();
    }

    @Test
    void handleIngredientAtPhase1() {

        SamuraiActionIntentHandler handler = createActionIntentHandlerInstance();

        Map<String, Slot> slots = Collections.singletonMap("action", Slot.builder().withValue("boil").build());

        List<String> ingredients = new LinkedList<>();
        ingredients.add("rice");
        ingredients.add("rice");
        ingredients.add("shoe");

        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(SushiSliceConstants.USERNAME, "test");
        sessionAttributes.put(SushiSliceConstants.ACTIVITY, Activities.SUSHI_SLICE);
        sessionAttributes.put(SushiSliceConstants.INGREDIENT_REACTION, "boil");
        sessionAttributes.put(SushiSliceConstants.PREVIOUS_INGREDIENTS, ingredients);
        sessionAttributes.put(SushiSliceConstants.MISTAKES_COUNT, 0);
        sessionAttributes.put(SushiSliceConstants.SUCCESS_COUNT, 0);
        sessionAttributes.put(SushiSliceConstants.FIRST_TIME_ASKING, false);
        sessionAttributes.put(SushiSliceConstants.STATE_PHASE, StatePhase.PHASE_1);

        HandlerInput input = createInputWithSlotsAndSessionAttributes(slots, sessionAttributes);

        Optional<Response> response = handler.handle(input);

        response.isPresent();
    }
}
