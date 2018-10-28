package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivitiesManager;
import com.muffinsoft.alexa.skills.samuraichef.content.LevelManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.ProgressManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WRONG_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.WORD_BOARD_KARATE;

public class WordBoardKarateSessionStateManager extends BaseActivePhaseSamuraiChefSessionStateManager {

    private static final Logger logger = LoggerFactory.getLogger(WordBoardKarateSessionStateManager.class);

    public WordBoardKarateSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivitiesManager activitiesManager, LevelManager levelManager, PowerUpsManager powerUpsManager, ProgressManager progressManager) {
        super(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager, progressManager);
        this.currentActivity = WORD_BOARD_KARATE;
    }

    @Override
    protected DialogItem getActivePhaseDialog() {

        DialogItem dialog;

        if (Objects.equals(this.activityProgress.getCurrentIngredientReaction(), userReply)) {

            this.activityProgress.iterateSuccessCount();

            dialog = getSuccessDialog();
        }
        else {
            this.activityProgress.iterateMistakeCount();
            if (this.activityProgress.getMistakesCount() < level.getMaxMistakeCount()) {
                dialog = getFailureDialog(phraseManager.getValueByKey(WRONG_PHRASE));
            }
            else {
                dialog = getLoseRoundDialog();
            }
        }

        if (this.activityProgress.getSuccessCount() == level.getWonSuccessCount()) {
            dialog = getWinDialog();
        }

        return dialog;
    }
}
