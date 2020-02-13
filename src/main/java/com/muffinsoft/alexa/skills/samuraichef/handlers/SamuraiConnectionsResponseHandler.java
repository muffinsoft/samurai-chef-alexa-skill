package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.connections.ConnectionsResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muffinsoft.alexa.sdk.components.DialogTranslator;
import com.muffinsoft.alexa.sdk.enums.IntentType;
import com.muffinsoft.alexa.sdk.enums.PurchaseState;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.PhraseContainer;
import com.muffinsoft.alexa.sdk.util.BaseResponseAssembler;
import com.muffinsoft.alexa.sdk.util.ResponseAssembler;
import com.muffinsoft.alexa.skills.samuraichef.IoC;
import com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.muffinsoft.alexa.sdk.constants.SessionConstants.INTENT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.PURCHASE_STATE;

public class SamuraiConnectionsResponseHandler implements com.amazon.ask.dispatcher.request.handler.impl.ConnectionsResponseHandler {

    private static final Logger logger = LoggerFactory.getLogger(SamuraiConnectionsResponseHandler.class);

    private final DialogTranslator dialogTranslator;
    private final PhraseDependencyContainer phraseDependencyContainer;
    private final ResponseAssembler responseAssembler;

    public SamuraiConnectionsResponseHandler(SettingsDependencyContainer configurationContainer, PhraseDependencyContainer phraseDependencyContainer) {
        this.dialogTranslator = configurationContainer.getDialogTranslator();
        this.phraseDependencyContainer = phraseDependencyContainer;
        this.responseAssembler = new BaseResponseAssembler();
    }

    @Override
    public boolean canHandle(HandlerInput input, ConnectionsResponse connectionsResponse) {
        String name = input.getRequestEnvelopeJson().get("request").get("name").asText();
        return (name.equalsIgnoreCase("Buy") || name.equalsIgnoreCase("Upsell"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input, ConnectionsResponse connectionsResponse) {
        JsonNode token = input.getRequestEnvelopeJson().get("request").get("token");
        AttributesManager attributesManager = input.getAttributesManager();
        Map<String, Object> sessionAttributes = token != null ? getSessionAttributes(token) :
                verifyMap(attributesManager.getSessionAttributes());

        sessionAttributes.put(INTENT, IntentType.GAME);

        attributesManager.setSessionAttributes(sessionAttributes);

        Map<String, Object> persistentAttributes = verifyMap(attributesManager.getPersistentAttributes());
        attributesManager.setPersistentAttributes(persistentAttributes);
        persistentAttributes.put(SessionConstants.LAST_PURCHASE_ATTEMPT_ON, ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT));


        String code = input.getRequestEnvelopeJson().get("request").get("status").get("code").asText();

        List<PhraseContainer> speechText;
        DialogItem dialog;

        if (code.equalsIgnoreCase("200")) {
            String purchaseResult = input.getRequestEnvelopeJson().get("request").get("payload").get("purchaseResult").asText();

            logger.debug("Received connection response {}", purchaseResult);

            switch (purchaseResult) {
                case "PENDING_PURCHASE":
                    persistentAttributes.put(PURCHASE_STATE, PurchaseState.PENDING.name());
                    sessionAttributes.put(INTENT, IntentType.SELECT_MISSION);
                    speechText = phraseDependencyContainer.getRegularPhraseManager().getValueByKey("purchaseWait");
                    break;
                case "ACCEPTED": {
                    persistentAttributes.put(PURCHASE_STATE, PurchaseState.ENTITLED.name());
                    speechText = phraseDependencyContainer.getRegularPhraseManager().getValueByKey("purchaseHistory");
                    break;
                }
                case "DECLINED": {
                    persistentAttributes.put(PURCHASE_STATE, PurchaseState.NOT_ENTITLED.name());
                    sessionAttributes.put(INTENT, IntentType.SELECT_MISSION);
                    return new SamuraiActionIntentHandler(IoC.provideIntentFactory()).handle(input);
                }
                case "ALREADY_PURCHASED": {
                    speechText = phraseDependencyContainer.getRegularPhraseManager().getValueByKey("purchaseAlreadyDone");
                    persistentAttributes.put(PURCHASE_STATE, PurchaseState.ENTITLED.name());
                    break;
                }
                default:
                    speechText = phraseDependencyContainer.getRegularPhraseManager().getValueByKey("purchaseUnsuccessful");
                    persistentAttributes.put(PURCHASE_STATE, PurchaseState.FAILED.name());
                    break;
            }

        } else {
            //Something failed
            System.out.println("Connections.Response indicated failure. error: " + input.getRequestEnvelopeJson().get("request").get("status").get("message").toString());
            persistentAttributes.put(PURCHASE_STATE, PurchaseState.FAILED.name());
            speechText = phraseDependencyContainer.getRegularPhraseManager().getValueByKey("purchaseError");
        }

        dialog = DialogItem.builder()
                .addResponse(dialogTranslator.translate(speechText, true))
                .build();
        attributesManager.savePersistentAttributes();
        return responseAssembler.assemble(input, dialog);
    }

    public static Map<String, Object> getSessionAttributes(JsonNode jsonNode) {
        String json = jsonNode.toString().replaceAll("^\"|\"$|\\\\", "");
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            TypeReference<HashMap<String, Object>> mapType = new TypeReference<HashMap<String, Object>>() {
            };
            return mapper.readValue(json, mapType);
        } catch (IOException e) {
            throw new AskSdkException("Unable to read or deserialize data" + e.getMessage());
        }
    }

    private static Map<String, Object> verifyMap(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            map = new HashMap<>();
        }
        return map;
    }
}
