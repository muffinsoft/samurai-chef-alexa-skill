package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.services.monetization.EntitlementReason;
import com.amazon.ask.model.services.monetization.InSkillProduct;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.activities.StateManager;
import com.muffinsoft.alexa.sdk.components.DialogTranslator;
import com.muffinsoft.alexa.sdk.enums.IntentType;
import com.muffinsoft.alexa.sdk.handlers.RefundIntentHandler;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.PhraseContainer;
import com.muffinsoft.alexa.sdk.util.PurchaseManager;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;

import java.util.List;

import static com.muffinsoft.alexa.sdk.constants.SessionConstants.INTENT;
import static com.muffinsoft.alexa.sdk.enums.IntentType.CONTINUE_OR_MENU;

public class SamuraiRefundIntentHandler extends RefundIntentHandler {

    private final DialogTranslator dialogTranslator;
    private final PhraseDependencyContainer phraseDependencyContainer;

    public SamuraiRefundIntentHandler(SettingsDependencyContainer configurationContainer, PhraseDependencyContainer phraseDependencyContainer) {
        this.dialogTranslator = configurationContainer.getDialogTranslator();
        this.phraseDependencyContainer = phraseDependencyContainer;
    }

    @Override
    public StateManager nextTurn(HandlerInput input) {
        return new BaseStateManager(getSlotsFromInput(input), input.getAttributesManager(), dialogTranslator) {
            @Override
            public DialogItem nextResponse() {
                boolean arePurchasesEnabled = (boolean) getSessionAttributes().get("arePurchasesEnabled");
                List<PhraseContainer> response;
                InSkillProduct product = PurchaseManager.getInSkillProduct(input);
                getSessionAttributes().put(INTENT, IntentType.GAME);
                if (PurchaseManager.isEntitled(product) && product.getEntitlementReason() == EntitlementReason.AUTO_ENTITLED || !arePurchasesEnabled) {
                    logger.debug("Refund request, entitlement reason: {}, purchases enabled: {}", product.getEntitlementReason(), arePurchasesEnabled);
                    response = phraseDependencyContainer.getRegularPhraseManager().getValueByKey("unrecognized");
                } else {
                    logger.debug("Refund request, not entitled");
                    response = phraseDependencyContainer.getRegularPhraseManager().getValueByKey("purchaseNoRefund");
                    getSessionAttributes().put(INTENT, CONTINUE_OR_MENU);
                }
                return DialogItem.builder()
                        .addResponse(dialogTranslator.translate(response, true))
                        .withReprompt(dialogTranslator.translate(response, true))
                        .build();
            }
        };
    }
}
