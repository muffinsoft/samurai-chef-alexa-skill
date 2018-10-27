package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.attributes.persistence.PersistenceAdapter;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.exception.PersistenceException;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.JUICE_WARRIOR;

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
                .withPersistenceAdapter(new MockPersistenceAdapter())
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
    void moveBetweenActivitiesAfterWin() {

        SamuraiActionIntentHandler handler = createActionIntentHandlerInstance();

        Map<String, Slot> slots = new HashMap<>();
        slots.put("action", Slot.builder().withValue("yes").build());

        Map<String, Object> userProgress = new LinkedHashMap<>();
        userProgress.put("lastActivity", JUICE_WARRIOR.name());
        userProgress.put("stripeCount", 0);
        userProgress.put("starCount", 0);
        userProgress.put("winInARowCount", 1);
        userProgress.put("currentLevel", 0);
        userProgress.put("finishedRounds", new String[]{Activities.SUSHI_SLICE.name()});

        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(SessionConstants.ACTIVITY, JUICE_WARRIOR);
        sessionAttributes.put(SessionConstants.STATE_PHASE, StatePhase.WIN);
        sessionAttributes.put(SessionConstants.USER_PROGRESS, userProgress);

        HandlerInput input = createInputWithSlotsAndSessionAttributes(slots, sessionAttributes);

        Optional<Response> response = handler.handle(input);

        response.isPresent();
    }

    @Test
    void moveBetweenActivitiesAfterWinWithoutDemo() {

        SamuraiActionIntentHandler handler = createActionIntentHandlerInstance();

        Map<String, Slot> slots = new HashMap<>();
        slots.put("action", Slot.builder().withValue("yes").build());

        Map<String, Object> userProgress = new LinkedHashMap<>();
        userProgress.put("lastActivity", JUICE_WARRIOR.name());
        userProgress.put("stripeCount", 0);
        userProgress.put("starCount", 0);
        userProgress.put("winInARowCount", 1);
        userProgress.put("currentLevel", 2);
        userProgress.put("finishedRounds", new String[]{Activities.SUSHI_SLICE.name()});

        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(SessionConstants.ACTIVITY, JUICE_WARRIOR);
        sessionAttributes.put(SessionConstants.STATE_PHASE, StatePhase.WIN);
        sessionAttributes.put(SessionConstants.USER_PROGRESS, userProgress);

        HandlerInput input = createInputWithSlotsAndSessionAttributes(slots, sessionAttributes);

        Optional<Response> response = handler.handle(input);

        response.isPresent();
    }

    @Test
    void moveBetweenActivitiesAfterLoseWithFilledFinishedActivities() {

        SamuraiActionIntentHandler handler = createActionIntentHandlerInstance();

        Map<String, Slot> slots = new HashMap<>();
        slots.put("action", Slot.builder().withValue("mission").build());

        List<String> finishedActivities = Collections.singletonList(JUICE_WARRIOR.name());

        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(SessionConstants.ACTIVITY, Activities.SUSHI_SLICE);
        sessionAttributes.put(SessionConstants.STATE_PHASE, StatePhase.LOSE);

        HandlerInput input = createInputWithSlotsAndSessionAttributes(slots, sessionAttributes);

        Optional<Response> response = handler.handle(input);

        response.isPresent();
    }

    @Test
    void moveFromLastToFirstActivityAfterWin() {

        SamuraiActionIntentHandler handler = createActionIntentHandlerInstance();

        Map<String, Slot> slots = new HashMap<>();
        slots.put("action", Slot.builder().withValue("yes").build());

        List<String> finishedActivities = Arrays.asList(Activities.SUSHI_SLICE.name(), JUICE_WARRIOR.name(), Activities.WORD_BOARD_KARATE.name());

        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(SessionConstants.ACTIVITY, Activities.FOOD_TASTER);
        sessionAttributes.put(SessionConstants.STATE_PHASE, StatePhase.WIN);

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
        sessionAttributes.put(SessionConstants.STATE_PHASE, StatePhase.PHASE_1);

        HandlerInput input = createInputWithSlotsAndSessionAttributes(slots, sessionAttributes);

        Optional<Response> response = handler.handle(input);

        response.isPresent();
    }

    private class MockPersistenceAdapter implements PersistenceAdapter {

        @Override
        public Optional<Map<String, Object>> getAttributes(RequestEnvelope envelope) throws PersistenceException {
            return Optional.empty();
        }

        @Override
        public void saveAttributes(RequestEnvelope envelope, Map<String, Object> attributes) throws PersistenceException {

        }
    }
}
