package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Request;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.SessionStateManager;
import com.muffinsoft.alexa.sdk.handlers.ActionIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.activities.FoodTasterSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.JuiceWarriorSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.SushiSliceSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.WordBoardKarateSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivitiesManager;
import com.muffinsoft.alexa.skills.samuraichef.content.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.content.LevelManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.amazon.ask.request.Predicates.intentName;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY;
import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.SUSHI_SLICE;

public class SamuraiActionIntentHandler extends ActionIntentHandler {

    private static final Logger logger = LoggerFactory.getLogger(SamuraiActionIntentHandler.class);

    private final PhraseManager phraseManager;
    private final CardManager cardManager;
    private final ActivitiesManager activitiesManager;
    private final LevelManager levelManager;
    private final PowerUpsManager powerUpsManager;

    public SamuraiActionIntentHandler(PhraseManager phraseManager, ActivitiesManager activitiesManager, CardManager cardManager, LevelManager levelManager, PowerUpsManager powerUpsManager) {
        this.phraseManager = phraseManager;
        this.activitiesManager = activitiesManager;
        this.cardManager = cardManager;
        this.levelManager = levelManager;
        this.powerUpsManager = powerUpsManager;
    }

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("SamuraiActionIntent"));
    }

    @Override
    public SessionStateManager nextTurn(HandlerInput input) {

        Request request = input.getRequestEnvelope().getRequest();
        IntentRequest intentRequest = (IntentRequest) request;

        Map<String, Slot> slots = intentRequest.getIntent().getSlots();

        Activities currentActivity = getCurrentActivity(input);

        SessionStateManager stateManager;

        logger.info("Going to handle activity " + currentActivity);

        switch (currentActivity) {
//            case NAME_HANDLER:
//                stateManager = new NameHandlerSessionStateManager(slots, input.getAttributesManager(), phraseManager, activitiesManager, levelManager, powerUpsManager);
//                break;
            case SUSHI_SLICE:
                stateManager = new SushiSliceSessionStateManager(slots, input.getAttributesManager(), phraseManager, activitiesManager, levelManager, powerUpsManager);
                break;
            case JUICE_WARRIOR:
                stateManager = new JuiceWarriorSessionStateManager(slots, input.getAttributesManager(), phraseManager, activitiesManager, levelManager, powerUpsManager);
                break;
            case WORD_BOARD_KARATE:
                stateManager = new WordBoardKarateSessionStateManager(slots, input.getAttributesManager(), phraseManager, activitiesManager, levelManager, powerUpsManager);
                break;
            case FOOD_TASTER:
                stateManager = new FoodTasterSessionStateManager(slots, input.getAttributesManager(), phraseManager, activitiesManager, levelManager, powerUpsManager);
                break;
            default:
                throw new IllegalStateException("Exception while handling activity: " + currentActivity);
        }

        return stateManager;
    }

    private Activities getCurrentActivity(HandlerInput input) {
        String rawActivity = String.valueOf(input.getAttributesManager().getSessionAttributes().getOrDefault(ACTIVITY, SUSHI_SLICE.name()));
        return Activities.valueOf(rawActivity);
    }

    @Override
    public String getPhrase() {
        return null;
    }

    @Override
    public String getSimpleCard() {
        return cardManager.getValueByKey("welcome");
    }
}
