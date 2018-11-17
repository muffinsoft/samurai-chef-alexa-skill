package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.Speech;
import com.muffinsoft.alexa.skills.samuraichef.components.UserReplyComparator;
import com.muffinsoft.alexa.skills.samuraichef.content.MissionManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.Intents;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.REPEAT_LAST_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.SELECT_MISSION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WANT_EXIT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_HIGH_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_LOW_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_MID_PROGRESS_DB;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_PROGRESS;

public class CancelStateManager extends BaseStateManager {

    private static final Logger logger = LogManager.getLogger(CancelStateManager.class);
    private final PhraseManager phraseManager;
    private final MissionManager missionManager;
    private UserMission currentMission;
    private UserProgress userProgress;

    public CancelStateManager(Map<String, Slot> inputSlots, AttributesManager attributesManager, ConfigContainer configContainer) {
        super(inputSlots, attributesManager);
        this.phraseManager = configContainer.getPhraseManager();
        this.missionManager = configContainer.getMissionManager();
    }

    @Override
    protected void populateActivityVariables() {

        String stringifyMission = String.valueOf(getSessionAttributes().get(CURRENT_MISSION));
        if (stringifyMission != null && !stringifyMission.isEmpty() && !stringifyMission.equals("null")) {
            this.currentMission = UserMission.valueOf(stringifyMission);
        }
        else {
            this.currentMission = null;
        }

        LinkedHashMap rawUserProgress = (LinkedHashMap) getSessionAttributes().get(USER_PROGRESS);
        this.userProgress = rawUserProgress != null ? mapper.convertValue(rawUserProgress, UserProgress.class) : new UserProgress(this.currentMission, true);

        logger.debug("Session attributes on the start of handling: " + this.getSessionAttributes().toString());
    }

    @Override
    protected void updatePersistentAttributes() {
        switch (this.currentMission) {
            case LOW_MISSION:
                saveUserProgressForMission(USER_LOW_PROGRESS_DB);
                break;
            case MEDIUM_MISSION:
                saveUserProgressForMission(USER_MID_PROGRESS_DB);
                break;
            case HIGH_MISSION:
                saveUserProgressForMission(USER_HIGH_PROGRESS_DB);
                break;
        }
        logger.debug("Persistent attributes on the end of handling: " + this.getPersistentAttributes().toString());
    }

    private void saveUserProgressForMission(String value) {
        try {
            if (userProgress.getCurrentActivity().equals(userProgress.getPreviousActivity())) {
                Activities nextActivityForMission = missionManager.getNextActivityForMission(this.currentMission, userProgress.getFinishedMissions());
                userProgress.setCurrentActivity(nextActivityForMission.name());
            }
            getPersistentAttributes().put(value, mapper.writeValueAsString(userProgress));
        }
        catch (IOException e) {
            throw new IllegalStateException("Exception while cleaning Mission Progress in Persistent Attributes", e);
        }
    }

    @Override
    public DialogItem nextResponse() {

        logger.debug("Available session attributes: " + getSessionAttributes());

        String dialog;

        if (UserReplyComparator.compare(getUserReply(), UserReplies.YES)) {
            savePersistentAttributes();
            dialog = phraseManager.getValueByKey(SELECT_MISSION_PHRASE);
            getSessionAttributes().remove(CURRENT_MISSION);
            getSessionAttributes().remove(ACTIVITY_PROGRESS);
            getSessionAttributes().remove(USER_PROGRESS);
            getSessionAttributes().put(INTENT, Intents.GAME);
        }
        else if (UserReplyComparator.compare(getUserReply(), UserReplies.NO)) {
            savePersistentAttributes();
            dialog = phraseManager.getValueByKey(WANT_EXIT_PHRASE);
            getSessionAttributes().put(INTENT, Intents.EXIT);
        }
        else {
            dialog = phraseManager.getValueByKey(REPEAT_LAST_PHRASE);
        }

        DialogItem.Builder builder = DialogItem.builder().addResponse(Speech.ofText(dialog));

        return builder.build();
    }
}
