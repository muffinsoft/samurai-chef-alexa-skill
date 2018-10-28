package com.muffinsoft.alexa.skills.samuraichef.components;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.SessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.FoodTasterCorrectAnswerSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.FoodTasterDoubleActionSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.FoodTasterSecondChanceSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.FoodTasterSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.JuiceWarriorCorrectAnswerSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.JuiceWarriorMoreEarnSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.JuiceWarriorSecondChanceSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.JuiceWarriorSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.SushiSliceCorrectAnswerSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.SushiSliceMoreEarnSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.SushiSliceSecondChanceSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.SushiSliceSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.WordBoardKarateCorrectAnswerSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.WordBoardKarateSecondChanceSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.activities.WordBoardKarateSessionStateManager;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivitiesManager;
import com.muffinsoft.alexa.skills.samuraichef.content.LevelManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.ProgressManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.Equipments;

import java.util.Map;

public class SessionStateFabric {

    private final PhraseManager phraseManager;
    private final ActivitiesManager activitiesManager;
    private final LevelManager levelManager;
    private final PowerUpsManager powerUpsManager;
    private final ProgressManager progressManager;

    public SessionStateFabric(PhraseManager phraseManager, ActivitiesManager activitiesManager, LevelManager levelManager, PowerUpsManager powerUpsManager, ProgressManager progressManager) {
        this.phraseManager = phraseManager;
        this.activitiesManager = activitiesManager;
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
                return new FoodTasterSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, progressManager);
            case SUPER_SPATULE:
            case SECRET_SAUCE:
            case CHEF_HAT:
                return new FoodTasterCorrectAnswerSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, progressManager);
            case KARATE_GI:
            case HACHIMAKI:
                return new FoodTasterSecondChanceSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, progressManager);
            case SUMO_MAWASHI:
                return new FoodTasterDoubleActionSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, progressManager);
            default:
                throw new IllegalStateException("Exception while handling equipment: " + currentEquipment);
        }
    }

    private WordBoardKarateSessionStateManager createWordBoardKarateSessionStateManager(Equipments currentEquipment, Map<String, Slot> slots, AttributesManager attributesManager) {

        switch (currentEquipment) {
            case EMPTY_SLOT:
                return new WordBoardKarateSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, progressManager);
            case SUPER_SPATULE:
            case SECRET_SAUCE:
            case CHEF_HAT:
                return new WordBoardKarateCorrectAnswerSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, progressManager);
            case KARATE_GI:
            case HACHIMAKI:
                return new WordBoardKarateSecondChanceSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, progressManager);
            default:
                throw new IllegalStateException("Exception while handling equipment: " + currentEquipment);
        }
    }

    private SushiSliceSessionStateManager createSushiSliceSessionStateManager(Equipments currentEquipment, Map<String, Slot> slots, AttributesManager attributesManager) {

        switch (currentEquipment) {
            case EMPTY_SLOT:
                return new SushiSliceSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, progressManager);
            case SUSHI_BLADE:
            case CUISINE_KATANA:
                return new SushiSliceMoreEarnSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, progressManager);
            case SUPER_SPATULE:
            case SECRET_SAUCE:
            case CHEF_HAT:
                return new SushiSliceCorrectAnswerSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, progressManager);
            case KARATE_GI:
            case HACHIMAKI:
                return new SushiSliceSecondChanceSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, progressManager);
            default:
                throw new IllegalStateException("Exception while handling equipment: " + currentEquipment);
        }
    }

    private JuiceWarriorSessionStateManager createJuiceWarriorSessionStateManager(Equipments currentEquipment, Map<String, Slot> slots, AttributesManager attributesManager) {

        switch (currentEquipment) {
            case EMPTY_SLOT:
                return new JuiceWarriorSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, progressManager);
            case SUSHI_BLADE:
            case CUISINE_KATANA:
                return new JuiceWarriorMoreEarnSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, progressManager);
            case SUPER_SPATULE:
            case SECRET_SAUCE:
            case CHEF_HAT:
                return new JuiceWarriorCorrectAnswerSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, progressManager);
            case KARATE_GI:
            case HACHIMAKI:
                return new JuiceWarriorSecondChanceSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, progressManager);
            default:
                throw new IllegalStateException("Exception while handling equipment: " + currentEquipment);
        }
    }
}
