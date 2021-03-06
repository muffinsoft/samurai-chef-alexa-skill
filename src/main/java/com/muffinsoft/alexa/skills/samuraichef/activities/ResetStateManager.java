package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.enums.IntentType;
import com.muffinsoft.alexa.sdk.enums.StateType;
import com.muffinsoft.alexa.sdk.model.BasePhraseContainer;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.SlotName;
import com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.ActivityPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.MissionPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.RegularPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.AplManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;
import com.muffinsoft.alexa.skills.samuraichef.models.ActivityProgress;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SpeechSettings;
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

import static com.muffinsoft.alexa.sdk.enums.StateType.ACTIVITY_INTRO;
import static com.muffinsoft.alexa.sdk.enums.StateType.DEMO;
import static com.muffinsoft.alexa.sdk.enums.StateType.MISSION_INTRO;
import static com.muffinsoft.alexa.sdk.enums.StateType.READY;
import static com.muffinsoft.alexa.sdk.enums.StateType.SUBMISSION_INTRO;
import static com.muffinsoft.alexa.skills.samuraichef.components.UserReplyComparator.compare;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.READY_TO_PLAY_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.READY_TO_START_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.REPEAT_LAST_PHRASE;
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
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_REPLY_BREAKPOINT;

@SuppressWarnings("Duplicates")
public class ResetStateManager extends BaseStateManager {

    private static final Logger logger = LogManager.getLogger(ResetStateManager.class);

    private final RegularPhraseManager regularPhraseManager;
    private final MissionPhraseManager missionPhraseManager;
    private final ActivityPhraseManager activityPhraseManager;
    private final CardManager cardManager;
    private final AplManager aplManager;
    private Activities currentActivity;
    private UserProgress userProgress;
    private ActivityProgress activityProgress;
    private UserMission currentMission;
    private int starCount;
    private StateType statePhase;
    private Set<String> finishedMissions;
    private Integer userReplyBreakpointPosition;

    public ResetStateManager(Map<String, Slot> slots, AttributesManager attributesManager, SettingsDependencyContainer settingsDependencyContainer, PhraseDependencyContainer phraseDependencyContainer) {
        super(slots, attributesManager, settingsDependencyContainer.getDialogTranslator());
        this.regularPhraseManager = phraseDependencyContainer.getRegularPhraseManager();
        this.missionPhraseManager = phraseDependencyContainer.getMissionPhraseManager();
        this.activityPhraseManager = phraseDependencyContainer.getActivityPhraseManager();
        this.cardManager = settingsDependencyContainer.getCardManager();
        this.aplManager = settingsDependencyContainer.getAplManager();
    }

    @Override
    protected void populateActivityVariables() {
        StateType statePhase = StateType.valueOf(String.valueOf(getSessionAttributes().getOrDefault(STATE_PHASE, MISSION_INTRO)));
        LinkedHashMap rawActivityProgress = (LinkedHashMap) getSessionAttributes().get(ACTIVITY_PROGRESS);
        activityProgress = rawActivityProgress != null ? mapper.convertValue(rawActivityProgress, ActivityProgress.class) : new ActivityProgress();

        Object isMissionPresent = getSessionAttributes().get(CURRENT_MISSION);
        if (isMissionPresent != null) {
            this.currentMission = UserMission.valueOf(String.valueOf(isMissionPresent));
        }
        else {
            this.currentMission = null;
        }

        LinkedHashMap rawUserProgress = (LinkedHashMap) getSessionAttributes().get(USER_PROGRESS);
        this.userProgress = rawUserProgress != null ? mapper.convertValue(rawUserProgress, UserProgress.class) : new UserProgress(this.currentMission, true);
        this.currentActivity = this.userProgress.getCurrentActivity() != null ? Activities.valueOf(this.userProgress.getCurrentActivity()) : null;

        this.userReplyBreakpointPosition = (Integer) this.getSessionAttributes().getOrDefault(USER_REPLY_BREAKPOINT, null);

        this.statePhase = StateType.valueOf(String.valueOf(getSessionAttributes().getOrDefault(STATE_PHASE, MISSION_INTRO)));

        this.starCount = (int) getSessionAttributes().getOrDefault(STAR_COUNT, 0);

        //noinspection unchecked
        List<String> finishedMissionArray = (List<String>) getSessionAttributes().getOrDefault(FINISHED_MISSIONS, new ArrayList<String>());
        this.finishedMissions = new HashSet<>(finishedMissionArray);
    }


