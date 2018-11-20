package com.muffinsoft.alexa.skills.samuraichef.activities.action;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.Speech;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.IngredientReaction;

import java.util.Map;

import static com.muffinsoft.alexa.sdk.model.Speech.ofAlexa;
import static com.muffinsoft.alexa.sdk.model.Speech.ofIvy;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WON_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WON_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.QUESTION_TIME;
import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.SUSHI_SLICE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.WIN;

public class SushiSliceStateManager extends BaseActivePhaseSamuraiChefStateManager {

    private Long questionTime;

    public SushiSliceStateManager(Map<String, Slot> slots, AttributesManager attributesManager, ConfigContainer configContainer) {
        super(slots, attributesManager, configContainer);
        currentActivity = SUSHI_SLICE;
    }

    @Override
    protected DialogItem.Builder handleSuccess(DialogItem.Builder builder) {

        long answerTime = System.currentTimeMillis();

        long answerLimit = this.stripe.getTimeLimitPhaseOneInMillis();

        if (questionTime == null || answerTime - questionTime < answerLimit) {

            builder = super.handleSuccess(builder);
        }
        else {
            builder = handleTooLongMistake(builder);
        }

        builder = appendMockCompetitionAnswer(builder);

        return builder;
    }

    private DialogItem.Builder appendMockCompetitionAnswer(DialogItem.Builder builder) {

        Speech speech = builder.popLastSpeech();

        IngredientReaction randomIngredient = getRandomIngredient();

        builder.addResponse(ofAlexa(randomIngredient.getIngredient()))
                .addResponse(ofIvy(randomIngredient.getUserReply()))
                .addResponse(speech);

        return builder;
    }

    @Override
    DialogItem.Builder getWinDialog(DialogItem.Builder builder) {
        this.statePhase = WIN;

        IngredientReaction randomIngredient = getRandomIngredient();

        String wrongReplyOnIngredient = getWrongReplyOnIngredient(randomIngredient.getIngredient());

        return builder
                .replaceResponse(ofAlexa(randomIngredient.getIngredient()))
                .addResponse(ofIvy(randomIngredient.getUserReply()))
                .addResponse(ofAlexa(wrongReplyOnIngredient))
                .withSlotName(actionSlotName)
                .withReprompt(ofAlexa(phraseManager.getValueByKey(WON_REPROMPT_PHRASE)));
    }

    @Override
    protected void resetActivityProgress() {
        super.resetActivityProgress();
        this.questionTime = null;
    }

    @Override
    protected void populateActivityVariables() {
        super.populateActivityVariables();
        questionTime = (Long) getSessionAttributes().get(QUESTION_TIME);
    }

    @Override
    protected void updateSessionAttributes() {
        super.updateSessionAttributes();
        getSessionAttributes().put(QUESTION_TIME, System.currentTimeMillis());
    }
}
