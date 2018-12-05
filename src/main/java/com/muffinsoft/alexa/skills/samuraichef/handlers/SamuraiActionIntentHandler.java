package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Slot;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muffinsoft.alexa.sdk.activities.StateManager;
import com.muffinsoft.alexa.sdk.handlers.GameIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.activities.CancelStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.ExitConfirmationStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.ExitStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.HelpStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.InitialGreetingStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.ResetConfirmationStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.ResetMissionSelectionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.ResetStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.SelectLevelStateManager;
import com.muffinsoft.alexa.skills.samuraichef.components.SessionStateFabric;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.Intents;
import com.muffinsoft.alexa.skills.samuraichef.enums.PowerUps;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.models.ActivityProgress;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.FINISHED_MISSIONS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STAR_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_HIGH_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_LOW_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_MID_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_PROGRESS;

public class SamuraiActionIntentHandler extends GameIntentHandler {

    private final SettingsDependencyContainer settingsDependencyContainer;
    private final PhraseDependencyContainer phraseDependencyContainer;
    private final SessionStateFabric stateManagerFabric;

    public SamuraiActionIntentHandler(SettingsDependencyContainer settingsDependencyContainer, PhraseDependencyContainer phraseDependencyContainer, SessionStateFabric stateManagerFabric) {
        this.settingsDependencyContainer = settingsDependencyContainer;
        this.phraseDependencyContainer = phraseDependencyContainer;
        this.stateManagerFabric = stateManagerFabric;
    }

    @Override
    public StateManager nextTurn(HandlerInput input) {

        AttributesManager attributesManager = input.getAttributesManager();

        Map<String, Slot> slots = getSlotsFromInput(input);

        Intents activeIntent = Intents.valueOf(String.valueOf(attributesManager.getSessionAttributes().getOrDefault(INTENT, Intents.GAME)));

        logger.debug("Handling ");

        switch (activeIntent) {
            case INITIAL_GREETING:
                return new InitialGreetingStateManager(slots, attributesManager, settingsDependencyContainer, phraseDependencyContainer);
            case GAME:
                return handleGameActivity(input, slots, attributesManager);
            case CANCEL:
                return new CancelStateManager(slots, attributesManager, settingsDependencyContainer, phraseDependencyContainer);
            case EXIT:
                return new ExitStateManager(slots, attributesManager, settingsDependencyContainer, phraseDependencyContainer);
            case EXIT_CONFIRMATION:
                return new ExitConfirmationStateManager(slots, attributesManager, settingsDependencyContainer, phraseDependencyContainer);
            case HELP:
                return new HelpStateManager(slots, attributesManager, settingsDependencyContainer, phraseDependencyContainer);
            case RESET:
                return new ResetStateManager(slots, attributesManager, settingsDependencyContainer, phraseDependencyContainer);
            case RESET_CONFIRMATION:
                return new ResetConfirmationStateManager(slots, attributesManager, settingsDependencyContainer, phraseDependencyContainer);
            case RESET_MISSION_SELECTION:
                return new ResetMissionSelectionStateManager(slots, attributesManager, settingsDependencyContainer, phraseDependencyContainer);
            default:
                throw new IllegalArgumentException("Unknown intent type " + activeIntent);
        }
    }

    private StateManager handleGameActivity(HandlerInput input, Map<String, Slot> slots, AttributesManager attributesManager) {

        boolean userSelectLevel = attributesManager.getSessionAttributes().containsKey(CURRENT_MISSION);

        handlePersistentAttributes(input);

        if (userSelectLevel) {

            UserProgress currentUserProgress = getCurrentUserProgress(input);

            Activities currentActivity;

            if (currentUserProgress.isJustCreated() || currentUserProgress.getCurrentActivity() == null) {
                currentActivity = getFirstActivityForMission(input);
            }
            else {
                currentActivity = getActivityFromUserProgress(currentUserProgress);
            }

            PowerUps currentEquipment = PowerUps.EMPTY_SLOT;

            ActivityProgress currentActivityProgress = getCurrentActivityProgress(input);

            if (currentActivityProgress.isPowerUpEquipped()) {
                currentEquipment = PowerUps.valueOf(currentActivityProgress.getActivePowerUp());
            }

            StateManager stateManager = stateManagerFabric.createFromRequest(currentActivity, currentEquipment, slots, attributesManager);

            logger.debug("Going to handle activity " + currentActivity + " with equipment " + currentEquipment);

            return stateManager;
        }
        else {
            logger.debug("Going to handle mission selection ");
            return new SelectLevelStateManager(slots, attributesManager, settingsDependencyContainer, phraseDependencyContainer);
        }
    }

