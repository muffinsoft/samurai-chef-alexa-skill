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
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.muffinsoft.alexa.skills.samuraichef.components.VoiceTranslator.translate;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.MISSION_PROGRESS_REMOVED_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.REPEAT_LAST_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.SELECT_MISSION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.SELECT_MISSION_TO_REMOVE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.FINISHED_MISSIONS;
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
    private Set<String> finishedMissions;

    public ResetConfirmationStateManager(Map<String, Slot> slots, AttributesManager attributesManager, ConfigContainer configContainer) {
        super(slots, attributesManager);
        this.phraseManager = configContainer.getPhraseManager();
        String foodSlotName = SlotName.AMAZON_FOOD.text;
        this.userFoodSlotReply = slots != null ? (slots.containsKey(foodSlotName) ? slots.get(foodSlotName).getValue() : null) : null;
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

        Object isMissionPresent = getSessionAttributes().get(CURRENT_MISSION);
        if (isMissionPresent != null) {
            this.currentMission = UserMission.valueOf(String.valueOf(isMissionPresent));
        }
        else {
            this.currentMission = null;
        }

        this.starCount = (int) getSessionAttributes().getOrDefault(STAR_COUNT, 0);

        List<String> finishedMissionArray = (List<String>) getSessionAttributes().getOrDefault(FINISHED_MISSIONS, new ArrayList<String>());
        this.finishedMissions = new HashSet<>(finishedMissionArray);

        logger.debug("Session attributes on the start of handling: " + this.getSessionAttributes().toString());
    }

    @Override
    protected void updatePersistentAttributes() {

        if (this.currentMission == null) {
            throw new IllegalStateException("Try remove mission progress without Current mission value");
        }

        if (this.finishedMissions.contains(this.currentMission.name())) {

            if (this.finishedMissions.size() > 1) {
                this.finishedMissions.remove(this.currentMission.name());
                getPersistentAttributes().put(FINISHED_MISSIONS, this.finishedMissions);
            }
            else {
                getPersistentAttributes().remove(FINISHED_MISSIONS);
            }
        }

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
                throw new IllegalStateException("Absent value " + value + " in persistent attributes");
            }

            int stripeCountAtMission = missionUserProgress.getStripeCount();

            logger.info("Will be removed " + stripeCountAtMission + " start from progress");

            int newStarCount = this.starCount - stripeCountAtMission;

            logger.info("New star count value should be " + newStarCount);

            getPersistentAttributes().put(STAR_COUNT, newStarCount);

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

        DialogItem.Builder builder = DialogItem.builder();

        if (this.currentMission == null) {
            builder.addResponse(translate(phraseManager.getValueByKey(SELECT_MISSION_TO_REMOVE_PHRASE)));
            getSessionAttributes().put(INTENT, Intents.GAME);
            return builder.build();
        }

        if (UserReplyComparator.compare(getUserReply(), UserReplies.NO)) {
            builder.addResponse(translate(phraseManager.getValueByKey(SELECT_MISSION_PHRASE)));
            getSessionAttributes().remove(CURRENT_MISSION);
            getSessionAttributes().remove(ACTIVITY_PROGRESS);
            getSessionAttributes().put(INTENT, Intents.GAME);
        }
        else if (UserReplyComparator.compare(getUserReply(), UserReplies.YES)) {
            builder.addResponse(translate(phraseManager.getValueByKey(MISSION_PROGRESS_REMOVED_PHRASE)));
            builder.addResponse(translate(phraseManager.getValueByKey(SELECT_MISSION_PHRASE)));
            getSessionAttributes().put(INTENT, Intents.GAME);
            getSessionAttributes().remove(ACTIVITY_PROGRESS);
            getSessionAttributes().remove(USER_PROGRESS);
            getSessionAttributes().remove(STATE_PHASE);
            getSessionAttributes().remove(STAR_COUNT);
            getSessionAttributes().remove(CURRENT_MISSION);
            getSessionAttributes().remove(FINISHED_MISSIONS);
            savePersistentAttributes();
        }
        else {
            builder.addResponse(translate(phraseManager.getValueByKey(REPEAT_LAST_PHRASE)));
        }

        return builder.build();
    }
}