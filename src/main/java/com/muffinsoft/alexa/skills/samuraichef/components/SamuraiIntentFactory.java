package com.muffinsoft.alexa.skills.samuraichef.components;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.activities.StateManager;
import com.muffinsoft.alexa.sdk.components.IntentFactory;
import com.muffinsoft.alexa.sdk.constants.PaywallConstants;
import com.muffinsoft.alexa.sdk.enums.IntentType;
import com.muffinsoft.alexa.sdk.enums.PurchaseState;
import com.muffinsoft.alexa.sdk.enums.StateType;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.SlotName;
import com.muffinsoft.alexa.skills.samuraichef.activities.*;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.AplManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.CardManager;
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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static com.muffinsoft.alexa.sdk.constants.SessionConstants.INTENT;
import static com.muffinsoft.alexa.sdk.constants.SessionConstants.STATE_TYPE;
import static com.muffinsoft.alexa.sdk.enums.IntentType.GAME;
import static com.muffinsoft.alexa.sdk.enums.StateType.RETURN_TO_GAME;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.SELECT_MISSION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.*;

public class SamuraiIntentFactory implements IntentFactory {

    private static final Logger logger = LogManager.getLogger(SamuraiIntentFactory.class);

    private final SettingsDependencyContainer settingsDependencyContainer;
    private final PhraseDependencyContainer phraseDependencyContainer;
    private final SessionStateFabric sessionStateFabric;
    private final CardManager cardManager;
    private final AplManager aplManager;

    public SamuraiIntentFactory(SettingsDependencyContainer settingsDependencyContainer, PhraseDependencyContainer phraseDependencyContainer, SessionStateFabric sessionStateFabric) {
        this.settingsDependencyContainer = settingsDependencyContainer;
        this.phraseDependencyContainer = phraseDependencyContainer;
        this.sessionStateFabric = sessionStateFabric;
        this.cardManager = settingsDependencyContainer.getCardManager();
        this.aplManager = settingsDependencyContainer.getAplManager();
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
                return selectMission(slots, attributesManager);
            case MENU_OR_CONTINUE:
                return continueOrMenu(slots, attributesManager, !UserReplyComparator.isYes(slots));
            case CONTINUE_OR_MENU:
                return continueOrMenu(slots, attributesManager, UserReplyComparator.isYes(slots));
            case BUY_INTENT:
                return buy(slots, attributesManager);
            default:
                throw new IllegalArgumentException("Unknown intent type " + intent);
        }
    }

    private StateManager continueOrMenu(Map<String, Slot> slots, AttributesManager attributesManager, boolean game) {
        if (game) {
            return handleGameActivity(slots, attributesManager);
        } else {
            return selectMission(slots, attributesManager);
        }
    }

    private StateManager selectMission(Map<String, Slot> slots, AttributesManager attributesManager) {
        return new BaseStateManager(slots, attributesManager, settingsDependencyContainer.getDialogTranslator()) {
            @Override
            public DialogItem nextResponse() {
                attributesManager.getSessionAttributes().put(INTENT, IntentType.GAME);
                return DialogItem.builder()
                        .addResponse(getDialogTranslator().translate(phraseDependencyContainer.getRegularPhraseManager().getValueByKey(SELECT_MISSION_PHRASE)))
                        .withAplDocument(aplManager.getContainer())
                        .addBackgroundImageUrl(cardManager.getValueByKey("mission-selection"))
                        .build();
            }
        };
    }

    private StateManager upsell(Map<String, Slot> slots, AttributesManager attributesManager) {
        return new BaseStateManager(slots, attributesManager, settingsDependencyContainer.getDialogTranslator()) {
            @Override
            public DialogItem nextResponse() {
                logger.debug("Generating upsell");
                getPersistentAttributes().put(PaywallConstants.UPSELL, ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT));
                return BuyManager.getBuyResponse(attributesManager, phraseDependencyContainer, getDialogTranslator(), PaywallConstants.UPSELL);
            }
        };
    }

    private StateManager buy(Map<String, Slot> slots, AttributesManager attributesManager) {
        if (UserReplyComparator.isYes(slots)) {
            return new BaseStateManager(slots, attributesManager, null) {
                @Override
                public DialogItem nextResponse() {
                    getPersistentAttributes().put(PaywallConstants.UPSELL, ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT));
                    savePersistentAttributes();
                    return DialogItem.builder().withDirective(PaywallConstants.BUY).build();
                }
            };
        }
        else {
            attributesManager.getSessionAttributes().put(INTENT, GAME);
            attributesManager.getSessionAttributes().put(STATE_TYPE, RETURN_TO_GAME);
            return handleGameActivity(slots, attributesManager);
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
                            return getPreviousOrDefaultIntentType(attributesManager);
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
                            return getPreviousOrDefaultIntentType(attributesManager);
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

    private IntentType getPreviousOrDefaultIntentType(AttributesManager attributesManager) {

        Map<String, Object> sessionAttributes = attributesManager.getSessionAttributes();

        IntentType intentType = IntentType.GAME;

        if (sessionAttributes.containsKey(PREVIOUS_INTENT)) {
            intentType = IntentType.valueOf(String.valueOf(sessionAttributes.get(PREVIOUS_INTENT)));
        }

        if (intentType == IntentType.GAME) {
            if (sessionAttributes.containsKey(STATE_PHASE)) {
                StateType stateType = StateType.valueOf(String.valueOf(sessionAttributes.get(STATE_PHASE)));
                if (stateType == StateType.GAME_PHASE_1 || stateType == StateType.GAME_PHASE_2 || stateType == StateType.READY) {
                    sessionAttributes.put(STATE_PHASE, StateType.RETURN_TO_GAME);
                }
            }
        }

        sessionAttributes.put(INTENT, intentType);
        sessionAttributes.remove(HELP_STATE);
        sessionAttributes.remove(PREVIOUS_INTENT);

        return intentType;
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

            if (getPurchaseState(attributesManager) != PurchaseState.ENTITLED && currentUserProgress.getStripeCount() > 2) {
                boolean isPurchasable = (boolean) attributesManager.getSessionAttributes().getOrDefault("isPurchasable", false);
                if (isPurchasable) {
                    return upsell(slots, attributesManager);
                } else {
                    return selectMission(slots, attributesManager);
                }
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

    private PurchaseState getPurchaseState(AttributesManager attributesManager) {
        String state = String.valueOf(attributesManager.getPersistentAttributes().getOrDefault(PURCHASE_STATE, PurchaseState.NOT_ENTITLED));
        return PurchaseState.valueOf(state);
    }
}