    private Activities getActivityFromUserProgress(UserProgress userProgress) {
        String currentActivity = userProgress.getCurrentActivity();
        return Activities.valueOf(currentActivity);
    }

    private ActivityProgress getCurrentActivityProgress(HandlerInput input) {

        LinkedHashMap rawActivityProgress = (LinkedHashMap) input.getAttributesManager().getSessionAttributes().get(ACTIVITY_PROGRESS);

        return rawActivityProgress != null ? new ObjectMapper().convertValue(rawActivityProgress, ActivityProgress.class) : new ActivityProgress();
    }

    private UserProgress getCurrentUserProgress(HandlerInput input) {

        LinkedHashMap rawUserProgress = (LinkedHashMap) input.getAttributesManager().getSessionAttributes().get(USER_PROGRESS);

        return rawUserProgress != null ? new ObjectMapper().convertValue(rawUserProgress, UserProgress.class) : new UserProgress(true);
    }

    private Activities getFirstActivityForMission(HandlerInput input) {

        UserMission userMission = UserMission.valueOf(String.valueOf(input.getAttributesManager().getSessionAttributes().get(CURRENT_MISSION)));

        return settingsDependencyContainer.getMissionManager().getFirstActivityForMission(userMission);
    }

    private void handlePersistentAttributes(HandlerInput input) {

        AttributesManager attributesManager = input.getAttributesManager();

        if (!attributesManager.getSessionAttributes().containsKey(STAR_COUNT)) {
            int starCount = ((BigDecimal) attributesManager.getPersistentAttributes().getOrDefault(STAR_COUNT, BigDecimal.ZERO)).intValue();
            attributesManager.getSessionAttributes().put(STAR_COUNT, starCount);
        }

        if (!attributesManager.getSessionAttributes().containsKey(FINISHED_MISSIONS)) {
            LinkedHashSet finishedMission = (LinkedHashSet) attributesManager.getPersistentAttributes().getOrDefault(FINISHED_MISSIONS, new LinkedHashSet<>());
            List<String> result = new ArrayList<>();
            for (Object o : finishedMission) {
                result.add(String.valueOf(o));
            }
            attributesManager.getSessionAttributes().put(FINISHED_MISSIONS, result);
        }

        Object rawMission = attributesManager.getSessionAttributes().get(CURRENT_MISSION);

        if (rawMission == null) {
            return;
        }

        UserMission userMission = UserMission.valueOf(String.valueOf(rawMission));

        String dbSource;

        switch (userMission) {
            case LOW_MISSION:
                dbSource = USER_LOW_PROGRESS_DB;
                break;
            case MEDIUM_MISSION:
                dbSource = USER_MID_PROGRESS_DB;
                break;
            case HIGH_MISSION:
                dbSource = USER_HIGH_PROGRESS_DB;
                break;
            default:
                throw new IllegalStateException("Unknown user level: " + userMission.name());
        }

        Map<String, Object> sessionAttributes = attributesManager.getSessionAttributes();

        if (attributesManager.getPersistentAttributes().containsKey(dbSource)) {

            if (sessionAttributes == null) {
                sessionAttributes = new HashMap<>();
            }

            String jsonInString = String.valueOf(attributesManager.getPersistentAttributes().get(dbSource));

            ObjectMapper mapper = new ObjectMapper();

            try {
                LinkedHashMap rawUserProgress = mapper.readValue(jsonInString, LinkedHashMap.class);
                sessionAttributes.put(USER_PROGRESS, rawUserProgress);
            }
            catch (IOException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
        else {
            sessionAttributes.remove(USER_PROGRESS);
        }
    }
}
