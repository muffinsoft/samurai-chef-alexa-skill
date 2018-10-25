package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivitiesManager;
import com.muffinsoft.alexa.skills.samuraichef.content.LevelManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.TOO_LONG_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WRONG_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.QUESTION_TIME;
import static com.muffinsoft.alexa.skills.samuraichef.enums.Activities.JUICE_WARRIOR;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.WIN;

public class JuiceWarriorSessionStateManager extends BaseSamuraiChefSessionStateManager {

    private static final Logger logger = LoggerFactory.getLogger(JuiceWarriorSessionStateManager.class);

    protected Long questionTime;

    public JuiceWarriorSessionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, PhraseManager phraseManager, ActivitiesManager activitiesManager, LevelManager levelManager, PowerUpsManager powerUpsManager) {
        super(slots, attributesManager, phraseManager, activitiesManager, levelManager, powerUpsManager);
        this.currentActivity = JUICE_WARRIOR;
    }

    @Override
    protected DialogItem getActivePhaseDialog() {

        DialogItem dialog;

        long answerTime = System.currentTimeMillis();

        if (Objects.equals(currentIngredientReaction, userReply)) {

            long answerLimit = level.getTimeLimitPhaseOneInMillis();

            if (questionTime == null || answerTime - questionTime < answerLimit) {

                this.successCount++;

                dialog = getSuccessDialog();
            }
            else {
                dialog = getFailureDialog(phraseManager.getValueByKey(TOO_LONG_PHRASE));
            }
        }
        else {
            this.mistakesCount++;
            if (this.mistakesCount < level.getMaxMistakeCount()) {
                dialog = getFailureDialog(phraseManager.getValueByKey(WRONG_PHRASE));
            }
            else {
                dialog = getLoseRoundDialog();
            }
        }

        if (this.successCount == level.getWonSuccessCount()) {
            this.statePhase = WIN;
            dialog = getWinDialog();
        }

        return dialog;
    }

    @Override
    protected void resetRoundProgress() {
        super.resetRoundProgress();
        this.questionTime = null;
    }

    @Override
    protected void populateActivityVariables() {
        super.populateActivityVariables();
        questionTime = (Long) sessionAttributes.get(QUESTION_TIME);
    }

    @Override
    protected void updateSessionAttributes() {
        super.updateSessionAttributes();
        sessionAttributes.put(QUESTION_TIME, System.currentTimeMillis());
    }
}
