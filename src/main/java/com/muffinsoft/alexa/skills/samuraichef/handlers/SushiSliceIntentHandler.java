package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Directive;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Request;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.dialog.ElicitSlotDirective;
import com.amazon.ask.model.ui.Card;
import com.amazon.ask.model.ui.OutputSpeech;
import com.amazon.ask.model.ui.SimpleCard;
import com.amazon.ask.model.ui.SsmlOutputSpeech;
import com.muffinsoft.alexa.skills.samuraichef.content.IngredientsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.model.SlotName;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class SushiSliceIntentHandler implements RequestHandler {

    private static final String ITEM = "item";
    private static final String SLOT_NAME = SlotName.ACTION.text;

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("SushiSliceIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {

        String reply = getReplyFromInput(input);

        Map<String, Object> attributes = input.getAttributesManager().getSessionAttributes();

        String saved = null;

        if (attributes != null) {
            Object savedItem = attributes.get(ITEM);
            saved = String.valueOf(savedItem);
        } else {
            attributes = new HashMap<>();
        }

        boolean shouldEnd = false;
        String speechText;

        if (reply == null || reply.isEmpty()) {

            speechText = PhraseManager.getPhrase("actionDescription");
            speechText = nextIngredient(input, attributes, speechText);

        } else if (IngredientsManager.getIngredientResponse(saved).equals(reply)) {

            speechText = PhraseManager.getPhrase("actionApprove");
            speechText = nextIngredient(input, attributes, speechText);

        } else {

            speechText = PhraseManager.getPhrase("actionFail");
            speechText += " You should to make other action than " + reply +  ". Game session is over";
            shouldEnd = true;
        }

        Response response = assembleResponse(shouldEnd, speechText);

        return Optional.of(response);
    }

    private String getReplyFromInput(HandlerInput input) {

        Request request = input.getRequestEnvelope().getRequest();

        IntentRequest intentRequest = (IntentRequest) request;

        Map<String, Slot> slots = intentRequest.getIntent().getSlots();

        return slots.get(SLOT_NAME).getValue();
    }

    private Response assembleResponse(boolean shouldEnd, String speechText) {

        OutputSpeech speech = SsmlOutputSpeech.builder()
                .withSsml("<speak>" + speechText + "</speak>")
                .build();

        Card card = SimpleCard.builder()
                .withTitle(PhraseManager.getPhrase("welcomeCard"))
                .withContent(speechText)
                .build();

        Directive directive = ElicitSlotDirective.builder()
                .withSlotToElicit(SLOT_NAME)
                .build();

        return Response.builder()
                .withOutputSpeech(speech)
                .withCard(card)
                .addDirectivesItem(directive)
                .withShouldEndSession(shouldEnd)
                .build();
    }

    private String nextIngredient(HandlerInput input, Map<String, Object> attributes, String speechText) {

        String ingredient = IngredientsManager.getIngredient();

        attributes.put(ITEM, ingredient);

        input.getAttributesManager().setSessionAttributes(attributes);

        speechText += " " + ingredient;

        return speechText;
    }
}
