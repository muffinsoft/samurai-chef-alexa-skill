package com.muffinsoft.alexa.skills.samuraichef.tests.activities;

import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.enums.IntentType;
import com.muffinsoft.alexa.sdk.enums.StateType;
import com.muffinsoft.alexa.sdk.model.SlotName;
import com.muffinsoft.alexa.skills.samuraichef.IoC;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.SushiSliceCorrectAnswerStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.SushiSliceSecondChanceStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.SushiSliceStateManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.PowerUps;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.models.ActivityProgress;
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.FINISHED_MISSIONS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.QUESTION_TIME;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_PROGRESS;

class SushiSliceStateManagerTest extends BaseStateManagerTest {

    @Test
    void testStripeIntro() {

        Map<String, Slot> slots = createSlotsForValue(SlotName.ACTION, "any");

        ActivityProgress activityProgress = new ActivityProgress();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(STATE_PHASE, StateType.SUBMISSION_OUTRO);

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();
        Assertions.assertEquals(sessionAttributes.get(STATE_PHASE), StateType.DEMO);
    }

    @Test
    void testActivityIntro() {

        Map<String, Slot> slots = createSlotsForValue(SlotName.ACTION, "any");

        ActivityProgress activityProgress = new ActivityProgress();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(STATE_PHASE, StateType.ACTIVITY_INTRO);

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();
        Assertions.assertEquals(sessionAttributes.get(STATE_PHASE), StateType.DEMO);
    }

    @Test
    void testDemo() {

        Map<String, Slot> slots = createSlotsForValue(SlotName.ACTION, "any");

        ActivityProgress activityProgress = new ActivityProgress();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(STATE_PHASE, StateType.DEMO);

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();
        Assertions.assertEquals(sessionAttributes.get(STATE_PHASE), StateType.READY);
    }

    @Test
    void testActivePhaseLose() {

        Map<String, Slot> slots = createSlotsForValue(SlotName.ACTION, "test");

        Integer maxMistakeCount = IoC.provideSettingsDependencies().getActivityManager().getStripeForActivityAtMission(Activities.SUSHI_SLICE, 0, UserMission.LOW_MISSION).getMaxMistakeCount();

        ActivityProgress activityProgress = new ActivityProgress();
        activityProgress.setCurrentIngredientReaction("any");
        activityProgress.setSuccessCount(maxMistakeCount - 1);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(STATE_PHASE, StateType.GAME_PHASE_1);

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();
        Assertions.assertEquals(sessionAttributes.get(STATE_PHASE), StateType.GAME_PHASE_1);
    }

    //    @Test
    void testRetry() {

        Map<String, Slot> slots = createSlotsForValue(SlotName.NAVIGATION, "again");

        ActivityProgress activityProgress = new ActivityProgress();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(STATE_PHASE, StateType.LOSE);

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();
        Assertions.assertEquals(sessionAttributes.get(STATE_PHASE), StateType.DEMO);
    }

    @Test
    void testExit() {

        Map<String, Slot> slots = createSlotsForValue(SlotName.CONFIRMATION, "no");

        ActivityProgress activityProgress = new ActivityProgress();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(STATE_PHASE, StateType.LOSE);

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();
        Assertions.assertNull(sessionAttributes.get(STATE_PHASE));
    }

    @Test
    void testActivePhaseMistake() {

        Map<String, Slot> slots = createSlotsForValue(SlotName.ACTION, "test");

        ActivityProgress activityProgress = new ActivityProgress();
        activityProgress.setCurrentIngredientReaction("no");
        activityProgress.setSuccessCount(2);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(STATE_PHASE, StateType.GAME_PHASE_1);
        attributes.put(QUESTION_TIME, System.currentTimeMillis());

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();
        ActivityProgress result = (ActivityProgress) sessionAttributes.get(ACTIVITY_PROGRESS);

        Assertions.assertEquals(result.getMistakesCount(), 1);
        Assertions.assertEquals(result.getSuccessCount(), 2);
        Assertions.assertEquals(result.getSuccessInRow(), 0);
    }

    @Test
    void testActivePhaseMistakeWithCorrectAnswer() {

        Map<String, Slot> slots = createSlotsForValue(SlotName.ACTION, "test");

        String powerUp = PowerUps.CORRECT_ANSWER_SLOT.name();

        ActivityProgress activityProgress = new ActivityProgress();
        activityProgress.setCurrentIngredientReaction("no");
        activityProgress.setSuccessCount(2);
        activityProgress.setSuccessInRow(2);
        activityProgress.setActivePowerUp(powerUp);
        activityProgress.setExistingPowerUps(new String[]{powerUp});

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(STATE_PHASE, StateType.GAME_PHASE_1);
        attributes.put(QUESTION_TIME, System.currentTimeMillis());

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceCorrectAnswerStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();
        ActivityProgress result = (ActivityProgress) sessionAttributes.get(ACTIVITY_PROGRESS);

        Assertions.assertEquals(result.getMistakesCount(), 0);
        Assertions.assertEquals(result.getSuccessCount(), 3);
        Assertions.assertEquals(result.getSuccessInRow(), 0);
        Assertions.assertTrue(result.getExistingPowerUps().isEmpty());
        Assertions.assertNull(result.getActivePowerUp());
    }

