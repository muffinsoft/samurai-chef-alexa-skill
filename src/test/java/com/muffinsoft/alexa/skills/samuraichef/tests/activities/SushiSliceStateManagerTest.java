package com.muffinsoft.alexa.skills.samuraichef.tests.activities;

import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.skills.samuraichef.IoC;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.SushiSliceCorrectAnswerStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.SushiSliceSecondChanceStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.SushiSliceStateManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.Intents;
import com.muffinsoft.alexa.skills.samuraichef.enums.PowerUps;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.models.ActivityProgress;
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.FINISHED_MISSIONS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.QUESTION_TIME;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STAR_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_REPLY_BREAKPOINT;

class SushiSliceStateManagerTest extends BaseStateManagerTest {

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
        Assertions.assertEquals(sessionAttributes.get(STATE_PHASE), StatePhase.MISSION_INTRO);
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

        Integer wonSuccessCount = IoC.provideConfigurationContainer().getActivityManager().getStripeForActivityAtMission(Activities.SUSHI_SLICE, 0, UserMission.LOW_MISSION).getWonSuccessCount();

        ActivityProgress activityProgress = new ActivityProgress();
        activityProgress.setCurrentIngredientReaction("test");
        activityProgress.setSuccessCount(wonSuccessCount - 1);

        UserProgress userProgress = new UserProgress(UserMission.LOW_MISSION, false);
        userProgress.setCurrentActivity(Activities.SUSHI_SLICE.name());
        userProgress.setFinishedActivities(new String[]{Activities.FOOD_TASTER.name(), Activities.JUICE_WARRIOR.name(), Activities.WORD_BOARD_KARATE.name()});

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(USER_PROGRESS, toMap(userProgress));
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

        Integer maxMistakeCount = IoC.provideConfigurationContainer().getActivityManager().getStripeForActivityAtMission(Activities.SUSHI_SLICE, 0, UserMission.LOW_MISSION).getMaxMistakeCount();

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

    //    @Test
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

    @Test
    void testActivePhaseSuccess() {

        Map<String, Slot> slots = createSlotsForValue("test");

        ActivityProgress activityProgress = new ActivityProgress();
        activityProgress.setCurrentIngredientReaction("test");
        activityProgress.setSuccessCount(2);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(STATE_PHASE, StatePhase.PHASE_1);
        attributes.put(QUESTION_TIME, System.currentTimeMillis());

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();
        ActivityProgress result = (ActivityProgress) sessionAttributes.get(ACTIVITY_PROGRESS);

        Assertions.assertEquals(result.getSuccessCount(), 3);
        Assertions.assertEquals(result.getSuccessInRow(), 1);
    }

    @Test
    void testActivePhaseLastSuccessAnswer() {

        Map<String, Slot> slots = createSlotsForValue("test");

        ActivityProgress activityProgress = new ActivityProgress();
        activityProgress.setCurrentIngredientReaction("test");
        activityProgress.setSuccessCount(6);

        UserProgress userProgress = new UserProgress(UserMission.LOW_MISSION);
        userProgress.setStripeCount(1);
        userProgress.setFinishedActivities(new String[]{Activities.FOOD_TASTER.name(), Activities.WORD_BOARD_KARATE.name(), Activities.JUICE_WARRIOR.name()});

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(USER_PROGRESS, toMap(userProgress));
        attributes.put(STATE_PHASE, StatePhase.PHASE_1);
        attributes.put(QUESTION_TIME, System.currentTimeMillis());

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();

        Assertions.assertEquals(sessionAttributes.get(STAR_COUNT), 2);
        Assertions.assertEquals(sessionAttributes.get(STATE_PHASE), StatePhase.WIN);
    }

