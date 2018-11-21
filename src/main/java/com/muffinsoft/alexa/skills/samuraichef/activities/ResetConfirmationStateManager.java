package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.SlotName;
import com.muffinsoft.alexa.skills.samuraichef.components.UserReplyComparator;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Intents;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseSettings;
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.components.VoiceTranslator.translate;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.MISSION_PROGRESS_REMOVED_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.REPEAT_LAST_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.SELECT_MISSION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STAR_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_HIGH_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_LOW_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_MID_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_PROGRESS;

public class ResetConfirmationStateManager extends BaseStateManager {

    private static final Logger logger = LogManager.getLogger(CancelStateManager.class);

    private final String userFoodSlotReply;

    private final PhraseManager phraseManager;
    private UserMission currentMission;
    private int starCount;

    public ResetConfirmationStateManager(Map<String, Slot> slots, AttributesManager attributesManager, ConfigContainer configContainer) {
        super(slots, attributesManager);
        this.phraseManager = configContainer.getPhraseManager();
        String foodSlotName = SlotName.AMAZON_FOOD.text;
        this.userFoodSlotReply = slots.containsKey(foodSlotName) ? slots.get(foodSlotName).getValue() : null;
    }

    @Override
    public String getUserReply() {
        String userReply = super.getUserReply();
        if (userReply != null && !userReply.isEmpty()) {
            return userReply;
        }
        else {
            return this.userFoodSlotReply;
        }
    }

    @Override
    protected void populateActivityVariables() {

        this.currentMission = UserMission.valueOf(String.valueOf(getSessionAttributes().get(CURRENT_MISSION)));

        this.starCount = (int) getSessionAttributes().getOrDefault(STAR_COUNT, 0);

        logger.debug("Session attributes on the start of handling: " + this.getSessionAttributes().toString());
    }

    @Override
    protected void updatePersistentAttributes() {
        switch (this.currentMission) {
            case LOW_MISSION:
                removeMissionProgress(USER_LOW_PROGRESS_DB);
                break;
            case MEDIUM_MISSION:
                removeMissionProgress(USER_MID_PROGRESS_DB);
                break;
            case HIGH_MISSION:
                removeMissionProgress(USER_HIGH_PROGRESS_DB);
                break;
        }
        logger.debug("Persistent attributes on the end of handling: " + this.getPersistentAttributes().toString());
    }

    private void removeMissionProgress(String value) {
        try {

            UserProgress missionUserProgress;

            if (getPersistentAttributes().containsKey(value)) {
                String jsonInString = String.valueOf(getPersistentAttributes().get(value));
                LinkedHashMap rawUserProgress = mapper.readValue(jsonInString, LinkedHashMap.class);
                missionUserProgress = mapper.convertValue(rawUserProgress, UserProgress.class);
            }
            else {
                return;
            }

            this.starCount = this.starCount - missionUserProgress.getStripeCount();
            getPersistentAttributes().put(STAR_COUNT, this.starCount);
            getSessionAttributes().put(STAR_COUNT, this.starCount);

            missionUserProgress.resetMissionProgress();
            getPersistentAttributes().put(value, mapper.writeValueAsString(missionUserProgress));
        }
        catch (IOException e) {
            throw new IllegalStateException("Exception while cleaning Mission Progress in Persistent Attributes", e);
        }
    }

    @Override
    public DialogItem nextResponse() {
        logger.debug("Available session attributes: " + getSessionAttributes());

        PhraseSettings dialog;

        if (UserReplyComparator.compare(getUserReply(), UserReplies.NO)) {
            dialog = phraseManager.getValueByKey(SELECT_MISSION_PHRASE);
            getSessionAttributes().remove(CURRENT_MISSION);
            getSessionAttributes().remove(ACTIVITY_PROGRESS);
            getSessionAttributes().put(INTENT, Intents.GAME);
        }
        else if (UserReplyComparator.compare(getUserReply(), UserReplies.YES)) {
            dialog = phraseManager.getValueByKey(MISSION_PROGRESS_REMOVED_PHRASE);
            getSessionAttributes().put(INTENT, Intents.GAME);
            getSessionAttributes().remove(ACTIVITY_PROGRESS);
            getSessionAttributes().remove(USER_PROGRESS);
            getSessionAttributes().remove(STATE_PHASE);
            getSessionAttributes().remove(STAR_COUNT);
            savePersistentAttributes();
        }
        else {
            dialog = phraseManager.getValueByKey(REPEAT_LAST_PHRASE);
        }

        DialogItem.Builder builder = DialogItem.builder().addResponse(translate(dialog));

        return builder.build();
    }
}