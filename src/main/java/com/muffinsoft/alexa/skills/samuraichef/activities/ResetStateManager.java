package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.components.UserReplyComparator;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.RegularPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Intents;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;
import com.muffinsoft.alexa.skills.samuraichef.models.ActivityProgress;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;
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
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.READY_TO_PLAY_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.RETURN_TO_GAME_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.SELECT_MISSION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.FINISHED_MISSIONS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.QUESTION_TIME;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STAR_COUNT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.STATE_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_HIGH_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_LOW_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_MID_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.MISSION_INTRO;

public class ResetStateManager extends BaseStateManager {

    private static final Logger logger = LogManager.getLogger(CancelStateManager.class);

    private final RegularPhraseManager regularPhraseManager;

    private StatePhase statePhase;
    private ActivityProgress activityProgress;

    private UserMission currentMission;
    private int starCount;
    private Set<String> finishedMissions;

    public ResetStateManager(Map<String, Slot> slots, AttributesManager attributesManager, SettingsDependencyContainer settingsDependencyContainer, PhraseDependencyContainer phraseDependencyContainer) {
        super(slots, attributesManager);
        this.regularPhraseManager = phraseDependencyContainer.getRegularPhraseManager();
    }

    @Override
    protected void populateActivityVariables() {
        statePhase = StatePhase.valueOf(String.valueOf(getSessionAttributes().getOrDefault(STATE_PHASE, MISSION_INTRO)));
        LinkedHashMap rawActivityProgress = (LinkedHashMap) getSessionAttributes().get(ACTIVITY_PROGRESS);
        activityProgress = rawActivityProgress != null ? mapper.convertValue(rawActivityProgress, ActivityProgress.class) : new ActivityProgress();

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
                return;
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

        if (UserReplyComparator.compare(getUserReply(), UserReplies.NEW)) {
            builder.addResponse(translate(regularPhraseManager.getValueByKey(SELECT_MISSION_PHRASE)));
            getSessionAttributes().remove(CURRENT_MISSION);
            getSessionAttributes().remove(ACTIVITY_PROGRESS);
            getSessionAttributes().put(INTENT, Intents.GAME);
        }
        else if (UserReplyComparator.compare(getUserReply(), UserReplies.RESET)) {
            builder.addResponse(translate(regularPhraseManager.getValueByKey(READY_TO_PLAY_PHRASE)));
            getSessionAttributes().remove(ACTIVITY_PROGRESS);
            getSessionAttributes().remove(STATE_PHASE);
            getSessionAttributes().remove(STAR_COUNT);
            getSessionAttributes().remove(FINISHED_MISSIONS);
            getSessionAttributes().put(INTENT, Intents.GAME);
            getSessionAttributes().put(STATE_PHASE, MISSION_INTRO);
            savePersistentAttributes();
        }
        else {
            getSessionAttributes().put(INTENT, Intents.GAME);
            getSessionAttributes().put(STATE_PHASE, StatePhase.STRIPE_INTRO);
            builder.addResponse(translate(regularPhraseManager.getValueByKey(RETURN_TO_GAME_PHRASE)));
            if (activityProgress != null && activityProgress.getPreviousIngredient() != null) {
                builder.addResponse(translate(activityProgress.getPreviousIngredient()));
            }
            else {
                builder.addResponse(translate(regularPhraseManager.getValueByKey(READY_TO_PLAY_PHRASE)));
            }
            getSessionAttributes().put(QUESTION_TIME, System.currentTimeMillis());
        }

        return builder.build();
    }
}
