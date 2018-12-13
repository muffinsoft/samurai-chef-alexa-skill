package com.muffinsoft.alexa.skills.samuraichef.tests.activities;

import com.amazon.ask.model.Slot;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muffinsoft.alexa.sdk.enums.StateType;
import com.muffinsoft.alexa.skills.samuraichef.IoC;
import com.muffinsoft.alexa.skills.samuraichef.activities.CancelStateManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.models.ActivityProgress;
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_LOW_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_PROGRESS;

class CancelStateManagerTest extends BaseStateManagerTest {


    @Test
    void testCancelYes() throws IOException {

        Map<String, Slot> slots = createSlotsForValue("yes");

        ActivityProgress activityProgress = new ActivityProgress();

        UserProgress userProgress = new UserProgress();
        userProgress.addFinishedActivities(Activities.SUSHI_SLICE.name());
        userProgress.setPreviousActivity(Activities.SUSHI_SLICE.name());
        userProgress.setCurrentActivity(Activities.SUSHI_SLICE.name());
        userProgress.setMission(UserMission.LOW_MISSION.name());

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(CURRENT_MISSION, UserMission.LOW_MISSION);
        attributes.put(STATE_PHASE, StateType.WIN);
        attributes.put(ACTIVITY_PROGRESS, toMap(activityProgress));
        attributes.put(USER_PROGRESS, toMap(userProgress));

        CancelStateManager stateManager = new CancelStateManager(slots, createAttributesManager(slots, attributes), IoC.provideSettingsDependencies(), IoC.providePhraseDependencies());

        stateManager.nextResponse();

        stateManager.updateAttributesManager();

        Map<String, Object> persistentAttributes = stateManager.getPersistentAttributes();
        String stringifyResult = String.valueOf(persistentAttributes.get(USER_LOW_PROGRESS_DB));
        UserProgress result = new ObjectMapper().readValue(stringifyResult, UserProgress.class);

        Assertions.assertNotEquals(result.getCurrentActivity(), Activities.SUSHI_SLICE.name());
    }
}
