package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Directive;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Request;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.dialog.ElicitSlotDirective;
import com.amazon.ask.model.ui.Card;
import com.amazon.ask.model.ui.OutputSpeech;
import com.amazon.ask.model.ui.Reprompt;
import com.amazon.ask.model.ui.SimpleCard;
import com.amazon.ask.model.ui.SsmlOutputSpeech;
import com.muffinsoft.alexa.sdk.handlers.LaunchRequestHandler;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.SlotName;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static com.muffinsoft.alexa.sdk.content.BaseConstants.USERNAME;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.INTRO_PHRASE;

public class SamuraiLaunchRequestHandler extends LaunchRequestHandler {

    private final PhraseManager phraseManager;

    public SamuraiLaunchRequestHandler(PhraseManager phraseManager) {
        super();
        this.phraseManager = phraseManager;
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {

        String userResponse = getUserResponse(input);

        DialogItem dialogItem;

        if (userResponse == null) {
            dialogItem = new DialogItem(phraseManager.getValueByKey(INTRO_PHRASE + 0), false, SlotName.ACTION.text);
        }
        else {
            input.getAttributesManager().setSessionAttributes(Collections.singletonMap(USERNAME, userResponse));
            dialogItem = new DialogItem(phraseManager.getValueByKey(INTRO_PHRASE + 1), true, SlotName.ACTION.text);
        }

        return Optional.of(assembleResponse(dialogItem));
    }

    private String getUserResponse(HandlerInput input) {

        Request request = input.getRequestEnvelope().getRequest();

        IntentRequest intentRequest = (IntentRequest) request;

        Map<String, Slot> slots = intentRequest.getIntent().getSlots();

        Slot slot = slots.get(SlotName.ACTION.text);

        return slot.getValue();
    }

    @Override
    public String getPhrase() {
        return phraseManager.getValueByKey("welcome");
    }

    @Override
    public String getSimpleCard() {
        return phraseManager.getValueByKey("welcomeCard");
    }

    private Response assembleResponse(DialogItem dialog) {

        String speechText = dialog.getResponseText();

        OutputSpeech speech = SsmlOutputSpeech.builder()
                .withSsml("<speak>" + speechText + "</speak>")
                .build();

        Card card = SimpleCard.builder()
                .withTitle(this.getSimpleCard())
                .withContent(speechText)
                .build();

        Response.Builder response = Response.builder()
                .withOutputSpeech(speech)
                .withCard(card)
                .withShouldEndSession(dialog.isEnd());

        if (dialog.getSlotName() != null) {
            Directive directive = ElicitSlotDirective.builder()
                    .withSlotToElicit(dialog.getSlotName())
                    .build();
            response = response.addDirectivesItem(directive);
        }

        if (dialog.isRepromptRequired()) {
            Reprompt reprompt = Reprompt.builder()
                    .withOutputSpeech(speech)
                    .build();
            response = response.withReprompt(reprompt);
        }

        return response.build();
    }
}
