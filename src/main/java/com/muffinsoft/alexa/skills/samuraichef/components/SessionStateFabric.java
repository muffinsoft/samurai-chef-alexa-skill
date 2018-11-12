package com.muffinsoft.alexa.skills.samuraichef.components;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.SessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.FoodTasterCorrectAnswerSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.FoodTasterSecondChanceSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.FoodTasterSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.JuiceWarriorCorrectAnswerSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.JuiceWarriorSecondChanceSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.JuiceWarriorSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.SushiSliceCorrectAnswerSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.SushiSliceSecondChanceSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.SushiSliceSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.WordBoardKarateCorrectAnswerSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.WordBoardKarateSecondChanceSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.WordBoardKarateSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivityManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.MissionManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.Equipments;

import java.util.Map;

public class SessionStateFabric {

    private final PhraseManager phraseManager;
    private final ActivityManager activityManager;
    private final PowerUpsManager powerUpsManager;
    private final MissionManager missionManager;

    public SessionStateFabric(PhraseManager phraseManager, ActivityManager activityManager, PowerUpsManager powerUpsManager, MissionManager missionManager) {
        this.phraseManager = phraseManager;
        this.activityManager = activityManager;
        this.powerUpsManager = powerUpsManager;
        this.missionManager = missionManager;
    }

    public SessionStateManager createFromRequest(Activities currentActivity, Equipments currentEquipment, Map<String, Slot> slots, AttributesManager attributesManager) {

        SessionStateManager stateManager;

        switch (currentActivity) {

//            case NAME_HANDLER:
//                stateManager = new NameHandlerSessionStateManager(slots, input.getAttributesManager(), phraseManager, activitiesManager, activityManager, powerUpsManager);
//                break;
            case SUSHI_SLICE:
                stateManager = createSushiSliceSessionStateManager(currentEquipment, slots, attributesManager);
                break;
            case JUICE_WARRIOR:
                stateManager = createJuiceWarriorSessionStateManager(currentEquipment, slots, attributesManager);
                break;
            case WORD_BOARD_KARATE:
                stateManager = createWordBoardKarateSessionStateManager(currentEquipment, slots, attributesManager);
                break;
            case FOOD_TASTER:
                stateManager = createFoodTasterSessionStateManager(currentEquipment, slots, attributesManager);
                break;
            default:
                throw new IllegalStateException("Exception while handling activity: " + currentActivity);
        }

        return stateManager;
    }

    private FoodTasterSessionStateManager createFoodTasterSessionStateManager(Equipments currentEquipment, Map<String, Slot> slots, AttributesManager attributesManager) {

        switch (currentEquipment) {
            case EMPTY_SLOT:
                return new FoodTasterSessionStateManager(slots, attributesManager, phraseManager, activityManager, powerUpsManager, missionManager);
            case CORRECT_ANSWER_SLOT:
                return new FoodTasterCorrectAnswerSessionStateManager(slots, attributesManager, phraseManager, activityManager, powerUpsManager, missionManager);
            case SECOND_CHANCE_SLOT:
                return new FoodTasterSecondChanceSessionStateManager(slots, attributesManager, phraseManager, activityManager, powerUpsManager, missionManager);
            default:
                throw new IllegalStateException("Exception while handling equipment: " + currentEquipment);
        }
    }

    private WordBoardKarateSessionStateManager createWordBoardKarateSessionStateManager(Equipments currentEquipment, Map<String, Slot> slots, AttributesManager attributesManager) {

        switch (currentEquipment) {
            case EMPTY_SLOT:
                return new WordBoardKarateSessionStateManager(slots, attributesManager, phraseManager, activityManager, powerUpsManager, missionManager);
            case CORRECT_ANSWER_SLOT:
                return new WordBoardKarateCorrectAnswerSessionStateManager(slots, attributesManager, phraseManager, activityManager, powerUpsManager, missionManager);
            case SECOND_CHANCE_SLOT:
                return new WordBoardKarateSecondChanceSessionStateManager(slots, attributesManager, phraseManager, activityManager, powerUpsManager, missionManager);
            default:
                throw new IllegalStateException("Exception while handling equipment: " + currentEquipment);
        }
    }

    private SushiSliceSessionStateManager createSushiSliceSessionStateManager(Equipments currentEquipment, Map<String, Slot> slots, AttributesManager attributesManager) {

        switch (currentEquipment) {
            case EMPTY_SLOT:
                return new SushiSliceSessionStateManager(slots, attributesManager, phraseManager, activityManager, powerUpsManager, missionManager);
            case CORRECT_ANSWER_SLOT:
                return new SushiSliceCorrectAnswerSessionStateManager(slots, attributesManager, phraseManager, activityManager, powerUpsManager, missionManager);
            case SECOND_CHANCE_SLOT:
                return new SushiSliceSecondChanceSessionStateManager(slots, attributesManager, phraseManager, activityManager, powerUpsManager, missionManager);
            default:
                throw new IllegalStateException("Exception while handling equipment: " + currentEquipment);
        }
    }

    private JuiceWarriorSessionStateManager createJuiceWarriorSessionStateManager(Equipments currentEquipment, Map<String, Slot> slots, AttributesManager attributesManager) {

        switch (currentEquipment) {
            case EMPTY_SLOT:
                return new JuiceWarriorSessionStateManager(slots, attributesManager, phraseManager, activityManager, powerUpsManager, missionManager);
            case CORRECT_ANSWER_SLOT:
                return new JuiceWarriorCorrectAnswerSessionStateManager(slots, attributesManager, phraseManager, activityManager, powerUpsManager, missionManager);
            case SECOND_CHANCE_SLOT:
                return new JuiceWarriorSecondChanceSessionStateManager(slots, attributesManager, phraseManager, activityManager, powerUpsManager, missionManager);
            default:
                throw new IllegalStateException("Exception while handling equipment: " + currentEquipment);
        }
    }
}
