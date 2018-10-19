package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Request;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.SessionStateManager;
import com.muffinsoft.alexa.sdk.handlers.ActionIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.activities.FoodTasterSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.JuiceWarriorSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.NameHandlerSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.SushiSliceSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.WordBoardKarateSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivitiesManager;
import com.muffinsoft.alexa.skills.samuraichef.content.IngredientsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;

import java.util.Map;

import static com.amazon.ask.request.Predicates.intentName;
import static com.muffinsoft.alexa.skills.samuraichef.content.SamuraiChefSessionConstants.ACTIVITY;
import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.NAME_HANDLER;

public class SamuraiActionIntentHandler extends ActionIntentHandler {

    private final PhraseManager phraseManager;
    private final ActivitiesManager activitiesManager;
    private final IngredientsManager ingredientsManager;

    public SamuraiActionIntentHandler(PhraseManager phraseManager, IngredientsManager ingredientsManager, ActivitiesManager activitiesManager) {
        this.phraseManager = phraseManager;
        this.ingredientsManager = ingredientsManager;
        this.activitiesManager = activitiesManager;
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

        switch (currentActivity) {
            case NAME_HANDLER:
                stateManager = new NameHandlerSessionStateManager(slots, input.getAttributesManager(), phraseManager, ingredientsManager, activitiesManager);
                break;
            case SUSHI_SLICE:
                stateManager = new SushiSliceSessionStateManager(slots, input.getAttributesManager(), phraseManager, ingredientsManager, activitiesManager);
                break;
            case JUICE_WARRIOR:
                stateManager = new JuiceWarriorSessionStateManager(slots, input.getAttributesManager(), phraseManager, ingredientsManager, activitiesManager);
                break;
            case WORD_BOARD_KARATE:
                stateManager = new WordBoardKarateSessionStateManager(slots, input.getAttributesManager(), phraseManager, ingredientsManager, activitiesManager);
                break;
            case FOOD_TASTER:
                stateManager = new FoodTasterSessionStateManager(slots, input.getAttributesManager(), phraseManager, ingredientsManager, activitiesManager);
                break;
            default:
                throw new IllegalStateException("Exception while handling activity: " + currentActivity);
        }

        return stateManager;
    }

    private Activities getCurrentActivity(HandlerInput input) {
        String rawActivity = String.valueOf(input.getAttributesManager().getSessionAttributes().getOrDefault(ACTIVITY, NAME_HANDLER.name()));
        return Activities.valueOf(rawActivity);
    }

    @Override
    public String getPhrase() {
        // not used in current version
        return null;
    }

    @Override
    public String getSimpleCard() {
        return phraseManager.getValueByKey("welcomeCard");
    }
}
