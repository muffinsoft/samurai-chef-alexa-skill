package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.IngredientsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;

import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

import static com.muffinsoft.alexa.sdk.content.BaseConstants.USERNAME;
import static com.muffinsoft.alexa.sdk.content.BaseConstants.USERNAME_PLACEHOLDER;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.INGREDIENT_REACTION;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.MISTAKES_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.PREVIOUS_INGREDIENTS;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.QUESTION_TIME;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.SUCCESS_COUNT;

public class JuiceWarriorSessionStateManager extends BaseSamuraiChefSessionStateManager {

    private String currentIngredientReaction;
    private String userName;
    private LinkedList<String> previousIngredients;
    private StatePhase statePhase;
    private int successCount;
    private int mistakesCount;
    private Long questionTime;

    public JuiceWarriorSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, IngredientsManager ingredientsManager) {
        super(slots, attributesManager, phraseManager, ingredientsManager);
        this.currentActivity = Activities.JUICE_WARRIOR;
    }

    @Override
    protected void populateActivityVariables() {
        //noinspection unchecked
        previousIngredients = (LinkedList<String>) sessionAttributes.get(PREVIOUS_INGREDIENTS);
        statePhase = StatePhase.valueOf(String.valueOf(sessionAttributes.get(STATE_PHASE)));
        successCount = (int) sessionAttributes.get(SUCCESS_COUNT);
        mistakesCount = (int) sessionAttributes.get(MISTAKES_COUNT);
        questionTime = (Long) sessionAttributes.get(QUESTION_TIME);
        userName = String.valueOf(sessionAttributes.get(USERNAME));
        Object ingredient = sessionAttributes.get(INGREDIENT_REACTION);
        currentIngredientReaction = ingredient != null ? String.valueOf(ingredient) : null;
    }

    @Override
    protected void updateSessionAttributes() {
        sessionAttributes.put(MISTAKES_COUNT, mistakesCount);
        sessionAttributes.put(SUCCESS_COUNT, successCount);
        sessionAttributes.put(STATE_PHASE, statePhase);
        sessionAttributes.put(QUESTION_TIME, System.currentTimeMillis());
    }

    @Override
    public DialogItem nextResponse() {

        DialogItem dialog;


        if (this.statePhase == StatePhase.INTRO) {
            dialog = getIntroDialog(this.currentActivity);
        }
        else if (this.statePhase == StatePhase.DEMO) {
            dialog = getDemoDialog(this.currentActivity);
        }
        else {

            if (this.successCount == 10) {
                this.statePhase = StatePhase.PHASE_2;
            }

            long answerTime = System.currentTimeMillis();

            if (Objects.equals(currentIngredientReaction, userReply)) {
                if (questionTime == null || questionTime - answerTime < 3000) {
                    dialog = getSuccessDialog();
                }
                else {
                    dialog = getFailureDialog("<emphasis level=\"reduced\">Too long!");
                }
            }
            else {
                dialog = getFailureDialog("<emphasis level=\"reduced\">Wrong!");
            }

            sessionAttributes.put(QUESTION_TIME, System.currentTimeMillis());
        }

        String responseText = dialog.getResponseText().replace(USERNAME_PLACEHOLDER, userName);
        dialog.setResponseText(responseText);

        return dialog;
    }
}