    @Override
    protected void updatePersistentAttributes() {

        if (this.currentMission == null) {
            getPersistentAttributes().clear();
        }
        else {

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

        if (compare(getUserReply(SlotName.NAVIGATION), UserReplies.THIS_MISSION)) {
            logger.warn("Current mission: " + this.currentMission);
            getSessionAttributes().remove(ACTIVITY_PROGRESS);
            getSessionAttributes().remove(STATE_PHASE);
            getSessionAttributes().remove(STAR_COUNT);
            getSessionAttributes().remove(FINISHED_MISSIONS);
            getSessionAttributes().put(INTENT, IntentType.GAME);

            if (this.currentMission != null) {
                handleMissionIntroState(builder, this.currentMission);
            }
            else {
                builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(SELECT_MISSION_PHRASE)))
                        .withAplDocument(aplManager.getContainer())
                        .addBackgroundImageUrl(cardManager.getValueByKey("mission-selection"));
            }
            savePersistentAttributes();
        }
        else if (compare(getUserReply(SlotName.NAVIGATION), UserReplies.NEW_MISSION)) {
            builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(SELECT_MISSION_PHRASE)))
                    .withAplDocument(aplManager.getContainer())
                    .addBackgroundImageUrl(cardManager.getValueByKey("mission-selection"));
            getSessionAttributes().remove(CURRENT_MISSION);
            getSessionAttributes().remove(ACTIVITY_PROGRESS);
            getSessionAttributes().put(INTENT, IntentType.GAME);
        }
        else if (compare(getUserReply(SlotName.NAVIGATION), UserReplies.BACK) ||
                compare(getUserReply(SlotName.CONFIRMATION), UserReplies.NO) ||
                compare(getUserReply(SlotName.NAVIGATION), UserReplies.AGAIN)
        ) {
            getSessionAttributes().put(INTENT, IntentType.GAME);
            getSessionAttributes().put(STATE_PHASE, StateType.SUBMISSION_INTRO);
            builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(RETURN_TO_GAME_PHRASE)));
            logger.warn("Current activity: " + this.currentActivity);
            logger.warn("Current mission: " + this.currentMission);
            if (this.userProgress.isGameFinished() || this.finishedMissions.size() == 3) {
                builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(SELECT_MISSION_PHRASE)))
                        .withAplDocument(aplManager.getContainer())
                        .addBackgroundImageUrl(cardManager.getValueByKey("mission-selection"));
                getSessionAttributes().remove(ACTIVITY_PROGRESS);
                getSessionAttributes().remove(CURRENT_MISSION);
                getSessionAttributes().remove(STATE_PHASE);
                getSessionAttributes().remove(STAR_COUNT);
                getSessionAttributes().remove(FINISHED_MISSIONS);
                getSessionAttributes().put(INTENT, IntentType.GAME);
                savePersistentAttributes();
            }
            else if (this.currentMission != null && this.finishedMissions.contains(this.currentMission.name())) {
                builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(SELECT_MISSION_PHRASE)))
                        .withAplDocument(aplManager.getContainer())
                        .addBackgroundImageUrl(cardManager.getValueByKey("mission-selection"));
                getSessionAttributes().remove(CURRENT_MISSION);
                getSessionAttributes().remove(ACTIVITY_PROGRESS);
                getSessionAttributes().put(INTENT, IntentType.GAME);
            }
            else if (activityProgress != null && activityProgress.getPreviousIngredient() != null && !activityProgress.getPreviousIngredient().isEmpty()) {
                String previousIngredient = activityProgress.getPreviousIngredient();
                builder.addResponse(getDialogTranslator().translate(previousIngredient));
                builder.withAplDocument(aplManager.getContainer());
                builder.addBackgroundImageUrl(getBackgroundImageUrl(previousIngredient));
            }
            else if (this.userProgress != null && this.currentActivity != null && this.statePhase != null) {
                switch (this.statePhase) {
                    case MISSION_INTRO:
                        builder = handleMissionIntroState(builder, this.currentMission);
                        break;
                    case SUBMISSION_INTRO:
                        builder = handleStripeIntroState(builder, this.currentMission, this.userProgress.getStripeCount());
                        break;
                    case ACTIVITY_INTRO:
                        handleActivityIntroState(builder, this.currentActivity, this.userProgress.getStripeCount());
                        break;
                    case DEMO:
                        handleDemoState(builder);
                        break;
                    default:
                        builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(READY_TO_PLAY_PHRASE)));
                        break;
                }
                this.getSessionAttributes().put(ACTIVITY_PROGRESS, this.activityProgress);
                this.getSessionAttributes().put(STATE_PHASE, this.statePhase);
            }
            else if (this.currentActivity == null || this.currentMission == null) {
                builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(SELECT_MISSION_PHRASE)))
                        .withAplDocument(aplManager.getContainer())
                        .addBackgroundImageUrl(cardManager.getValueByKey("mission-selection"));
                getSessionAttributes().remove(CURRENT_MISSION);
                getSessionAttributes().remove(ACTIVITY_PROGRESS);
                getSessionAttributes().put(INTENT, IntentType.GAME);
            }
            else {
                builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(READY_TO_PLAY_PHRASE)));
            }
            getSessionAttributes().put(QUESTION_TIME, System.currentTimeMillis());
        }
        else {
            builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(REPEAT_LAST_PHRASE)));
        }

        return builder.build();
    }

    private String getBackgroundImageUrl(String ingredient) {
        String url = "https://d3ih6nf6077n17.cloudfront.net/images/{size}/icons/" + ingredient.replace(" ", "-") + ".jpg";
        logger.info("Going to load icon by url: " + url);
        return url;
    }

    private DialogItem.Builder handleMissionIntroState(DialogItem.Builder builder, UserMission currentMission) {

        logger.debug("Handling " + this.statePhase + ". Moving to " + SUBMISSION_INTRO);

        this.statePhase = SUBMISSION_INTRO;

        List<BasePhraseContainer> dialog = missionPhraseManager.getMissionIntro(currentMission);

        int iterationPointer = wrapAnyUserResponse(dialog, builder, MISSION_INTRO);

        builder.addBackgroundImageUrl(cardManager.getValueByKey("mission-selection-" + currentMission.key));

        if (iterationPointer >= dialog.size()) {
            builder = handleStripeIntroState(builder, currentMission, this.userProgress.getStripeCount());
        }

        return builder;
    }

    private DialogItem.Builder handleStripeIntroState(DialogItem.Builder builder, UserMission currentMission, int number) {

        logger.debug("Handling " + this.statePhase + ". Moving to " + SUBMISSION_INTRO);

        this.statePhase = ACTIVITY_INTRO;

        List<BasePhraseContainer> dialog = missionPhraseManager.getStripeIntroByMission(currentMission, number);
        if (number > 0) {
            builder.addBackgroundImageUrl(cardManager.getValueByKey("mission-intro-" + currentMission.key + "-" + number));
        }

        int iterationPointer = wrapAnyUserResponse(dialog, builder, SUBMISSION_INTRO);

        if (iterationPointer >= dialog.size()) {
            builder = handleActivityIntroState(builder, this.currentActivity, number);
        }

        return builder;
    }

    private DialogItem.Builder handleActivityIntroState(DialogItem.Builder builder, Activities activity, int number) {

        logger.debug("Handling " + this.statePhase + ". Moving to " + ACTIVITY_INTRO);

        this.userProgress.setCurrentActivity(activity.name());

        savePersistentAttributes();

        SpeechSettings speechSettings = activityPhraseManager.getSpeechForActivityByStripeNumberAtMission(activity, number, this.currentMission);

        builder.addBackgroundImageUrl(speechSettings.getInstructionImageUrl());

        for (BasePhraseContainer partOfSpeech : speechSettings.getIntro()) {
            builder.addResponse(getDialogTranslator().translate(partOfSpeech));
        }

        if (speechSettings.isShouldRunDemo()) {

            logger.debug("Handling " + this.statePhase + ". Moving to " + DEMO);

            this.statePhase = DEMO;

            SpeechSettings demoSpeechSettings = activityPhraseManager.getSpeechForActivityByStripeNumberAtMission(this.currentActivity, this.userProgress.getStripeCount(), this.currentMission);

            builder.addResponse(getDialogTranslator().translate(demoSpeechSettings.getShouldRunDemoPhrase()));
        }
        else {

            logger.debug("Handling " + this.statePhase + ". Moving to " + READY);

            this.statePhase = READY;
            appendReadyToStart(builder);
        }

        resetActivityProgress();

        return builder;
    }

    private void resetActivityProgress() {
        this.activityProgress.reset();
    }

    private DialogItem.Builder handleDemoState(DialogItem.Builder builder) {

        savePersistentAttributes();

        logger.debug("Handling " + this.statePhase + ". Moving to " + READY);

        this.statePhase = READY;

        if (compare(getUserReply(SlotName.CONFIRMATION), UserReplies.NO)) {

            builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(READY_TO_START_PHRASE)));
        }
        else {

            SpeechSettings dialog = activityPhraseManager.getSpeechForActivityByStripeNumberAtMission(this.currentActivity, this.userProgress.getStripeCount(), this.currentMission);

            int iterationPointer = wrapAnyUserResponse(dialog.getDemo(), builder, DEMO);

            if (iterationPointer >= dialog.getDemo().size()) {
                appendReadyToStart(builder);
            }
        }

        SpeechSettings settings = activityPhraseManager.getSpeechForActivityByStripeNumberAtMission(this.currentActivity, this.userProgress.getStripeCount(), this.currentMission);
        builder.addBackgroundImageUrl(settings.getInstructionImageUrl());

        return builder;
    }

    private int wrapAnyUserResponse(List<BasePhraseContainer> dialog, DialogItem.Builder builder, StateType statePhase) {

        if (this.userReplyBreakpointPosition != null) {
            this.getSessionAttributes().remove(USER_REPLY_BREAKPOINT);
        }

        logger.debug("Going to run dialog for " + statePhase + ". Dialog contains " + dialog.size() + " elements. Current step: " + this.userReplyBreakpointPosition);

        int index = 0;

        for (BasePhraseContainer phraseContainer : dialog) {

            index++;

            if (this.userReplyBreakpointPosition != null && index <= this.userReplyBreakpointPosition) {
                continue;
            }

            if (phraseContainer.isUserResponse()) {
                this.userReplyBreakpointPosition = index;
                this.getSessionAttributes().put(SessionConstants.USER_REPLY_BREAKPOINT, index);
                this.getSessionAttributes().put("ANY_RESPONSE", true);
                this.statePhase = statePhase;
                break;
            }
            builder.addResponse(getDialogTranslator().translate(phraseContainer));
        }

        if (index >= dialog.size()) {
            this.userReplyBreakpointPosition = null;
            this.getSessionAttributes().remove(SessionConstants.USER_REPLY_BREAKPOINT);
            logger.debug("Dialog at " + statePhase + " is finished");
        }

        return index;
    }

    private void appendReadyToStart(DialogItem.Builder builder) {

        SpeechSettings speechSettings = activityPhraseManager.getSpeechForActivityByStripeNumberAtMission(this.currentActivity, this.userProgress.getStripeCount(), this.currentMission);

        builder.addResponse(getDialogTranslator().translate(speechSettings.getReadyToStartPhrase()));
    }
}
