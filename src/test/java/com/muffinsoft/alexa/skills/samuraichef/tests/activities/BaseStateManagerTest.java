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
import com.muffinsoft.alexa.skills.samuraichef.tests.MockPersistenceAdapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

    LinkedHashMap toMap(Object progress) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(progress, LinkedHashMap.class);
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
