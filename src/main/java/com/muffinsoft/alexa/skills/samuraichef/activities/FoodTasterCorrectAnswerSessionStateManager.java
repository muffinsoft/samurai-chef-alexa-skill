package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivityManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.MissionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.USED_EQUIPMENT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WRONG_PHRASE;

public class FoodTasterCorrectAnswerSessionStateManager extends FoodTasterSessionStateManager {

    private static final Logger logger = LoggerFactory.getLogger(FoodTasterCorrectAnswerSessionStateManager.class);

    public FoodTasterCorrectAnswerSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivityManager activityManager, PowerUpsManager powerUpsManager, MissionManager missionManager) {
        super(slots, attributesManager, phraseManager, activityManager, powerUpsManager, missionManager);
    }

    @Override
    protected DialogItem handleMistake() {

        if (this.userProgress.isPowerUpEquipped()) {

            this.dialogPrefix = phraseManager.getValueByKey(USED_EQUIPMENT_PHRASE);
            this.userProgress.removePowerUp();
            return getSuccessDialog();
        }
        else {
            this.activityProgress.iterateMistakeCount();
            if (this.activityProgress.getMistakesCount() < stripe.getMaxMistakeCount()) {
                return getFailureDialog(phraseManager.getValueByKey(WRONG_PHRASE));
            }
            else {
                return getLoseRoundDialog();
            }
        }
    }
}
