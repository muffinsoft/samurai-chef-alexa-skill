package com.muffinsoft.alexa.skills.samuraichef.tests.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.Session;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.User;
import com.amazon.ask.model.slu.entityresolution.Resolution;
import com.amazon.ask.model.slu.entityresolution.Resolutions;
import com.amazon.ask.model.slu.entityresolution.Status;
import com.amazon.ask.model.slu.entityresolution.StatusCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muffinsoft.alexa.skills.samuraichef.IoC;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.SushiSliceStateManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.models.ActivityProgress;
import com.muffinsoft.alexa.skills.samuraichef.tests.MockPersistenceAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STATE_PHASE;

class BaseStateManagerTest {

    AttributesManager createAttributesManager(Map<String, Slot> slots, Map<String, Object> attributes) {

        return AttributesManager.builder()
                .withRequestEnvelope(RequestEnvelope.builder()
                        .withSession(Session.builder()
                                .withSessionId("test.session")
                                .withUser(User.builder().withUserId("user.id").build())
                                .withAttributes(attributes)
                                .withNew(false)
                                .build()
                        )
                        .withRequest(IntentRequest.builder()
                                .withIntent(
                                        Intent.builder()
                                                .withSlots(slots)
                                                .build()
                                ).build())
                        .build())
                .withPersistenceAdapter(new MockPersistenceAdapter())
                .build();
    }

    LinkedHashMap toMap(ActivityProgress activityProgress) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(activityProgress, LinkedHashMap.class);
    }

    Map<String, Slot> createSlotsForValue(String value) {
        Map<String, Slot> slots = new HashMap<>();
        slots.put("action", createSlotForValue(value));
        return slots;
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

    @Test
    void testMissionIntro() {

        Map<String, Slot> slots = createSlotsForValue("any");

        ActivityProgress activityProgress = new ActivityProgress();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();
        Assertions.assertEquals(sessionAttributes.get(STATE_PHASE), StatePhase.STRIPE_INTRO);
    }

    @Test
    void testStripeIntro() {

        Map<String, Slot> slots = createSlotsForValue("any");

        ActivityProgress activityProgress = new ActivityProgress();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(STATE_PHASE, StatePhase.STRIPE_INTRO);

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();
        Assertions.assertEquals(sessionAttributes.get(STATE_PHASE), StatePhase.ACTIVITY_INTRO);
    }

    @Test
    void testActivityIntro() {

        Map<String, Slot> slots = createSlotsForValue("any");

        ActivityProgress activityProgress = new ActivityProgress();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(STATE_PHASE, StatePhase.ACTIVITY_INTRO);

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();
        Assertions.assertEquals(sessionAttributes.get(STATE_PHASE), StatePhase.DEMO);
    }

    @Test
    void testDemo() {

        Map<String, Slot> slots = createSlotsForValue("any");

        ActivityProgress activityProgress = new ActivityProgress();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(STATE_PHASE, StatePhase.DEMO);

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();
        Assertions.assertEquals(sessionAttributes.get(STATE_PHASE), StatePhase.READY_PHASE);
    }

    @Test
    void testReadyPhase() {

        Map<String, Slot> slots = createSlotsForValue("any");

        ActivityProgress activityProgress = new ActivityProgress();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(STATE_PHASE, StatePhase.READY_PHASE);

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();
        Assertions.assertEquals(sessionAttributes.get(STATE_PHASE), StatePhase.PHASE_1);
    }

    @Test
    void testActivePhaseWin() {

        Map<String, Slot> slots = createSlotsForValue("test");

        Integer wonSuccessCount = IoC.provideConfigurationContainer().getActivityManager().getLevelForActivity(Activities.SUSHI_SLICE, 0).getWonSuccessCount();

        ActivityProgress activityProgress = new ActivityProgress();
        activityProgress.setCurrentIngredientReaction("test");
        activityProgress.setSuccessCount(wonSuccessCount - 1);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(STATE_PHASE, StatePhase.PHASE_1);

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();
        Assertions.assertEquals(sessionAttributes.get(STATE_PHASE), StatePhase.WIN);
    }

    @Test
    void testActivePhaseLose() {

        Map<String, Slot> slots = createSlotsForValue("test");

        Integer maxMistakeCount = IoC.provideConfigurationContainer().getActivityManager().getLevelForActivity(Activities.SUSHI_SLICE, 0).getMaxMistakeCount();

        ActivityProgress activityProgress = new ActivityProgress();
        activityProgress.setCurrentIngredientReaction("any");
        activityProgress.setSuccessCount(maxMistakeCount - 1);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(STATE_PHASE, StatePhase.PHASE_1);

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();
        Assertions.assertEquals(sessionAttributes.get(STATE_PHASE), StatePhase.LOSE);
    }

    @Test
    void testRetry() {

        Map<String, Slot> slots = createSlotsForValue("again");

        ActivityProgress activityProgress = new ActivityProgress();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(STATE_PHASE, StatePhase.LOSE);

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();
        Assertions.assertEquals(sessionAttributes.get(STATE_PHASE), StatePhase.DEMO);
    }

    @Test
    void testExit() {

        Map<String, Slot> slots = createSlotsForValue("no");

        ActivityProgress activityProgress = new ActivityProgress();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(STATE_PHASE, StatePhase.LOSE);

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();
        Assertions.assertNull(sessionAttributes.get(STATE_PHASE));
    }
}
