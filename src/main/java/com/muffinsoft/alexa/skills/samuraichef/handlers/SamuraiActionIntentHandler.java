package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Request;
import com.amazon.ask.model.Slot;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muffinsoft.alexa.sdk.activities.SessionStateManager;
import com.muffinsoft.alexa.sdk.handlers.GameActionIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.activities.SelectLevelStateManager;
import com.muffinsoft.alexa.skills.samuraichef.components.SessionStateFabric;
import com.muffinsoft.alexa.skills.samuraichef.content.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.Equipments;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserLevel;
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.amazon.ask.request.Predicates.intentName;
import static com.muffinsoft.alexa.skills.samuraichef.constants.CardConstants.WELCOME_CARD;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_HIGH_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_LEVEL;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_LOW_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_MID_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_PROGRESS;

public class SamuraiActionIntentHandler extends GameActionIntentHandler {

    private static final Logger logger = LoggerFactory.getLogger(SamuraiActionIntentHandler.class);

    private final CardManager cardManager;
    private final SessionStateFabric stateManagerFabric;

    public SamuraiActionIntentHandler(CardManager cardManager, SessionStateFabric stateManagerFabric) {
        this.cardManager = cardManager;
        this.stateManagerFabric = stateManagerFabric;
    }

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("SamuraiActionIntent"));
    }

    @Override
    public SessionStateManager nextTurn(HandlerInput input) {

        AttributesManager attributesManager = input.getAttributesManager();

        boolean userSelectLevel = attributesManager.getSessionAttributes().containsKey(USER_LEVEL);

        Request request = input.getRequestEnvelope().getRequest();

        IntentRequest intentRequest = (IntentRequest) request;

        Map<String, Slot> slots = intentRequest.getIntent().getSlots();

        if (userSelectLevel) {

            handlePersistentAttributes(input);

            UserProgress currentUserProgress = getCurrentUserProgress(input);

            Activities currentActivity;

            if (currentUserProgress.isJustCreated()) {
                currentActivity = getCurrentActivity(input);
            }
            else {
                currentActivity = Activities.valueOf(currentUserProgress.getLastActivity());
            }

            Equipments currentEquipment = Equipments.EMPTY_SLOT;

            if (currentUserProgress.isPowerUpEquipped()) {
                currentEquipment = Equipments.valueOf(currentUserProgress.getEquippedPowerUp());
            }

            SessionStateManager stateManager = stateManagerFabric.createFromRequest(currentActivity, currentEquipment, slots, attributesManager);

            logger.info("Going to handle activity " + currentActivity + " with equipment " + currentEquipment);

            return stateManager;
        }
        else {
            return new SelectLevelStateManager(slots, attributesManager);
        }
    }

    private UserProgress getCurrentUserProgress(HandlerInput input) {

        LinkedHashMap rawUserProgress = (LinkedHashMap) input.getAttributesManager().getSessionAttributes().get(USER_PROGRESS);

        return rawUserProgress != null ? new ObjectMapper().convertValue(rawUserProgress, UserProgress.class) : new UserProgress(true);
    }

    private Activities getCurrentActivity(HandlerInput input) {

        Activities firstActivity = activitiesManager.getFirstActivity();

        String rawActivity = String.valueOf(input.getAttributesManager().getSessionAttributes().getOrDefault(ACTIVITY, firstActivity.name()));
        return Activities.valueOf(rawActivity);
    }

    private void handlePersistentAttributes(HandlerInput input) {

        AttributesManager attributesManager = input.getAttributesManager();

        if (!attributesManager.getSessionAttributes().containsKey(USER_PROGRESS)) {

            UserLevel userLevel = UserLevel.valueOf(String.valueOf(attributesManager.getSessionAttributes().get(USER_LEVEL)));

            String dbSource;

            switch (userLevel) {
                case LOW:
                    dbSource = USER_LOW_PROGRESS_DB;
                    break;
                case MEDIUM:
                    dbSource = USER_MID_PROGRESS_DB;
                    break;
                case HIGH:
                    dbSource = USER_HIGH_PROGRESS_DB;
                    break;
                default:
                    throw new IllegalStateException("");
            }

            if (attributesManager.getPersistentAttributes().containsKey(dbSource)) {

                Map<String, Object> sessionAttributes = attributesManager.getSessionAttributes();

                if (sessionAttributes == null) {
                    sessionAttributes = new HashMap<>();
                }

                String jsonInString = String.valueOf(attributesManager.getPersistentAttributes().get(dbSource));

                try {
                    LinkedHashMap linkedHashMap = new ObjectMapper().readValue(jsonInString, LinkedHashMap.class);
                    sessionAttributes.put(USER_PROGRESS, linkedHashMap);
                }
                catch (IOException e) {
                    throw new IllegalStateException(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public String getPhrase() {
        return null;
    }

    @Override
    public String getSimpleCard() {
        return cardManager.getValueByKey(WELCOME_CARD);
    }
}