    @Test
    void testActivePhaseLastSuccessAnswerStep5() {

        Map<String, Slot> slots = createSlotsForValue("test");

        ActivityProgress activityProgress = new ActivityProgress();
        activityProgress.setCurrentIngredientReaction("test");
        activityProgress.setSuccessCount(7);
        activityProgress.setStripeComplete(true);
        activityProgress.setMissionFinished(true);

        UserProgress userProgress = new UserProgress(UserMission.LOW_MISSION);
        userProgress.setStripeCount(2);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(USER_REPLY_BREAKPOINT, 5);
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(USER_PROGRESS, toMap(userProgress));
        attributes.put(STATE_PHASE, StatePhase.WIN);
        attributes.put(QUESTION_TIME, System.currentTimeMillis());

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();

        Assertions.assertEquals(sessionAttributes.get(STAR_COUNT), 2);
        Assertions.assertEquals(sessionAttributes.get(STATE_PHASE), StatePhase.WIN);
    }

    @Test
    void testActivePhaseSuccessEarnPowerUp() {

        Map<String, Slot> slots = createSlotsForValue("test");

        int successInRowForPowerUp = IoC.provideConfigurationContainer().getMissionManager().getSuccessInRowForPowerUp();

        ActivityProgress activityProgress = new ActivityProgress();
        activityProgress.setCurrentIngredientReaction("test");
        activityProgress.setSuccessInRow(successInRowForPowerUp - 1);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(STATE_PHASE, StatePhase.PHASE_1);
        attributes.put(QUESTION_TIME, System.currentTimeMillis());

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();
        ActivityProgress result = (ActivityProgress) sessionAttributes.get(ACTIVITY_PROGRESS);

        Assertions.assertEquals(result.getSuccessInRow(), successInRowForPowerUp);
        Assertions.assertFalse(result.getExistingPowerUps().isEmpty());
        Assertions.assertNotNull(result.getActivePowerUp());
    }

    @Test
    void testActivePhaseMistake() {

        Map<String, Slot> slots = createSlotsForValue("test");

        ActivityProgress activityProgress = new ActivityProgress();
        activityProgress.setCurrentIngredientReaction("no");
        activityProgress.setSuccessCount(2);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(STATE_PHASE, StatePhase.PHASE_1);
        attributes.put(QUESTION_TIME, System.currentTimeMillis());

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

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

        Map<String, Slot> slots = createSlotsForValue("test");

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
        attributes.put(STATE_PHASE, StatePhase.PHASE_1);
        attributes.put(QUESTION_TIME, System.currentTimeMillis());

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceCorrectAnswerStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

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

        Map<String, Slot> slots = createSlotsForValue("test");

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
        attributes.put(STATE_PHASE, StatePhase.PHASE_1);
        attributes.put(QUESTION_TIME, System.currentTimeMillis());

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceSecondChanceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

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

        Map<String, Slot> slots = createSlotsForValue("test");

        ActivityProgress activityProgress = new ActivityProgress();
        activityProgress.setCurrentIngredientReaction("test");
        activityProgress.setSuccessCount(2);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(STATE_PHASE, StatePhase.PHASE_1);
        attributes.put(QUESTION_TIME, System.currentTimeMillis() - 300000);

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

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

        Map<String, Slot> slots = createSlotsForValue("test");

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
        attributes.put(STATE_PHASE, StatePhase.PHASE_1);
        attributes.put(QUESTION_TIME, System.currentTimeMillis() - 300000);

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceCorrectAnswerStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

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

        Map<String, Slot> slots = createSlotsForValue("test");

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
        attributes.put(STATE_PHASE, StatePhase.PHASE_1);
        attributes.put(QUESTION_TIME, System.currentTimeMillis() - 300000);

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceSecondChanceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

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

        Map<String, Slot> slots = createSlotsForValue("test");

        ActivityProgress activityProgress = new ActivityProgress();
        activityProgress.setMissionFinished(true);
        UserProgress userProgress = new UserProgress(UserMission.LOW_MISSION, false);
        userProgress.setMissionFinished(true);


        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(USER_PROGRESS, toMap(userProgress));
        attributes.put(FINISHED_MISSIONS, Collections.singletonList(UserMission.LOW_MISSION.name()));

        SushiSliceStateManager sushiSliceStateManager = new SushiSliceSecondChanceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        sushiSliceStateManager.nextResponse();

        sushiSliceStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = sushiSliceStateManager.getSessionAttributes();

        Assertions.assertEquals(sessionAttributes.get(INTENT), Intents.RESET);
    }
}