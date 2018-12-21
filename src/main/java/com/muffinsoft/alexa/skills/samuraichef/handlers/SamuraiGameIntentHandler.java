package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.attributes.AttributesManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muffinsoft.alexa.sdk.components.IntentFactory;
import com.muffinsoft.alexa.sdk.constants.SessionConstants;
import com.muffinsoft.alexa.sdk.enums.IntentType;
import com.muffinsoft.alexa.sdk.handlers.GameIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.FINISHED_MISSIONS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STAR_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_HIGH_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_LOW_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_MID_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_PROGRESS;

abstract class SamuraiGameIntentHandler extends GameIntentHandler {

    SamuraiGameIntentHandler(IntentFactory intentFactory) {
        super(intentFactory);
    }

    @Override
    protected void handleAttributes(AttributesManager attributesManager) {
        Map<String, Object> sessionAttributes = attributesManager.getSessionAttributes();
        logger.info("Session Attributes: " + sessionAttributes);

        Map<String, Object> persistentAttributes = attributesManager.getPersistentAttributes();
        logger.info("Persistent Attributes: " + persistentAttributes);

        if (!attributesManager.getSessionAttributes().containsKey(STAR_COUNT)) {
            int starCount = ((BigDecimal) persistentAttributes.getOrDefault(STAR_COUNT, BigDecimal.ZERO)).intValue();
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

        if (persistentAttributes.containsKey(dbSource)) {

            if (sessionAttributes == null) {
                sessionAttributes = new HashMap<>();
            }

            String jsonInString = String.valueOf(persistentAttributes.get(dbSource));

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

    @Override
    protected IntentType getIntentFromRequest(AttributesManager attributesManager) {
        Object rawIntent = attributesManager.getSessionAttributes().get(SessionConstants.INTENT);
        if (rawIntent == null) {
            logger.info("Was evoked action intent handler with default Intent Type");
            return IntentType.GAME;
        }
        else {
            String stringifyIntent = String.valueOf(rawIntent);
            logger.info("Was evoked action intent handler with Intent Type: " + stringifyIntent);
            return IntentType.valueOf(stringifyIntent);
        }
    }
}
