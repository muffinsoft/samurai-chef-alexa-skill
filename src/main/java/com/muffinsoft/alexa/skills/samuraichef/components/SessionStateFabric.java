package com.muffinsoft.alexa.skills.samuraichef.components;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.StateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.FoodTasterCorrectAnswerStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.FoodTasterSecondChanceStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.FoodTasterStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.JuiceWarriorCorrectAnswerStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.JuiceWarriorSecondChanceStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.JuiceWarriorStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.SushiSliceCorrectAnswerStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.SushiSliceSecondChanceStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.SushiSliceStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.WordBoardKarateCorrectAnswerStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.WordBoardKarateSecondChanceStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.action.WordBoardKarateStateManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.PowerUps;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;

import java.util.Map;

public class SessionStateFabric {

    private final ConfigContainer configContainer;

    public SessionStateFabric(ConfigContainer configContainer) {
        this.configContainer = configContainer;
    }

    public StateManager createFromRequest(Activities currentActivity, PowerUps currentEquipment, Map<String, Slot> slots, AttributesManager attributesManager) {

        StateManager stateManager;

        switch (currentActivity) {
            case SUSHI_SLICE:
                stateManager = createSushiSliceStateManager(currentEquipment, slots, attributesManager);
                break;
            case JUICE_WARRIOR:
                stateManager = createJuiceWarriorStateManager(currentEquipment, slots, attributesManager);
                break;
            case WORD_BOARD_KARATE:
                stateManager = createWordBoardKarateStateManager(currentEquipment, slots, attributesManager);
                break;
            case FOOD_TASTER:
                stateManager = createFoodTasterStateManager(currentEquipment, slots, attributesManager);
                break;
            default:
                throw new IllegalStateException("Exception while handling activity: " + currentActivity);
        }

        return stateManager;
    }

    private FoodTasterStateManager createFoodTasterStateManager(PowerUps currentEquipment, Map<String, Slot> slots, AttributesManager attributesManager) {

        switch (currentEquipment) {
            case EMPTY_SLOT:
                return new FoodTasterStateManager(slots, attributesManager, configContainer);
            case CORRECT_ANSWER_SLOT:
                return new FoodTasterCorrectAnswerStateManager(slots, attributesManager, configContainer);
            case SECOND_CHANCE_SLOT:
                return new FoodTasterSecondChanceStateManager(slots, attributesManager, configContainer);
            default:
                throw new IllegalStateException("Exception while handling equipment: " + currentEquipment);
        }
    }

    private WordBoardKarateStateManager createWordBoardKarateStateManager(PowerUps currentEquipment, Map<String, Slot> slots, AttributesManager attributesManager) {

        switch (currentEquipment) {
            case EMPTY_SLOT:
                return new WordBoardKarateStateManager(slots, attributesManager, configContainer);
            case CORRECT_ANSWER_SLOT:
                return new WordBoardKarateCorrectAnswerStateManager(slots, attributesManager, configContainer);
            case SECOND_CHANCE_SLOT:
                return new WordBoardKarateSecondChanceStateManager(slots, attributesManager, configContainer);
            default:
                throw new IllegalStateException("Exception while handling equipment: " + currentEquipment);
        }
    }

    private SushiSliceStateManager createSushiSliceStateManager(PowerUps currentEquipment, Map<String, Slot> slots, AttributesManager attributesManager) {

        switch (currentEquipment) {
            case EMPTY_SLOT:
                return new SushiSliceStateManager(slots, attributesManager, configContainer);
            case CORRECT_ANSWER_SLOT:
                return new SushiSliceCorrectAnswerStateManager(slots, attributesManager, configContainer);
            case SECOND_CHANCE_SLOT:
                return new SushiSliceSecondChanceStateManager(slots, attributesManager, configContainer);
            default:
                throw new IllegalStateException("Exception while handling equipment: " + currentEquipment);
        }
    }

    private JuiceWarriorStateManager createJuiceWarriorStateManager(PowerUps currentEquipment, Map<String, Slot> slots, AttributesManager attributesManager) {

        switch (currentEquipment) {
            case EMPTY_SLOT:
                return new JuiceWarriorStateManager(slots, attributesManager, configContainer);
            case CORRECT_ANSWER_SLOT:
                return new JuiceWarriorCorrectAnswerStateManager(slots, attributesManager, configContainer);
            case SECOND_CHANCE_SLOT:
                return new JuiceWarriorSecondChanceStateManager(slots, attributesManager, configContainer);
            default:
                throw new IllegalStateException("Exception while handling equipment: " + currentEquipment);
        }
    }
}
