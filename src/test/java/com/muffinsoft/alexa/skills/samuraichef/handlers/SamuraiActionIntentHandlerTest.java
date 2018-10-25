package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Session;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.skills.samuraichef.IoC;
import com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
        return new SamuraiActionIntentHandler(IoC.providePhraseManager(), IoC.provideActivitiesManager(), IoC.provideCardManager(), IoC.provideIngredientsManager(), IoC.providePowerUpsManager(), IoC.provideRewardManager());
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
    void moveBetweenActivitiesAfterWin() {

        SamuraiActionIntentHandler handler = createActionIntentHandlerInstance();

        Map<String, Slot> slots = new HashMap<>();
        slots.put("action", Slot.builder().withValue("yes").build());

        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(SessionConstants.ACTIVITY, Activities.SUSHI_SLICE);
        sessionAttributes.put(SessionConstants.STATE_PHASE, StatePhase.WIN);

        HandlerInput input = createInputWithSlotsAndSessionAttributes(slots, sessionAttributes);

        Optional<Response> response = handler.handle(input);

        response.isPresent();
    }

    @Test
    void moveBetweenActivitiesAfterLoseWithFilledFinishedActivities() {

        SamuraiActionIntentHandler handler = createActionIntentHandlerInstance();

        Map<String, Slot> slots = new HashMap<>();
        slots.put("action", Slot.builder().withValue("mission").build());

        List<String> finishedActivities = Collections.singletonList(Activities.JUICE_WARRIOR.name());

        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(SessionConstants.ACTIVITY, Activities.SUSHI_SLICE);
        sessionAttributes.put(SessionConstants.STATE_PHASE, StatePhase.LOSE);
        sessionAttributes.put(SessionConstants.FINISHED_ROUNDS, finishedActivities);

        HandlerInput input = createInputWithSlotsAndSessionAttributes(slots, sessionAttributes);

        Optional<Response> response = handler.handle(input);

        response.isPresent();
    }

    @Test
    void moveFromLastToFirstActivityAfterWin() {

        SamuraiActionIntentHandler handler = createActionIntentHandlerInstance();

        Map<String, Slot> slots = new HashMap<>();
        slots.put("action", Slot.builder().withValue("yes").build());

        List<String> finishedActivities = Arrays.asList(Activities.SUSHI_SLICE.name(), Activities.JUICE_WARRIOR.name(), Activities.WORD_BOARD_KARATE.name());

        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(SessionConstants.ACTIVITY, Activities.FOOD_TASTER);
        sessionAttributes.put(SessionConstants.STATE_PHASE, StatePhase.WIN);
        sessionAttributes.put(SessionConstants.WIN_IN_A_ROW_COUNT, 2);
        sessionAttributes.put(SessionConstants.STRIPE_COUNT, 5);
        sessionAttributes.put(SessionConstants.FINISHED_ROUNDS, finishedActivities);

        HandlerInput input = createInputWithSlotsAndSessionAttributes(slots, sessionAttributes);

        Optional<Response> response = handler.handle(input);

        response.isPresent();
    }

    @Test
    void moveFromLastToFirstActivityAfterLose() {

        SamuraiActionIntentHandler handler = createActionIntentHandlerInstance();

        Map<String, Slot> slots = new HashMap<>();
        slots.put("action", Slot.builder().withValue("mission").build());

        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(SessionConstants.ACTIVITY, Activities.FOOD_TASTER);
        sessionAttributes.put(SessionConstants.STATE_PHASE, StatePhase.LOSE);

        HandlerInput input = createInputWithSlotsAndSessionAttributes(slots, sessionAttributes);

        Optional<Response> response = handler.handle(input);

        response.isPresent();
    }

    @Test
    void handleIngredientAtPhase0() {

        SamuraiActionIntentHandler handler = createActionIntentHandlerInstance();

        Map<String, Slot> slots = new HashMap<>();
        slots.put("action", Slot.builder().withValue("yes").build());
        slots.put("name", Slot.builder().withValue("Alex").build());

        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(SessionConstants.ACTIVITY, Activities.SUSHI_SLICE);
        sessionAttributes.put(SessionConstants.STATE_PHASE, StatePhase.READY_PHASE);

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
        sessionAttributes.put(SessionConstants.ACTIVITY, Activities.SUSHI_SLICE);
        sessionAttributes.put(SessionConstants.MISTAKES_COUNT, 0);
        sessionAttributes.put(SessionConstants.SUCCESS_COUNT, 0);
        sessionAttributes.put(SessionConstants.FIRST_TIME_ASKING, false);
//        sessionAttributes.put(SessionConstants.USERNAME, "Alex");
        sessionAttributes.put(SessionConstants.PREVIOUS_INGREDIENT, "shoe");
        sessionAttributes.put(SessionConstants.STATE_PHASE, StatePhase.PHASE_1);
        sessionAttributes.put(SessionConstants.INGREDIENT_REACTION, "no");

        HandlerInput input = createInputWithSlotsAndSessionAttributes(slots, sessionAttributes);

        Optional<Response> response = handler.handle(input);

        response.isPresent();
    }
}
