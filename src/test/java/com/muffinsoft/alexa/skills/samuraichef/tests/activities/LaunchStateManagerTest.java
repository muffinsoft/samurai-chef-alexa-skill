package com.muffinsoft.alexa.skills.samuraichef.tests.activities;

import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.skills.samuraichef.IoC;
import com.muffinsoft.alexa.skills.samuraichef.activities.LaunchStateManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Intents;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;

public class LaunchStateManagerTest extends BaseStateManagerTest {


    @Test
    void test() {
        Map<String, Slot> slots = createSlotsForValue("any");

        Map<String, Object> attributes = new HashMap<>();

        LaunchStateManager launchStateManager = new LaunchStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());

        launchStateManager.nextResponse();

        Map<String, Object> sessionAttributes = launchStateManager.getSessionAttributes();
        Assertions.assertEquals(sessionAttributes.get(INTENT), Intents.INITIAL_GREETING);
    }
}
