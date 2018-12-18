package com.muffinsoft.alexa.skills.samuraichef.components;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.activities.StateManager;
import com.muffinsoft.alexa.sdk.components.IntentFactory;
import com.muffinsoft.alexa.sdk.enums.IntentType;
import com.muffinsoft.alexa.sdk.enums.StateType;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.SlotName;
import com.muffinsoft.alexa.skills.samuraichef.activities.CancelStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.ExitConfirmationStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.ExitStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.HelpStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.InitialGreetingStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.ResetConfirmationStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.ResetMissionSelectionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.ResetStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.SelectLevelStateManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.HelpStates;
import com.muffinsoft.alexa.skills.samuraichef.enums.PowerUps;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.models.ActivityProgress;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.SELECT_MISSION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.HELP_STATE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_PROGRESS;

public class SamuraiIntentFactory implements IntentFactory {

    protected static final Logger logger = LogManager.getLogger(SamuraiIntentFactory.class);

    private final SettingsDependencyContainer settingsDependencyContainer;
    private final PhraseDependencyContainer phraseDependencyContainer;
    private final SessionStateFabric sessionStateFabric;

    public SamuraiIntentFactory(SettingsDependencyContainer settingsDependencyContainer, PhraseDependencyContainer phraseDependencyContainer, SessionStateFabric sessionStateFabric) {
        this.settingsDependencyContainer = settingsDependencyContainer;
        this.phraseDependencyContainer = phraseDependencyContainer;
        this.sessionStateFabric = sessionStateFabric;
    }

    @Override
    public StateManager getNextState(IntentType intent, Map<String, Slot> slots, AttributesManager attributesManager) {

        logger.info("Selecting State manager for " + intent);

        IntentType interceptedIntent = interceptIntent(intent, slots, attributesManager);

        switch (interceptedIntent) {
            case INITIAL_GREETING:
                return new InitialGreetingStateManager(slots, attributesManager, settingsDependencyContainer, phraseDependencyContainer);
            case GAME:
                return handleGameActivity(slots, attributesManager);
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
            case SELECT_MISSION:
                return new BaseStateManager(slots, attributesManager, settingsDependencyContainer.getDialogTranslator()) {
                    @Override
                    public DialogItem nextResponse() {
                        attributesManager.getSessionAttributes().put(INTENT, IntentType.GAME);
                        return DialogItem.builder()
                                .addResponse(getDialogTranslator().translate(phraseDependencyContainer.getRegularPhraseManager().getValueByKey(SELECT_MISSION_PHRASE)))
                                .build();
                    }
                };
            default:
                throw new IllegalArgumentException("Unknown intent type " + intent);
        }
    }

    private IntentType interceptIntent(IntentType intent, Map<String, Slot> slots, AttributesManager attributesManager) {
        if (intent == IntentType.HELP) {
            if (attributesManager.getSessionAttributes().containsKey(HELP_STATE)) {
                HelpStates helpState = HelpStates.valueOf(String.valueOf(attributesManager.getSessionAttributes().get(HELP_STATE)));
                if (helpState == HelpStates.PROCEED_GAME) {
                    if (slots.containsKey(SlotName.CONFIRMATION.text)) {
                        Slot slot = slots.get(SlotName.CONFIRMATION.text);
                        if (Objects.equals(slot.getValue(), "yes")) {
                            return getGameIntentType(attributesManager);
                        }
                        if (Objects.equals(slot.getValue(), "no")) {
                            return getSelectMissionIntentType(attributesManager);
                        }
                    }
                }
                else if (helpState == HelpStates.LEARN_MORE_HELP) {
                    if (slots.containsKey(SlotName.CONFIRMATION.text)) {
                        Slot slot = slots.get(SlotName.CONFIRMATION.text);
                        if (Objects.equals(slot.getValue(), "no")) {
                            return getGameIntentType(attributesManager);
                        }
                    }
                }
            }
        }
        return intent;
    }

    private IntentType getSelectMissionIntentType(AttributesManager attributesManager) {
        attributesManager.getSessionAttributes().clear();
        attributesManager.getSessionAttributes().put(INTENT, IntentType.SELECT_MISSION);
        return IntentType.SELECT_MISSION;
    }

    private IntentType getGameIntentType(AttributesManager attributesManager) {
        attributesManager.getSessionAttributes().put(INTENT, IntentType.GAME);
        attributesManager.getSessionAttributes().remove(HELP_STATE);
        if (attributesManager.getSessionAttributes().containsKey(STATE_PHASE)) {
            StateType stateType = StateType.valueOf(String.valueOf(attributesManager.getSessionAttributes().get(STATE_PHASE)));
            if (stateType == StateType.GAME_PHASE_1 || stateType == StateType.GAME_PHASE_2 || stateType == StateType.READY) {
                attributesManager.getSessionAttributes().put(STATE_PHASE, StateType.RETURN_TO_GAME);
            }
        }
        return IntentType.GAME;
    }

    private StateManager handleGameActivity(Map<String, Slot> slots, AttributesManager attributesManager) {

        boolean userSelectLevel = attributesManager.getSessionAttributes().containsKey(CURRENT_MISSION);

        if (userSelectLevel) {

            UserProgress currentUserProgress = getCurrentUserProgress(attributesManager);

            Activities currentActivity;

            if (currentUserProgress.isJustCreated() || currentUserProgress.getCurrentActivity() == null) {
                currentActivity = getFirstActivityForMission(attributesManager);
            }
            else {
                currentActivity = getActivityFromUserProgress(currentUserProgress);
            }

            PowerUps currentEquipment = PowerUps.EMPTY_SLOT;

            ActivityProgress currentActivityProgress = getCurrentActivityProgress(attributesManager);

            if (currentActivityProgress.isPowerUpEquipped()) {
                currentEquipment = PowerUps.valueOf(currentActivityProgress.getActivePowerUp());
            }

            StateManager activityStateManager = sessionStateFabric.createFromRequest(currentActivity, currentEquipment, slots, attributesManager);
            logger.info("Redirection to " + activityStateManager.getClass().getCanonicalName());
            return activityStateManager;
        }
        else {
            logger.info("Redirection to " + SelectLevelStateManager.class.getCanonicalName());
            return new SelectLevelStateManager(slots, attributesManager, settingsDependencyContainer, phraseDependencyContainer);
        }
    }

    private Activities getActivityFromUserProgress(UserProgress userProgress) {
        String currentActivity = userProgress.getCurrentActivity();
        return Activities.valueOf(currentActivity);
    }

    private ActivityProgress getCurrentActivityProgress(AttributesManager attributesManager) {

        LinkedHashMap rawActivityProgress = (LinkedHashMap) attributesManager.getSessionAttributes().get(ACTIVITY_PROGRESS);

        return rawActivityProgress != null ? new ObjectMapper().convertValue(rawActivityProgress, ActivityProgress.class) : new ActivityProgress();
    }

    private UserProgress getCurrentUserProgress(AttributesManager attributesManager) {

        LinkedHashMap rawUserProgress = (LinkedHashMap) attributesManager.getSessionAttributes().get(USER_PROGRESS);

        return rawUserProgress != null ? new ObjectMapper().convertValue(rawUserProgress, UserProgress.class) : new UserProgress(true);
    }

    private Activities getFirstActivityForMission(AttributesManager attributesManager) {

        UserMission userMission = UserMission.valueOf(String.valueOf(attributesManager.getSessionAttributes().get(CURRENT_MISSION)));

        return settingsDependencyContainer.getMissionManager().getFirstActivityForMission(userMission);
    }
}
