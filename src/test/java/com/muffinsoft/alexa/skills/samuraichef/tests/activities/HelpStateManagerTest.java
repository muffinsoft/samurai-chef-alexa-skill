package com.muffinsoft.alexa.skills.samuraichef.tests.activities;

public class HelpStateManagerTest extends BaseStateManagerTest {

//    @Test
//    void testStartGeneralHelp() {
//
//        Map<String, Slot> slots = createSlotsForValue(SlotName.ACTION, "any");
//
//        Map<String, Object> attributes = new HashMap<>();
//
//        HelpStateManager helpStateManager = new HelpStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());
//
//        helpStateManager.nextResponse();
//
//        helpStateManager.updateAttributesManager();
//
//        Map<String, Object> sessionAttributes = helpStateManager.getSessionAttributes();
//        Assertions.assertEquals(sessionAttributes.get(INTENT), Intents.HELP);
//        Assertions.assertEquals(sessionAttributes.get(HELP_STATE), HelpStates.GENERAL_HELP);
//    }

//    @Test
//    void testStartActivityCompetitionHelp() {
//
//        Map<String, Slot> slots = createSlotsForValue(SlotName.ACTION, "any");
//
//        Map<String, Object> attributes = new HashMap<>();
//
//        Map<String, Object> userProgress = new LinkedHashMap<>();
//        userProgress.put("currentActivity", SUSHI_SLICE.name());
//
//        attributes.put(USER_PROGRESS, userProgress);
//
//        HelpStateManager helpStateManager = new HelpStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());
//
//        helpStateManager.nextResponse();
//
//        helpStateManager.updateAttributesManager();
//
//        Map<String, Object> sessionAttributes = helpStateManager.getSessionAttributes();
//        Assertions.assertEquals(sessionAttributes.get(INTENT), Intents.HELP);
//        Assertions.assertEquals(sessionAttributes.get(HELP_STATE), HelpStates.COMPETITION_REMINDER_HELP);
//    }

//    @Test
//    void testStartActivityRegularFirstTimeHelp() {
//
//        Map<String, Slot> slots = createSlotsForValue(SlotName.ACTION, "any");
//
//        Map<String, Object> attributes = new HashMap<>();
//
//        Map<String, Object> userProgress = new LinkedHashMap<>();
//        userProgress.put("currentActivity", FOOD_TASTER.name());
//
//        attributes.put(USER_PROGRESS, userProgress);
//
//        HelpStateManager helpStateManager = new HelpStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());
//
//        helpStateManager.nextResponse();
//
//        helpStateManager.updateAttributesManager();
//
//        Map<String, Object> sessionAttributes = helpStateManager.getSessionAttributes();
//        Assertions.assertEquals(sessionAttributes.get(INTENT), Intents.HELP);
//        Assertions.assertEquals(sessionAttributes.get(HELP_STATE), HelpStates.LEARN_MORE_HELP);
//    }

//    @Test
//    void testStartActivityRegularHelp() {
//
//        Map<String, Slot> slots = createSlotsForValue(SlotName.ACTION, "any");
//
//        Map<String, Object> userProgress = new LinkedHashMap<>();
//        userProgress.put("currentActivity", FOOD_TASTER.name());
//        userProgress.put("stripeCount", 0);
//        userProgress.put("finishedActivities", new String[]{SUSHI_SLICE.name()});
//
//        Map<String, Object> attributes = new HashMap<>();
//        attributes.put(USER_PROGRESS, userProgress);
//
//        HelpStateManager helpStateManager = new HelpStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());
//
//        helpStateManager.nextResponse();
//
//        helpStateManager.updateAttributesManager();
//
//        Map<String, Object> sessionAttributes = helpStateManager.getSessionAttributes();
//        Assertions.assertEquals(sessionAttributes.get(INTENT), Intents.HELP);
//        Assertions.assertEquals(sessionAttributes.get(HELP_STATE), HelpStates.MORE_DETAILS_HELP);
//    }

//    @Test
//    void testStartMissionHelp() {
//
//        Map<String, Slot> slots = createSlotsForValue(SlotName.ACTION, "any");
//
//        Map<String, Object> attributes = new HashMap<>();
//        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION.name());
//
//        HelpStateManager helpStateManager = new HelpStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());
//
//        helpStateManager.nextResponse();
//
//        helpStateManager.updateAttributesManager();
//
//        Map<String, Object> sessionAttributes = helpStateManager.getSessionAttributes();
//        Assertions.assertEquals(sessionAttributes.get(INTENT), Intents.HELP);
//        Assertions.assertEquals(sessionAttributes.get(HELP_STATE), HelpStates.CONTINUE_PLAYING_HELP);
//    }
}