    @Test
    void testActivePhaseMistakeWithSecondChance() {

        Map<String, Slot> slots = createSlotsForValue(SlotName.ACTION, "test");

        String powerUp = PowerUps.SECOND_CHANCE_SLOT.name();

        ActivityProgress activityProgress = new ActivityProgress();
        activityProgress.setCurrentIngredientReaction("no");
        activityProgress.setSuccessCount(2);
        activityProgress.setSuccessInRow(2);
        activityProgress.setActivePowerUp(powerUp);
        activityProgress.setExistingPowerUps(new String[]{powerUp});

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(STATE_PHASE, StateType.GAME_PHASE_1);
        attributes.put(QUESTION_TIME, System.currentTimeMillis());

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceSecondChanceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();
        ActivityProgress result = (ActivityProgress) sessionAttributes.get(ACTIVITY_PROGRESS);

        Assertions.assertEquals(result.getMistakesCount(), 0);
        Assertions.assertEquals(result.getSuccessCount(), 2);
        Assertions.assertEquals(result.getSuccessInRow(), 0);
        Assertions.assertTrue(result.getExistingPowerUps().isEmpty());
        Assertions.assertNull(result.getActivePowerUp());
    }

    @Test
    void testActivePhaseTooLongMistake() {

        Map<String, Slot> slots = createSlotsForValue(SlotName.ACTION, "test");

        ActivityProgress activityProgress = new ActivityProgress();
        activityProgress.setCurrentIngredientReaction("test");
        activityProgress.setSuccessCount(2);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(STATE_PHASE, StateType.GAME_PHASE_1);
        attributes.put(QUESTION_TIME, System.currentTimeMillis() - 300000);

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();
        ActivityProgress result = (ActivityProgress) sessionAttributes.get(ACTIVITY_PROGRESS);

        Assertions.assertEquals(result.getMistakesCount(), 1);
        Assertions.assertEquals(result.getSuccessCount(), 2);
        Assertions.assertEquals(result.getSuccessInRow(), 0);
    }

    @Test
    void testActivePhaseTooLongMistakeWithCorrectAnswer() {

        Map<String, Slot> slots = createSlotsForValue(SlotName.ACTION, "test");

        String powerUp = PowerUps.CORRECT_ANSWER_SLOT.name();

        ActivityProgress activityProgress = new ActivityProgress();
        activityProgress.setCurrentIngredientReaction("test");
        activityProgress.setSuccessCount(1);
        activityProgress.setSuccessInRow(1);
        activityProgress.setActivePowerUp(powerUp);
        activityProgress.setExistingPowerUps(new String[]{powerUp});

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(STATE_PHASE, StateType.GAME_PHASE_1);
        attributes.put(QUESTION_TIME, System.currentTimeMillis() - 300000);

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceCorrectAnswerStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();
        ActivityProgress result = (ActivityProgress) sessionAttributes.get(ACTIVITY_PROGRESS);

        Assertions.assertEquals(result.getMistakesCount(), 0);
        Assertions.assertEquals(result.getSuccessCount(), 2);
        Assertions.assertEquals(result.getSuccessInRow(), 0);
        Assertions.assertTrue(result.getExistingPowerUps().isEmpty());
        Assertions.assertNull(result.getActivePowerUp());
    }

    @Test
    void testActivePhaseTooLongMistakeWithSecondChance() {

        Map<String, Slot> slots = createSlotsForValue(SlotName.ACTION, "test");

        String powerUp = PowerUps.SECOND_CHANCE_SLOT.name();

        ActivityProgress activityProgress = new ActivityProgress();
        activityProgress.setCurrentIngredientReaction("test");
        activityProgress.setSuccessCount(1);
        activityProgress.setSuccessInRow(1);
        activityProgress.setActivePowerUp(powerUp);
        activityProgress.setExistingPowerUps(new String[]{powerUp});

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(STATE_PHASE, StateType.GAME_PHASE_1);
        attributes.put(QUESTION_TIME, System.currentTimeMillis() - 300000);

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceSecondChanceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();
        ActivityProgress result = (ActivityProgress) sessionAttributes.get(ACTIVITY_PROGRESS);

        Assertions.assertEquals(result.getMistakesCount(), 0);
        Assertions.assertEquals(result.getSuccessCount(), 1);
        Assertions.assertEquals(result.getSuccessInRow(), 0);
        Assertions.assertTrue(result.getExistingPowerUps().isEmpty());
        Assertions.assertNull(result.getActivePowerUp());
    }

    @Test
    void testMissionComplete() {

        Map<String, Slot> slots = createSlotsForValue(SlotName.ACTION, "test");

        ActivityProgress activityProgress = new ActivityProgress();
        activityProgress.setMissionFinished(true);
        UserProgress userProgress = new UserProgress(UserMission.LOW_MISSION, false);
        userProgress.setMissionFinished(true);


        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(USER_PROGRESS, toMap(userProgress));
        attributes.put(FINISHED_MISSIONS, Collections.singletonList(UserMission.LOW_MISSION.name()));

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceSecondChanceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();

        Assertions.assertEquals(sessionAttributes.get(INTENT), IntentType.RESET);
    }
}