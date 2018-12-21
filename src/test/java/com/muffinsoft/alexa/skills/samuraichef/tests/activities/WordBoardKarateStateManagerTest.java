package com.muffinsoft.alexa.skills.samuraichef.tests.activities;

import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.enums.StateType;
import com.muffinsoft.alexa.sdk.model.SlotName;
import com.muffinsoft.alexa.skills.samuraichef.IoC;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.WordBoardKarateCorrectAnswerStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.WordBoardKarateSecondChanceStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.WordBoardKarateStateManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.PowerUps;
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

        WordBoardKarateStateManager wordBoardKarateStateManager = new WordBoardKarateStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());

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

        WordBoardKarateStateManager wordBoardKarateStateManager = new WordBoardKarateCorrectAnswerStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());

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

        WordBoardKarateStateManager wordBoardKarateStateManager = new WordBoardKarateSecondChanceStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());

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
