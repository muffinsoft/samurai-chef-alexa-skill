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
}
