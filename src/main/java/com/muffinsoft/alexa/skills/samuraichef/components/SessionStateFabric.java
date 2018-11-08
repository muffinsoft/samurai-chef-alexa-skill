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
import com.muffinsoft.alexa.skills.samuraichef.content.LevelManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.ProgressManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.Equipments;

import java.util.Map;

public class SessionStateFabric {

    private final PhraseManager phraseManager;
    private final LevelManager levelManager;
    private final PowerUpsManager powerUpsManager;
    private final ProgressManager progressManager;

    public SessionStateFabric(PhraseManager phraseManager, LevelManager levelManager, PowerUpsManager powerUpsManager, ProgressManager progressManager) {
        this.phraseManager = phraseManager;
        this.levelManager = levelManager;
        this.powerUpsManager = powerUpsManager;
        this.progressManager = progressManager;
    }

    public SessionStateManager createFromRequest(Activities currentActivity, Equipments currentEquipment, Map<String, Slot> slots, AttributesManager attributesManager) {

        SessionStateManager stateManager;

        switch (currentActivity) {

//            case NAME_HANDLER:
//                stateManager = new NameHandlerSessionStateManager(slots, input.getAttributesManager(), phraseManager, activitiesManager, levelManager, powerUpsManager);
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
                return new FoodTasterSessionStateManager(slots, attributesManager, phraseManager, levelManager, powerUpsManager, progressManager);
            case CHEF_HAT:
                return new FoodTasterCorrectAnswerSessionStateManager(slots, attributesManager, phraseManager, levelManager, powerUpsManager, progressManager);
            case SECRET_SAUCE:
                return new FoodTasterSecondChanceSessionStateManager(slots, attributesManager, phraseManager, levelManager, powerUpsManager, progressManager);
            default:
                throw new IllegalStateException("Exception while handling equipment: " + currentEquipment);
        }
    }

    private WordBoardKarateSessionStateManager createWordBoardKarateSessionStateManager(Equipments currentEquipment, Map<String, Slot> slots, AttributesManager attributesManager) {

        switch (currentEquipment) {
            case EMPTY_SLOT:
                return new WordBoardKarateSessionStateManager(slots, attributesManager, phraseManager, levelManager, powerUpsManager, progressManager);
            case CHEF_HAT:
                return new WordBoardKarateCorrectAnswerSessionStateManager(slots, attributesManager, phraseManager, levelManager, powerUpsManager, progressManager);
            case SECRET_SAUCE:
                return new WordBoardKarateSecondChanceSessionStateManager(slots, attributesManager, phraseManager, levelManager, powerUpsManager, progressManager);
            default:
                throw new IllegalStateException("Exception while handling equipment: " + currentEquipment);
        }
    }

    private SushiSliceSessionStateManager createSushiSliceSessionStateManager(Equipments currentEquipment, Map<String, Slot> slots, AttributesManager attributesManager) {

        switch (currentEquipment) {
            case EMPTY_SLOT:
                return new SushiSliceSessionStateManager(slots, attributesManager, phraseManager, levelManager, powerUpsManager, progressManager);
            case CHEF_HAT:
                return new SushiSliceCorrectAnswerSessionStateManager(slots, attributesManager, phraseManager, levelManager, powerUpsManager, progressManager);
            case SECRET_SAUCE:
                return new SushiSliceSecondChanceSessionStateManager(slots, attributesManager, phraseManager, levelManager, powerUpsManager, progressManager);
            default:
                throw new IllegalStateException("Exception while handling equipment: " + currentEquipment);
        }
    }

    private JuiceWarriorSessionStateManager createJuiceWarriorSessionStateManager(Equipments currentEquipment, Map<String, Slot> slots, AttributesManager attributesManager) {

        switch (currentEquipment) {
            case EMPTY_SLOT:
                return new JuiceWarriorSessionStateManager(slots, attributesManager, phraseManager, levelManager, powerUpsManager, progressManager);
            case CHEF_HAT:
                return new JuiceWarriorCorrectAnswerSessionStateManager(slots, attributesManager, phraseManager, levelManager, powerUpsManager, progressManager);
            case SECRET_SAUCE:
                return new JuiceWarriorSecondChanceSessionStateManager(slots, attributesManager, phraseManager, levelManager, powerUpsManager, progressManager);
            default:
                throw new IllegalStateException("Exception while handling equipment: " + currentEquipment);
        }
    }
}
