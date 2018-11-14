package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseSessionStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.Speech;
import com.muffinsoft.alexa.skills.samuraichef.components.UserReplyComparator;
import com.muffinsoft.alexa.skills.samuraichef.content.AliasManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_PROGRESS;

public class SelectLevelStateManager extends BaseSessionStateManager {

    private static final Logger logger = LogManager.getLogger(SelectLevelStateManager.class);

    private final AliasManager aliasManager;
    private UserProgress userProgress;

    public SelectLevelStateManager(Map<String, Slot> slots, AttributesManager attributesManager, AliasManager aliasManager) {
        super(slots, attributesManager);
        this.aliasManager = aliasManager;
    }

    @Override
    protected void initializeSessionAttributes() {
        this.sessionAttributes = new HashMap<>();
    }

    @Override
    protected void populateActivityVariables() {
        LinkedHashMap rawUserProgress = (LinkedHashMap) sessionAttributes.get(USER_PROGRESS);
        userProgress = rawUserProgress != null ? mapper.convertValue(rawUserProgress, UserProgress.class) : new UserProgress(true);
    }

    @Override
    protected void updateSessionAttributes() {

    }

    @Override
    protected void updatePersistentAttributes() {

    }

    @Override
    public DialogItem nextResponse() {

        logger.debug("Starting handling user reply '" + this.userReply + "' ...");

        String dialog;
        if (UserReplyComparator.compare(userReply, UserReplies.YES)) {
            dialog = "Please, select the level";
        }
        else if (UserReplyComparator.compare(userReply, UserReplies.LOW)) {
            dialog = checkIfMissionAvailable(UserMission.LOW_MISSION);
        }
        else if (UserReplyComparator.compare(userReply, UserReplies.MEDIUM)) {
            dialog = checkIfMissionAvailable(UserMission.MEDIUM_MISSION);
        }
        else if (UserReplyComparator.compare(userReply, UserReplies.HIGH)) {
            dialog = checkIfMissionAvailable(UserMission.HIGH_MISSION);
        }
        else {
            dialog = "I don't understand your choice, Please, select one of three available";
        }

        String cardTitle = null;
        if (this.sessionAttributes.containsKey(CURRENT_MISSION)) {
            cardTitle = aliasManager.getValueByKey(String.valueOf(this.sessionAttributes.get(CURRENT_MISSION)));
        }

        DialogItem.Builder builder = DialogItem.builder().withResponse(Speech.ofText(dialog)).withSlotName(actionSlotName);

        if (cardTitle != null) {
            builder.withCardTitle(cardTitle);
        }

        return builder.build();
    }

    private String checkIfMissionAvailable(UserMission mission) {

        Set<String> finishedMissions = userProgress.getFinishedMissions();
        if (finishedMissions.contains(mission.name())) {
            return "You have already complete this mission";
        }

        this.sessionAttributes.put(CURRENT_MISSION, mission);
        logger.debug("Session will be redirected to " + mission.name());

        return "Are you ready to start " + aliasManager.getValueByKey(mission.name()) + "?";

    }
}
