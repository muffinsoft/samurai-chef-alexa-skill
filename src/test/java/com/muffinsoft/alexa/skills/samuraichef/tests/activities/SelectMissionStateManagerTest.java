package com.muffinsoft.alexa.skills.samuraichef.tests.activities;

import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.skills.samuraichef.IoC;
import com.muffinsoft.alexa.skills.samuraichef.activities.SelectLevelStateManager;
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
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.FINISHED_MISSIONS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_PROGRESS;

public class SelectMissionStateManagerTest extends BaseStateManagerTest {

    @Test
    void testBlockingFinishedMission() {

        Map<String, Slot> slots = createSlotsForValue("chef");

        ActivityProgress activityProgress = new ActivityProgress();

        UserProgress userProgress = new UserProgress();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(FINISHED_MISSIONS, Arrays.asList(UserMission.LOW_MISSION.name()));
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(USER_PROGRESS, toMap(userProgress));

        SelectLevelStateManager stateManager = new SelectLevelStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());

        stateManager.nextResponse();

        stateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = stateManager.getSessionAttributes();

        Assertions.assertNotEquals(sessionAttributes.get(USER_PROGRESS), null);
    }

    //    @Test
    void testAllowingNonFinishedMission() {

        Map<String, Slot> slots = createSlotsForValue("chef");

        ActivityProgress activityProgress = new ActivityProgress();

        UserProgress userProgress = new UserProgress();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(FINISHED_MISSIONS, Collections.singletonList(UserMission.MEDIUM_MISSION.name()));
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(USER_PROGRESS, toMap(userProgress));

        SelectLevelStateManager stateManager = new SelectLevelStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());

        stateManager.nextResponse();

        stateManager.updateAttributesManager();

        Map<String, Object> sessionAttributes = stateManager.getSessionAttributes();

        Assertions.assertNull(sessionAttributes.get(USER_PROGRESS));
    }
}
