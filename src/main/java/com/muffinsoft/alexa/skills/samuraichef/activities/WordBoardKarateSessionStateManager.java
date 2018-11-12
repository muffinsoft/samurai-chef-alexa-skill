package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivityManager;
import com.muffinsoft.alexa.skills.samuraichef.content.MissionManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WRONG_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.WORD_BOARD_KARATE;

public class WordBoardKarateSessionStateManager extends BaseActivePhaseSamuraiChefSessionStateManager {

    private static final Logger logger = LoggerFactory.getLogger(WordBoardKarateSessionStateManager.class);

    public WordBoardKarateSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivityManager activityManager, PowerUpsManager powerUpsManager, MissionManager missionManager) {
        super(slots, attributesManager, phraseManager, activityManager, powerUpsManager, missionManager);
        this.currentActivity = WORD_BOARD_KARATE;
    }

    @Override
    protected DialogItem handleActivePhaseState() {

        DialogItem dialog;

        if (Objects.equals(this.activityProgress.getCurrentIngredientReaction(), userReply)) {

            this.activityProgress.iterateSuccessCount();

            dialog = getSuccessDialog();
        }
        else {
            this.activityProgress.iterateMistakeCount();
            if (this.activityProgress.getMistakesCount() < stripe.getMaxMistakeCount()) {
                dialog = getFailureDialog(phraseManager.getValueByKey(WRONG_PHRASE));
            }
            else {
                dialog = getLoseRoundDialog();
            }
        }

        if (this.activityProgress.getSuccessCount() == stripe.getWonSuccessCount()) {
            dialog = getWinDialog();
        }

        return dialog;
    }
}
