package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Request;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.game.SessionStateManager;
import com.muffinsoft.alexa.sdk.handlers.ActionIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.content.IngredientsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.activities.SushiSliceSessionStateManager;

import java.util.Map;

import static com.amazon.ask.request.Predicates.intentName;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.ACTIVITY;

public class SamuraiActionIntentHandler extends ActionIntentHandler {

    private final PhraseManager phraseManager;
    private final IngredientsManager ingredientsManager;

    public SamuraiActionIntentHandler(PhraseManager phraseManager, IngredientsManager ingredientsManager) {
        this.phraseManager = phraseManager;
        this.ingredientsManager = ingredientsManager;
    }

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("SushiSliceIntent"));
    }

    @Override
    public SessionStateManager nextTurn(HandlerInput input) {

        Request request = input.getRequestEnvelope().getRequest();
        IntentRequest intentRequest = (IntentRequest) request;

        Map<String, Slot> slots = intentRequest.getIntent().getSlots();

        Activities currentActivity = getCurrentActivity(input);

        switch (currentActivity) {
            case SUSHI_SLICE:
                return new SushiSliceSessionStateManager(slots, input.getAttributesManager(), phraseManager, ingredientsManager);
            default:
                return new SushiSliceSessionStateManager(slots, input.getAttributesManager(), phraseManager, ingredientsManager);
        }
    }

    private Activities getCurrentActivity(HandlerInput input) {
        String rawActivity = String.valueOf(input.getAttributesManager().getSessionAttributes().getOrDefault(ACTIVITY, "SUSHI_SLICE"));
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
