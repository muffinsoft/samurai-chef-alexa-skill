package com.muffinsoft.alexa.skills.samuraichef.tests.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Session;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.User;
import com.amazon.ask.model.slu.entityresolution.Resolution;
import com.amazon.ask.model.slu.entityresolution.Resolutions;
import com.amazon.ask.model.slu.entityresolution.Status;
import com.amazon.ask.model.slu.entityresolution.StatusCode;
import com.muffinsoft.alexa.skills.samuraichef.IoC;
import com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiActionIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.tests.MockPersistenceAdapter;
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
                                .withSessionId("test.session")
                                .withUser(User.builder().withUserId("user.id").build())
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

    Slot createSlotForValue(String value) {
        return Slot.builder()
                .withValue(value)
                .withResolutions(Resolutions.builder()
                        .withResolutionsPerAuthority(Collections.singletonList(Resolution.builder()
                                .withStatus(Status.builder()
                                        .withCode(StatusCode.ER_SUCCESS_MATCH)
                                        .build())
                                .withAuthority("testAuthority")
                                .build()))
                        .build())
                .build();
    }

    private SamuraiActionIntentHandler createActionIntentHandlerInstance() {
        return new SamuraiActionIntentHandler(IoC.provideConfigurationContainer(), IoC.provideSessionStateFabric());
    }

    @Test
    void moveToLevelSelecting() {

        SamuraiActionIntentHandler handler = createActionIntentHandlerInstance();

        Map<String, Slot> slots = new HashMap<>();
        slots.put("action", createSlotForValue(UserMission.LOW_MISSION.name()));

        Map<String, Object> sessionAttributes = new HashMap<>();

        HandlerInput input = createInputWithSlotsAndSessionAttributes(slots, sessionAttributes);

        Optional<Response> response = handler.handle(input);

        response.isPresent();
    }

    @Test
    void moveBetweenActivitiesAfterWin() {

        SamuraiActionIntentHandler handler = createActionIntentHandlerInstance();

        Map<String, Slot> slots = new HashMap<>();
        slots.put("action", createSlotForValue("yes"));

        Map<String, Object> userProgress = new LinkedHashMap<>();
        userProgress.put("lastActivity", JUICE_WARRIOR.name());
        userProgress.put("stripeCount", 0);
        userProgress.put("starCount", 0);
        userProgress.put("finishedActivities", new String[]{Activities.SUSHI_SLICE.name()});

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
        slots.put("action", createSlotForValue("mission"));

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
        slots.put("action", createSlotForValue("yes"));

        List<String> finishedActivities = Arrays.asList(Activities.SUSHI_SLICE.name(), JUICE_WARRIOR.name(), Activities.WORD_BOARD_KARATE.name());

        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(SessionConstants.ACTIVITY, Activities.FOOD_TASTER);
        sessionAttributes.put(SessionConstants.STATE_PHASE, StatePhase.WIN);

        HandlerInput input = createInputWithSlotsAndSessionAttributes(slots, sessionAttributes);

        Optional<Response> response = handler.handle(input);

        response.isPresent();
    }

    @Test
    void restartAfterLose() {

        SamuraiActionIntentHandler handler = createActionIntentHandlerInstance();

        Map<String, Slot> slots = new HashMap<>();
        slots.put("action", createSlotForValue("mission"));

        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(SessionConstants.ACTIVITY, Activities.FOOD_TASTER);
        sessionAttributes.put(SessionConstants.CURRENT_MISSION, UserMission.LOW_MISSION);
        sessionAttributes.put(SessionConstants.STATE_PHASE, StatePhase.LOSE);

        HandlerInput input = createInputWithSlotsAndSessionAttributes(slots, sessionAttributes);

        Optional<Response> response = handler.handle(input);

        response.isPresent();
    }

    @Test
    void handleIngredientAtPhase0() {

        SamuraiActionIntentHandler handler = createActionIntentHandlerInstance();

        Map<String, Slot> slots = new HashMap<>();
        slots.put("action", createSlotForValue("yes"));

        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(SessionConstants.ACTIVITY, Activities.SUSHI_SLICE);
        sessionAttributes.put(SessionConstants.CURRENT_MISSION, UserMission.LOW_MISSION);
        sessionAttributes.put(SessionConstants.STATE_PHASE, StatePhase.READY_PHASE);

        HandlerInput input = createInputWithSlotsAndSessionAttributes(slots, sessionAttributes);

        Optional<Response> response = handler.handle(input);

        response.isPresent();
    }

    @Test
    void handleIngredientAtPhase1() {

        SamuraiActionIntentHandler handler = createActionIntentHandlerInstance();

        Map<String, Slot> slots = new HashMap<>();
        slots.put("action", createSlotForValue("no"));

        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put(SessionConstants.ACTIVITY, Activities.SUSHI_SLICE);
        sessionAttributes.put(SessionConstants.STATE_PHASE, StatePhase.PHASE_1);

        HandlerInput input = createInputWithSlotsAndSessionAttributes(slots, sessionAttributes);

        Optional<Response> response = handler.handle(input);

        response.isPresent();
    }

}
