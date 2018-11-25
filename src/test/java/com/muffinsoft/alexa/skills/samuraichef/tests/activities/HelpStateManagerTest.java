package com.muffinsoft.alexa.skills.samuraichef.tests.activities;

import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.skills.samuraichef.IoC;
import com.muffinsoft.alexa.skills.samuraichef.activities.HelpStateManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.HelpStates;
import com.muffinsoft.alexa.skills.samuraichef.enums.Intents;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.HELP_STATE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.FOOD_TASTER;
import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.SUSHI_SLICE;

public class HelpStateManagerTest extends BaseStateManagerTest {

    @Test
    void testStartGeneralHelp() {

        Map<String, Slot> slots = createSlotsForValue("any");

        Map<String, Object> attributes = new HashMap<>();

        HelpStateManager helpStateManager = new HelpStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        helpStateManager.nextResponse();

        helpStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = helpStateManager.getSessionAttributes();
        Assertions.assertEquals(sessionAttributes.get(INTENT), Intents.HELP);
        Assertions.assertEquals(sessionAttributes.get(HELP_STATE), HelpStates.GENERAL_HELP);
    }

    @Test
    void testStartActivityCompetitionHelp() {

        Map<String, Slot> slots = createSlotsForValue("any");

        Map<String, Object> attributes = new HashMap<>();

        Map<String, Object> userProgress = new LinkedHashMap<>();
        userProgress.put("currentActivity", SUSHI_SLICE.name());

        attributes.put(USER_PROGRESS, userProgress);

        HelpStateManager helpStateManager = new HelpStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        helpStateManager.nextResponse();

        helpStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = helpStateManager.getSessionAttributes();
        Assertions.assertEquals(sessionAttributes.get(INTENT), Intents.HELP);
        Assertions.assertEquals(sessionAttributes.get(HELP_STATE), HelpStates.COMPETITION_REMINDER_HELP);
    }

    @Test
    void testStartActivityRegularFirstTimeHelp() {

        Map<String, Slot> slots = createSlotsForValue("any");

        Map<String, Object> attributes = new HashMap<>();

        Map<String, Object> userProgress = new LinkedHashMap<>();
        userProgress.put("currentActivity", FOOD_TASTER.name());

        attributes.put(USER_PROGRESS, userProgress);

        HelpStateManager helpStateManager = new HelpStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        helpStateManager.nextResponse();

        helpStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = helpStateManager.getSessionAttributes();
        Assertions.assertEquals(sessionAttributes.get(INTENT), Intents.HELP);
        Assertions.assertEquals(sessionAttributes.get(HELP_STATE), HelpStates.LEARN_MORE_HELP);
    }

    @Test
    void testStartActivityRegularHelp() {

        Map<String, Slot> slots = createSlotsForValue("any");

        Map<String, Object> userProgress = new LinkedHashMap<>();
        userProgress.put("currentActivity", FOOD_TASTER.name());
        userProgress.put("stripeCount", 0);
        userProgress.put("finishedActivities", new String[]{SUSHI_SLICE.name()});

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(USER_PROGRESS, userProgress);

        HelpStateManager helpStateManager = new HelpStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        helpStateManager.nextResponse();

        helpStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = helpStateManager.getSessionAttributes();
        Assertions.assertEquals(sessionAttributes.get(INTENT), Intents.HELP);
        Assertions.assertEquals(sessionAttributes.get(HELP_STATE), HelpStates.MORE_DETAILS_HELP);
    }

    @Test
    void testStartMissionHelp() {

        Map<String, Slot> slots = createSlotsForValue("any");

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION.name());

        HelpStateManager helpStateManager = new HelpStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        helpStateManager.nextResponse();

        helpStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = helpStateManager.getSessionAttributes();
        Assertions.assertEquals(sessionAttributes.get(INTENT), Intents.HELP);
        Assertions.assertEquals(sessionAttributes.get(HELP_STATE), HelpStates.CONTINUE_PLAYING_HELP);
    }
}
