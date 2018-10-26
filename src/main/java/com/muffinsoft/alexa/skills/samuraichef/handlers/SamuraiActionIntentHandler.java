package com.muffinsoft.alexa.skills.samuraichef.handlers;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Request;
import com.amazon.ask.model.Slot;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muffinsoft.alexa.sdk.activities.SessionStateManager;
import com.muffinsoft.alexa.sdk.handlers.ActionIntentHandler;
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
import com.muffinsoft.alexa.skills.samuraichef.content.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.content.LevelManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.RewardManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.Equipments;
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.amazon.ask.request.Predicates.intentName;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_PROGRESS;

public class SamuraiActionIntentHandler extends ActionIntentHandler {

    private static final Logger logger = LoggerFactory.getLogger(SamuraiActionIntentHandler.class);

    private final PhraseManager phraseManager;
    private final CardManager cardManager;
    private final ActivitiesManager activitiesManager;
    private final LevelManager levelManager;
    private final PowerUpsManager powerUpsManager;
    private final RewardManager rewardManager;

    public SamuraiActionIntentHandler(PhraseManager phraseManager, ActivitiesManager activitiesManager, CardManager cardManager, LevelManager levelManager, PowerUpsManager powerUpsManager, RewardManager rewardManager) {
        this.phraseManager = phraseManager;
        this.activitiesManager = activitiesManager;
        this.cardManager = cardManager;
        this.levelManager = levelManager;
        this.powerUpsManager = powerUpsManager;
        this.rewardManager = rewardManager;
    }

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("SamuraiActionIntent"));
    }

    @Override
    public SessionStateManager nextTurn(HandlerInput input) {

        Request request = input.getRequestEnvelope().getRequest();
        IntentRequest intentRequest = (IntentRequest) request;

        Map<String, Slot> slots = intentRequest.getIntent().getSlots();

        Activities currentActivity = getCurrentActivity(input);

        UserProgress currentUserProgress = getCurrentUserProgress(input);

        Equipments currentEquipment = Equipments.EMPTY_SLOT;

        if (currentUserProgress.isPowerUpEquipped()) {
            currentEquipment = Equipments.valueOf(currentUserProgress.getEquippedPowerUp());
        }

        SessionStateManager stateManager;

        logger.info("Going to handle activity " + currentActivity + " with equipment " + currentEquipment);

        switch (currentActivity) {
//            case NAME_HANDLER:
//                stateManager = new NameHandlerSessionStateManager(slots, input.getAttributesManager(), phraseManager, activitiesManager, levelManager, powerUpsManager);
//                break;
            case SUSHI_SLICE:
                stateManager = createSushiSliceSessionStateManager(currentEquipment, slots, input.getAttributesManager());
                break;
            case JUICE_WARRIOR:
                stateManager = createJuiceWarriorSessionStateManager(currentEquipment, slots, input.getAttributesManager());
                break;
            case WORD_BOARD_KARATE:
                stateManager = createWordBoardKarateSessionStateManager(currentEquipment, slots, input.getAttributesManager());
                break;
            case FOOD_TASTER:
                stateManager = createFoodTasterSessionStateManager(currentEquipment, slots, input.getAttributesManager());
                break;
            default:
                throw new IllegalStateException("Exception while handling activity: " + currentActivity);
        }

        return stateManager;
    }

    private FoodTasterSessionStateManager createFoodTasterSessionStateManager(Equipments currentEquipment, Map<String, Slot> slots, AttributesManager attributesManager) {

        switch (currentEquipment) {
            case EMPTY_SLOT:
                return new FoodTasterSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, rewardManager);
            case SUPER_SPATULE:
            case SECRET_SAUCE:
            case CHEF_HAT:
                return new FoodTasterCorrectAnswerSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, rewardManager);
            case KARATE_GI:
            case HACHIMAKI:
                return new FoodTasterSecondChanceSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, rewardManager);
            case SUMO_MAWASHI:
                return new FoodTasterDoubleActionSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, rewardManager);
            default:
                throw new IllegalStateException("Exception while handling equipment: " + currentEquipment);
        }
    }

    private WordBoardKarateSessionStateManager createWordBoardKarateSessionStateManager(Equipments currentEquipment, Map<String, Slot> slots, AttributesManager attributesManager) {

        switch (currentEquipment) {
            case EMPTY_SLOT:
                return new WordBoardKarateSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, rewardManager);
            case SUPER_SPATULE:
            case SECRET_SAUCE:
            case CHEF_HAT:
                return new WordBoardKarateCorrectAnswerSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, rewardManager);
            case KARATE_GI:
            case HACHIMAKI:
                return new WordBoardKarateSecondChanceSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, rewardManager);
            default:
                throw new IllegalStateException("Exception while handling equipment: " + currentEquipment);
        }
    }

    private SushiSliceSessionStateManager createSushiSliceSessionStateManager(Equipments currentEquipment, Map<String, Slot> slots, AttributesManager attributesManager) {

        switch (currentEquipment) {
            case EMPTY_SLOT:
                return new SushiSliceSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, rewardManager);
            case SUSHI_BLADE:
            case CUISINE_KATANA:
                return new SushiSliceMoreEarnSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, rewardManager);
            case SUPER_SPATULE:
            case SECRET_SAUCE:
            case CHEF_HAT:
                return new SushiSliceCorrectAnswerSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, rewardManager);
            case KARATE_GI:
            case HACHIMAKI:
                return new SushiSliceSecondChanceSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, rewardManager);
            default:
                throw new IllegalStateException("Exception while handling equipment: " + currentEquipment);
        }
    }

    private JuiceWarriorSessionStateManager createJuiceWarriorSessionStateManager(Equipments currentEquipment, Map<String, Slot> slots, AttributesManager attributesManager) {

        switch (currentEquipment) {
            case EMPTY_SLOT:
                return new JuiceWarriorSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, rewardManager);
            case SUSHI_BLADE:
            case CUISINE_KATANA:
                return new JuiceWarriorMoreEarnSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, rewardManager);
            case SUPER_SPATULE:
            case SECRET_SAUCE:
            case CHEF_HAT:
                return new JuiceWarriorCorrectAnswerSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, rewardManager);
            case KARATE_GI:
            case HACHIMAKI:
                return new JuiceWarriorSecondChanceSessionStateManager(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, rewardManager);
            default:
                throw new IllegalStateException("Exception while handling equipment: " + currentEquipment);
        }
    }

    private UserProgress getCurrentUserProgress(HandlerInput input) {

        LinkedHashMap rawUserProgress = (LinkedHashMap) input.getAttributesManager().getSessionAttributes().get(USER_PROGRESS);
        return rawUserProgress != null ? new ObjectMapper().convertValue(rawUserProgress, UserProgress.class) : new UserProgress();
    }

    private Activities getCurrentActivity(HandlerInput input) {
        Activities firstActivity = activitiesManager.getFirstActivity();
        String rawActivity = String.valueOf(input.getAttributesManager().getSessionAttributes().getOrDefault(ACTIVITY, firstActivity.name()));
        return Activities.valueOf(rawActivity);
    }

    @Override
    public String getPhrase() {
        return null;
    }

    @Override
    public String getSimpleCard() {
        return cardManager.getValueByKey("welcome");
    }
}
