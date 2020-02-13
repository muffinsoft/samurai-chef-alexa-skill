package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.services.monetization.InSkillProduct;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.activities.StateManager;
import com.muffinsoft.alexa.sdk.components.DialogTranslator;
import com.muffinsoft.alexa.sdk.enums.IntentType;
import com.muffinsoft.alexa.sdk.handlers.WhatCanIBuyIntentHandler;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.PhraseContainer;
import com.muffinsoft.alexa.sdk.util.PurchaseManager;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;

import java.util.List;

import static com.muffinsoft.alexa.sdk.constants.SessionConstants.INTENT;

public class SamuraiWhatCanIBuyHandler extends WhatCanIBuyIntentHandler {

    private final DialogTranslator dialogTranslator;
    private final PhraseDependencyContainer phraseDependencyContainer;

    public SamuraiWhatCanIBuyHandler(SettingsDependencyContainer configurationContainer, PhraseDependencyContainer phraseDependencyContainer) {
        this.dialogTranslator = configurationContainer.getDialogTranslator();
        this.phraseDependencyContainer = phraseDependencyContainer;
    }

    @Override
    public StateManager nextTurn(HandlerInput input) {
        return new BaseStateManager(getSlotsFromInput(input), input.getAttributesManager(), dialogTranslator) {
            @Override
            public DialogItem nextResponse() {
                InSkillProduct product = PurchaseManager.getInSkillProduct(input);
                List<PhraseContainer> response;
                boolean arePurchasesEnabled = (boolean) getSessionAttributes().get("arePurchasesEnabled");
                getSessionAttributes().put(INTENT, IntentType.GAME);
                if (PurchaseManager.isEntitled(product)) {
                    response = phraseDependencyContainer.getRegularPhraseManager().getValueByKey("alreadyHave");
                    getSessionAttributes().put(MENU_OR_CONTINUE, "true");
                } else if (!arePurchasesEnabled) {
                    response = phraseDependencyContainer.getRegularPhraseManager().getValueByKey("unrecognized");
                } else if(PurchaseManager.isAvailable(product)) {
                    response = phraseDependencyContainer.getRegularPhraseManager().getValueByKey("purchaseWhat");
                    getSessionAttributes().put(INTENT, IntentType.BUY_INTENT);
                } else {
                    response = phraseDependencyContainer.getRegularPhraseManager().getValueByKey("purchaseNothing");
                    getSessionAttributes().put(MENU_OR_CONTINUE, "true");
                }
                return DialogItem.builder()
                        .addResponse(dialogTranslator.translate(response, true))
                        .withReprompt(dialogTranslator.translate(response, true))
                        .build();
            }
        };
    }
}
