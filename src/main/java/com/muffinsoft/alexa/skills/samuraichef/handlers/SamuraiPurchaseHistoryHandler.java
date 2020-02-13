package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.services.monetization.EntitlementReason;
import com.amazon.ask.model.services.monetization.InSkillProduct;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.activities.StateManager;
import com.muffinsoft.alexa.sdk.components.DialogTranslator;
import com.muffinsoft.alexa.sdk.enums.PurchaseState;
import com.muffinsoft.alexa.sdk.handlers.PurchaseHistoryHandler;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.PhraseContainer;
import com.muffinsoft.alexa.sdk.util.PurchaseManager;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.muffinsoft.alexa.sdk.constants.SessionConstants.INTENT;
import static com.muffinsoft.alexa.sdk.enums.IntentType.*;

public class SamuraiPurchaseHistoryHandler extends PurchaseHistoryHandler {

    private final DialogTranslator dialogTranslator;
    private final PhraseDependencyContainer phraseDependencyContainer;

    public SamuraiPurchaseHistoryHandler(SettingsDependencyContainer configurationContainer, PhraseDependencyContainer phraseDependencyContainer) {
        this.dialogTranslator = configurationContainer.getDialogTranslator();
        this.phraseDependencyContainer = phraseDependencyContainer;
    }

    @Override
    public StateManager nextTurn(HandlerInput input) {
        return new BaseStateManager(getSlotsFromInput(input), input.getAttributesManager(), dialogTranslator) {
            @Override
            public DialogItem nextResponse() {
                InSkillProduct product = PurchaseManager.getInSkillProduct(input);
                PurchaseState previousState = getPreviousPurchaseState(input);
                List<PhraseContainer> response;
                boolean arePurchasesEnabled = (boolean) getSessionAttributes().get("arePurchasesEnabled");
                if(PurchaseManager.isEntitled(product)) {
                    String key = product.getEntitlementReason() == EntitlementReason.AUTO_ENTITLED ? "unknownRequest" : "purchaseHistory";
                    response = phraseDependencyContainer.getRegularPhraseManager().getValueByKey(key);
                    getSessionAttributes().put(INTENT, MENU_OR_CONTINUE);
                } else if (!arePurchasesEnabled) {
                    response = phraseDependencyContainer.getRegularPhraseManager().getValueByKey("unrecognized");
                } else if (PurchaseManager.isPending(product, previousState)) {
                    response = phraseDependencyContainer.getRegularPhraseManager().getValueByKey("purchasePending");
                    getSessionAttributes().put(INTENT, GAME);
                } else if (PurchaseManager.isAvailable(product)) {
                    response = phraseDependencyContainer.getRegularPhraseManager().getValueByKey("purchaseHistoryNothing");
                    getSessionAttributes().put(INTENT, CONTINUE_OR_MENU);
                } else {
                    response = phraseDependencyContainer.getRegularPhraseManager().getValueByKey("purchaseNothing");
                }
                return DialogItem.builder()
                        .addResponse(dialogTranslator.translate(response, true))
                        .withReprompt(dialogTranslator.translate(response, true))
                        .build();
            }
        };
    }

    public static PurchaseState getPreviousPurchaseState(HandlerInput input) {
        Map<String, Object> persistent = input.getAttributesManager().getPersistentAttributes();
        if (persistent == null) {
            persistent = new HashMap<>();
        }
        PurchaseState previousState = null;
        Object previousStateObj = persistent.get("purchaseState");
        if (previousStateObj != null) {
            previousState = PurchaseState.valueOf(previousStateObj.toString());
        }
        return previousState;
    }
}
