package com.muffinsoft.alexa.skills.samuraichef.tests.activities;

import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.skills.samuraichef.IoC;
import com.muffinsoft.alexa.skills.samuraichef.activities.InitialGreetingStateManager;
import com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants;
import com.muffinsoft.alexa.skills.samuraichef.enums.Intents;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;

class InitialGreetingStateManagerTest extends BaseStateManagerTest {

    @Test
    void firstBreakpoint() {
        Map<String, Slot> slots = createSlotsForValue("any");

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(SessionConstants.USER_REPLY_BREAKPOINT, 2);

        InitialGreetingStateManager initialGreetingStateManager = new InitialGreetingStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        initialGreetingStateManager.nextResponse();

        Map<String, Object> sessionAttributes = initialGreetingStateManager.getSessionAttributes();
        Assertions.assertEquals(sessionAttributes.get(INTENT), Intents.INITIAL_GREETING);
    }

    @Test
    void secondBreakpoint() {
        Map<String, Slot> slots = createSlotsForValue("any");

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(SessionConstants.USER_REPLY_BREAKPOINT, 9);

        InitialGreetingStateManager initialGreetingStateManager = new InitialGreetingStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        initialGreetingStateManager.nextResponse();

        Map<String, Object> sessionAttributes = initialGreetingStateManager.getSessionAttributes();
        Assertions.assertEquals(sessionAttributes.get(INTENT), Intents.GAME);
    }
}
