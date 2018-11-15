package com.muffinsoft.alexa.skills.samuraichef.tests.activities;

import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.skills.samuraichef.IoC;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.WordBoardKarateCorrectAnswerStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.WordBoardKarateSecondChanceStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.WordBoardKarateStateManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.PowerUps;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.models.ActivityProgress;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.QUESTION_TIME;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STATE_PHASE;

class WordBoardKarateStateManagerTest extends BaseStateManagerTest {

    @Test
    void testStartMission() {

        Map<String, Slot> slots = createSlotsForValue("any");

        ActivityProgress activityProgress = new ActivityProgress();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));

        WordBoardKarateStateManager wordBoardKarateStateManager = new WordBoardKarateStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        wordBoardKarateStateManager.nextResponse();

        wordBoardKarateStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = wordBoardKarateStateManager.getSessionAttributes();
        Assertions.assertEquals(sessionAttributes.get(STATE_PHASE), StatePhase.STRIPE_INTRO);
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

        WordBoardKarateStateManager wordBoardKarateStateManager = new WordBoardKarateStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        wordBoardKarateStateManager.nextResponse();

        wordBoardKarateStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = wordBoardKarateStateManager.getSessionAttributes();
        ActivityProgress result = (ActivityProgress) sessionAttributes.get(ACTIVITY_PROGRESS);

        Assertions.assertEquals(result.getSuccessCount(), 3);
        Assertions.assertEquals(result.getSuccessInRow(), 1);
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

        WordBoardKarateStateManager wordBoardKarateStateManager = new WordBoardKarateStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        wordBoardKarateStateManager.nextResponse();

        wordBoardKarateStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = wordBoardKarateStateManager.getSessionAttributes();
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

        WordBoardKarateStateManager wordBoardKarateStateManager = new WordBoardKarateStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        wordBoardKarateStateManager.nextResponse();

        wordBoardKarateStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = wordBoardKarateStateManager.getSessionAttributes();
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

        WordBoardKarateStateManager wordBoardKarateStateManager = new WordBoardKarateCorrectAnswerStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        wordBoardKarateStateManager.nextResponse();

        wordBoardKarateStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = wordBoardKarateStateManager.getSessionAttributes();
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

        WordBoardKarateStateManager wordBoardKarateStateManager = new WordBoardKarateSecondChanceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideConfigurationContainer());

        wordBoardKarateStateManager.nextResponse();

        wordBoardKarateStateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = wordBoardKarateStateManager.getSessionAttributes();
        ActivityProgress result = (ActivityProgress) sessionAttributes.get(ACTIVITY_PROGRESS);

        Assertions.assertEquals(result.getMistakesCount(), 0);
        Assertions.assertEquals(result.getSuccessCount(), 2);
        Assertions.assertEquals(result.getSuccessInRow(), 0);
        Assertions.assertTrue(result.getExistingPowerUps().isEmpty());
        Assertions.assertNull(result.getActivePowerUp());
    }
}