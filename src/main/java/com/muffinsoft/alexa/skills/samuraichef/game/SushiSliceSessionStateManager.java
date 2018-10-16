package com.muffinsoft.alexa.skills.samuraichef.game;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.game.BaseSessionStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.IngredientsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.FAILURE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.INGREDIENT_REACTION;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.MISTAKES_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.PREVIOUS_INGREDIENTS;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.QUESTION_TIME;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.STATE_ITERATION;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.SUCCESS_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.SUSHI_SLICE_DEMO_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.SUSHI_SLICE_INTRO_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.USERNAME;
import static com.muffinsoft.alexa.skills.samuraichef.content.SushiSliceConstants.USERNAME_PLACEHOLDER;

public class SushiSliceSessionStateManager extends BaseSessionStateManager {

    private final PhraseManager phraseManager;
    private final IngredientsManager ingredientsManager;

    private String currentIngredientReaction;
    private String userName;
    private LinkedList<String> previousIngredients;
    private StatePhase statePhase;
    private int stateIteration;
    private int successCount;
    private int mistakesCount;
    private Long questionTime;

    public SushiSliceSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, IngredientsManager ingredientsManager) {
        super(slots, attributesManager);
        this.phraseManager = phraseManager;
        this.ingredientsManager = ingredientsManager;
    }

    @Override
    protected void initializeGame() {
        sessionAttributes = new HashMap<>();
        sessionAttributes.put(STATE_PHASE, StatePhase.INTRO);
        sessionAttributes.put(STATE_ITERATION, 0);
        sessionAttributes.put(MISTAKES_COUNT, 0);
        sessionAttributes.put(SUCCESS_COUNT, 0);
        sessionAttributes.put(PREVIOUS_INGREDIENTS, new LinkedList<String>());
    }

    @Override
    protected void populateFields() {
        //noinspection unchecked
        previousIngredients = (LinkedList<String>) sessionAttributes.get(PREVIOUS_INGREDIENTS);
        statePhase = StatePhase.valueOf(String.valueOf(sessionAttributes.get(STATE_PHASE)));
        successCount = (int) sessionAttributes.get(SUCCESS_COUNT);
        mistakesCount = (int) sessionAttributes.get(MISTAKES_COUNT);
        stateIteration = (int) sessionAttributes.get(STATE_ITERATION);
        questionTime = (Long) sessionAttributes.get(QUESTION_TIME);
        userName = String.valueOf(sessionAttributes.get(USERNAME));
        Object ingredient = sessionAttributes.get(INGREDIENT_REACTION);
        currentIngredientReaction = ingredient != null ? String.valueOf(ingredient) : null;
    }

    @Override
    public DialogItem nextResponse() {

        DialogItem dialog;


        if (this.statePhase == StatePhase.INTRO) {
            dialog = getIntroDialog();
        }
        else if (this.statePhase == StatePhase.DEMO) {
            dialog = getDemoDialog();
        }
        else {

            if (this.stateIteration == 10) {
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

        updateSession();
        return dialog;
    }

    private DialogItem getSuccessDialog() {
        String speechText = "<emphasis level=\"reduced\">";
        this.successCount++;
        speechText = nextIngredient(speechText);
        speechText += "</emphasis>";
        return new DialogItem(speechText, false, slotName);
    }

    private DialogItem getFailureDialog(String speechText) {
        this.mistakesCount++;
        if (this.mistakesCount < 3) {
            speechText = nextIngredient(speechText);
            speechText += "</emphasis>";
            return new DialogItem(speechText, false, slotName);
        }
        else {
            return new DialogItem(phraseManager.getValueByKey(FAILURE_PHRASE), true, slotName);
        }
    }

    private DialogItem getDemoDialog() {
        this.statePhase = StatePhase.PHASE_1;
        return new DialogItem(phraseManager.getValueByKey(SUSHI_SLICE_DEMO_PHRASE), false, slotName, true);
    }

    private DialogItem getIntroDialog() {

        if (stateIteration == 0) {
            userName = userReply;
            sessionAttributes.put(USERNAME, userName);
        }
        this.statePhase = StatePhase.DEMO;
        return new DialogItem(phraseManager.getValueByKey(SUSHI_SLICE_INTRO_PHRASE), false, slotName, true);
    }

    private void updateSession() {
        sessionAttributes.put(MISTAKES_COUNT, mistakesCount);
        sessionAttributes.put(SUCCESS_COUNT, successCount);
        sessionAttributes.put(STATE_PHASE, statePhase);
        sessionAttributes.put(STATE_ITERATION, stateIteration);
        sessionAttributes.put(QUESTION_TIME, System.currentTimeMillis());
        attributesManager.setSessionAttributes(sessionAttributes);
    }

    private String nextIngredient(String speechText) {
        String nextIngredient = ingredientsManager.getNextIngredient(this.previousIngredients);
        this.previousIngredients.addFirst(nextIngredient);
        if (this.previousIngredients.size() > 2) {
            this.previousIngredients.removeLast();
        }
        sessionAttributes.put(PREVIOUS_INGREDIENTS, this.previousIngredients);
        sessionAttributes.put(INGREDIENT_REACTION, ingredientsManager.getValueByKey(nextIngredient));
        return speechText + " " + nextIngredient + "!";
    }

    private enum StatePhase {
        INTRO,
        DEMO,
        PHASE_1,
        PHASE_2
    }
}
