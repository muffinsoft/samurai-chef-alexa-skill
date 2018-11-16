package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.Speech;
import com.muffinsoft.alexa.skills.samuraichef.components.UserReplyComparator;
import com.muffinsoft.alexa.skills.samuraichef.content.AliasManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.MISSION_ALREADY_COMPLETE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.READY_TO_START_MISSION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.SELECT_MISSION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.SELECT_MISSION_UNKNOWN_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_PROGRESS;

public class SelectLevelStateManager extends BaseStateManager {

    private static final Logger logger = LogManager.getLogger(SelectLevelStateManager.class);

    private final AliasManager aliasManager;
    private final PhraseManager phraseManager;
    private UserProgress userProgress;

    public SelectLevelStateManager(Map<String, Slot> slots, AttributesManager attributesManager, ConfigContainer configContainer) {
        super(slots, attributesManager);
        this.aliasManager = configContainer.getAliasManager();
        this.phraseManager = configContainer.getPhraseManager();
    }

    @Override
    protected void populateActivityVariables() {
        LinkedHashMap rawUserProgress = (LinkedHashMap) getSessionAttributes().get(USER_PROGRESS);
        userProgress = rawUserProgress != null ? mapper.convertValue(rawUserProgress, UserProgress.class) : new UserProgress(true);
    }

    @Override
    public DialogItem nextResponse() {

        logger.debug("Starting handling user reply '" + this.getUserReply() + "' ...");

        String dialog;
        if (UserReplyComparator.compare(getUserReply(), UserReplies.YES)) {
            dialog = phraseManager.getValueByKey(SELECT_MISSION_PHRASE);
        }
        else if (UserReplyComparator.compare(getUserReply(), UserReplies.LOW)) {
            dialog = checkIfMissionAvailable(UserMission.LOW_MISSION);
        }
        else if (UserReplyComparator.compare(getUserReply(), UserReplies.MEDIUM)) {
            dialog = checkIfMissionAvailable(UserMission.MEDIUM_MISSION);
        }
        else if (UserReplyComparator.compare(getUserReply(), UserReplies.HIGH)) {
            dialog = checkIfMissionAvailable(UserMission.HIGH_MISSION);
        }
        else {
            dialog = phraseManager.getValueByKey(SELECT_MISSION_UNKNOWN_PHRASE);
        }

        String cardTitle = null;
        if (this.getSessionAttributes().containsKey(CURRENT_MISSION)) {
            cardTitle = aliasManager.getValueByKey(String.valueOf(this.getSessionAttributes().get(CURRENT_MISSION)));
        }

        DialogItem.Builder builder = DialogItem.builder().withResponse(Speech.ofText(dialog));

        if (cardTitle != null) {
            builder.withCardTitle(cardTitle);
        }

        return builder.build();
    }

    private String checkIfMissionAvailable(UserMission mission) {

        Set<String> finishedMissions = userProgress.getFinishedMissions();
        if (finishedMissions.contains(mission.name())) {
            return phraseManager.getValueByKey(MISSION_ALREADY_COMPLETE_PHRASE);
        }

        this.getSessionAttributes().remove(ACTIVITY);
        this.getSessionAttributes().remove(ACTIVITY_PROGRESS);
        this.getSessionAttributes().remove(USER_PROGRESS);

        this.getSessionAttributes().put(CURRENT_MISSION, mission);
        logger.info("user will be redirected to " + mission.name());

        return phraseManager.getValueByKey(READY_TO_START_MISSION_PHRASE) + " " + aliasManager.getValueByKey(mission.name()) + "?";

    }
}
