package com.muffinsoft.alexa.skills.samuraichef.activities.action;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.enums.IntentType;
import com.muffinsoft.alexa.sdk.enums.SpeechType;
import com.muffinsoft.alexa.sdk.enums.StateType;
import com.muffinsoft.alexa.sdk.model.BasePhraseContainer;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.PhraseContainer;
import com.muffinsoft.alexa.sdk.model.SlotName;
import com.muffinsoft.alexa.sdk.model.Speech;
import com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.ActivityPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.MissionPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.RegularPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.ActivityManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.AplManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.MissionManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;
import com.muffinsoft.alexa.skills.samuraichef.models.ActivityProgress;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SpeechSettings;
import com.muffinsoft.alexa.skills.samuraichef.models.Stripe;
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;
import com.muffinsoft.alexa.skills.samuraichef.models.WordReaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.muffinsoft.alexa.sdk.enums.StateType.ACTIVITY_INTRO;
import static com.muffinsoft.alexa.sdk.enums.StateType.DEMO;
import static com.muffinsoft.alexa.sdk.enums.StateType.GAME_OUTRO;
import static com.muffinsoft.alexa.sdk.enums.StateType.GAME_PHASE_1;
import static com.muffinsoft.alexa.sdk.enums.StateType.LOSE;
import static com.muffinsoft.alexa.sdk.enums.StateType.MISSION_INTRO;
import static com.muffinsoft.alexa.sdk.enums.StateType.MISSION_OUTRO;
import static com.muffinsoft.alexa.sdk.enums.StateType.READY;
import static com.muffinsoft.alexa.sdk.enums.StateType.SUBMISSION_INTRO;
import static com.muffinsoft.alexa.sdk.enums.StateType.SUBMISSION_OUTRO;
import static com.muffinsoft.alexa.sdk.enums.StateType.WIN;
import static com.muffinsoft.alexa.sdk.model.SlotName.NAVIGATION;
import static com.muffinsoft.alexa.skills.samuraichef.components.UserReplyComparator.compare;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.FAILURE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.FAILURE_RE_PROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.GAME_FINISHED_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.MISSION_ALREADY_COMPLETE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.PERFECT_ACTIVITY_EARN_HIGH_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.PERFECT_ACTIVITY_EARN_LOW_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.PERFECT_ACTIVITY_EARN_MID_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.PERFECT_MISSION_EARN_HIGH_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.PERFECT_MISSION_EARN_LOW_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.PERFECT_MISSION_EARN_MID_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.PERFECT_STRIPE_EARN_HIGH_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.PERFECT_STRIPE_EARN_LOW_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.PERFECT_STRIPE_EARN_MID_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.READY_TO_START_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.REDIRECT_TO_SELECT_MISSION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.SELECT_MISSION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.WANT_RESET_PROGRESS_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.WON_RE_PROMPT_PHRASE;
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

abstract class BaseSamuraiChefStateManager extends BaseStateManager {

    static final Logger logger = LogManager.getLogger(BaseSamuraiChefStateManager.class);
    final RegularPhraseManager regularPhraseManager;
    final ActivityManager activityManager;
    final MissionManager missionManager;
    final CardManager cardManager;
    private final AplManager aplManager;
    private final ActivityPhraseManager activityPhraseManager;
    private final MissionPhraseManager missionPhraseManager;
    Activities currentActivity;
    ActivityProgress activityProgress;
    Stripe stripe;
    private StateType statePhase;
    private UserProgress userProgress;
    private UserMission currentMission;
    private boolean isLeaveMission = false;
    private boolean isMoveToReset = false;
    private int starCount;
    private Set<String> finishedMissions;
    private Integer userReplyBreakpointPosition;

    BaseSamuraiChefStateManager(Map<String, Slot> slots, AttributesManager attributesManager, SettingsDependencyContainer settingsDependencyContainer, PhraseDependencyContainer phraseDependencyContainer) {
        super(slots, attributesManager, settingsDependencyContainer.getDialogTranslator());
        this.regularPhraseManager = phraseDependencyContainer.getRegularPhraseManager();
        this.missionPhraseManager = phraseDependencyContainer.getMissionPhraseManager();
        this.activityPhraseManager = phraseDependencyContainer.getActivityPhraseManager();
        this.activityManager = settingsDependencyContainer.getActivityManager();
        this.missionManager = settingsDependencyContainer.getMissionManager();
        this.cardManager = settingsDependencyContainer.getCardManager();
        this.aplManager = settingsDependencyContainer.getAplManager();
    }

    @Override
    protected void populateActivityVariables() {

        this.currentMission = UserMission.valueOf(String.valueOf(getSessionAttributes().get(CURRENT_MISSION)));

        this.userReplyBreakpointPosition = (Integer) this.getSessionAttributes().getOrDefault(USER_REPLY_BREAKPOINT, null);

        this.statePhase = StateType.valueOf(String.valueOf(getSessionAttributes().getOrDefault(STATE_PHASE, MISSION_INTRO)));

        LinkedHashMap rawUserProgress = (LinkedHashMap) getSessionAttributes().get(USER_PROGRESS);
        this.userProgress = rawUserProgress != null ? mapper.convertValue(rawUserProgress, UserProgress.class) : new UserProgress(this.currentMission, true);

        this.starCount = (int) getSessionAttributes().getOrDefault(STAR_COUNT, 0);

        @SuppressWarnings("unchecked") List<String> finishedMissionArray = (List<String>) getSessionAttributes().getOrDefault(FINISHED_MISSIONS, new ArrayList<String>());
        this.finishedMissions = new HashSet<>(finishedMissionArray);

        LinkedHashMap rawActivityProgress = (LinkedHashMap) getSessionAttributes().get(ACTIVITY_PROGRESS);
        this.activityProgress = rawActivityProgress != null ? mapper.convertValue(rawActivityProgress, ActivityProgress.class) : new ActivityProgress();

        logger.debug("Session attributes on the start of handling: " + this.getSessionAttributes().toString());
    }

    @Override
    protected void updatePersistentAttributes() {

        updateMissionUserProgress();

        if (this.activityProgress.isStripeComplete()) {
            if (!this.activityProgress.isStarUpdated()) {
                ++this.starCount;
                this.activityProgress.setStarUpdated(true);
            }
            getPersistentAttributes().put(STAR_COUNT, this.starCount);
            getSessionAttributes().put(STAR_COUNT, this.starCount);
            logger.debug("Was updated star counter at Persistent attributes");
        }

        if (this.activityProgress.isMissionFinished()) {
            getPersistentAttributes().put(FINISHED_MISSIONS, this.finishedMissions);
            logger.debug("Was updated completed missions in all missions");
        }

        logger.debug("Persistent attributes on the end of handling: " + this.getPersistentAttributes().toString());
    }

    void updateUserMistakeStory() {
        int mistakesCount = this.activityProgress.getMistakesCount();
        this.userProgress.addMistakeCount(mistakesCount);
    }

    private void updateMissionUserProgress() {

        try {
            if (this.currentMission == UserMission.LOW_MISSION) {
                getPersistentAttributes().put(USER_LOW_PROGRESS_DB, mapper.writeValueAsString(this.userProgress));
                logger.debug("Was update User Progress at low mission");
            }
            else if (this.currentMission == UserMission.MEDIUM_MISSION) {
                getPersistentAttributes().put(USER_MID_PROGRESS_DB, mapper.writeValueAsString(this.userProgress));
                logger.debug("Was update User Progress at medium mission");
            }
            else {
                getPersistentAttributes().put(USER_HIGH_PROGRESS_DB, mapper.writeValueAsString(this.userProgress));
                logger.debug("Was update User Progress at high mission");
            }
        }
        catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Caught exception while updating user progress: " + e.getMessage(), e);
        }
    }

    @Override
    protected void updateSessionAttributes() {

        this.userProgress.setCurrentActivity(this.currentActivity.name());

        if (this.isLeaveMission) {
            if (!this.isMoveToReset) {
                getSessionAttributes().remove(CURRENT_MISSION);
            }
            getSessionAttributes().remove(ACTIVITY_PROGRESS);
            getSessionAttributes().remove(STATE_PHASE);
            getSessionAttributes().remove(QUESTION_TIME);
        }
        else {
            getSessionAttributes().put(ACTIVITY_PROGRESS, this.activityProgress);
            getSessionAttributes().put(STATE_PHASE, this.statePhase);
        }

        logger.debug("Session attributes on the end of handling: " + this.getSessionAttributes().toString());
    }

    @Override
    public DialogItem nextResponse() {

        DialogItem.Builder builder = DialogItem.builder();

        this.userProgress.setCurrentActivity(this.currentActivity.name());

        if (this.finishedMissions.contains(this.currentMission.name()) && (this.statePhase != MISSION_OUTRO && this.statePhase != WIN)) {
            logger.warn("User has finished current mission");
            return handleAlreadyFinishedMission(builder);
        }

        stripe = activityManager.getStripeForActivityAtMission(this.currentActivity, this.userProgress.getStripeCount(), this.currentMission);

        switch (this.statePhase) {
            case MISSION_INTRO:
                builder = handleMissionIntroState(builder, this.currentMission);
                break;
            case SUBMISSION_INTRO:
                builder = handleStripeIntroState(builder, this.currentMission, this.userProgress.getStripeCount());
                break;
            case ACTIVITY_INTRO:
                builder = handleActivityIntroState(builder, this.currentActivity, this.userProgress.getStripeCount());
                break;
            case DEMO:
                builder = handleDemoState(builder);
                break;
            case RETURN_TO_GAME:
                builder = handleReturnToGameState(builder);
                break;
            case READY:
                builder = handleReadyToStartState(builder);
                break;
            case LOSE:
                builder = handleLoseState(builder);
                break;
            case WIN:
                builder = handleWinState(builder);
                break;
            case SUBMISSION_OUTRO:
                builder = handleStripeOutroState(builder, this.currentMission);
                break;
            case MISSION_OUTRO:
                builder = handleMissionOutroState(builder);
                break;
            default:
                builder = handleActivePhaseState(builder);
                break;
        }

        addSessionEntities(builder);


        return builder.withAplDocument(aplManager.getContainer()).build();
    }

    private DialogItem.Builder handleReturnToGameState(DialogItem.Builder builder) {
        String ingredient = this.activityProgress.getPreviousIngredient();
        if (ingredient == null || ingredient.isEmpty()) {
            ingredient = nextIngredient(ingredient);
        }
        builder.addResponse(getSoundLine(ingredient, false))
                .addBackgroundImageUrl(getBackgroundImageUrl(ingredient));
        this.statePhase = GAME_PHASE_1;
        return builder;
    }

    private void addSessionEntities(DialogItem.Builder builder) {
        String reaction = this.activityProgress.getCurrentIngredientReaction();
        if (reaction != null && !reaction.isEmpty()) {
            Set<String> entities = new HashSet<>();
            entities.add(reaction);
            builder.withDynamicEntities(entities);
        }
    }

    private DialogItem handleAlreadyFinishedMission(DialogItem.Builder builder) {

        this.isLeaveMission = true;
        this.isMoveToReset = true;

        getSessionAttributes().put(INTENT, IntentType.RESET);
        getSessionAttributes().put(CURRENT_MISSION, this.userProgress.getMission());

        return builder
                .addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(MISSION_ALREADY_COMPLETE_PHRASE)))
                .addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(WANT_RESET_PROGRESS_PHRASE)))
                .build();
    }

    protected abstract DialogItem.Builder handleActivePhaseState(DialogItem.Builder builder);

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

    private DialogItem.Builder handleStripeIntroState(DialogItem.Builder builder, UserMission currentMission, int number) {

        logger.debug("Handling " + this.statePhase + ". Moving to " + SUBMISSION_INTRO);

        this.statePhase = ACTIVITY_INTRO;

        List<BasePhraseContainer> dialog = missionPhraseManager.getStripeIntroByMission(currentMission, number);
        builder.addBackgroundImageUrl(cardManager.getValueByKey("mission-intro-" + currentMission.key + "-" + number));

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

    private DialogItem.Builder handleReadyToStartState(DialogItem.Builder builder) {

        savePersistentAttributes();

        if (compare(getUserReply(SlotName.CONFIRMATION), UserReplies.NO)) {
            this.getSessionAttributes().remove(ACTIVITY_PROGRESS);
            this.getSessionAttributes().remove(CURRENT_MISSION);

            builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(SELECT_MISSION_PHRASE)))
                    .addBackgroundImageUrl(cardManager.getValueByKey("mission-selection"));
        }
        else {
            String speechText = nextIngredient(this.activityProgress.getPreviousIngredient());

            logger.debug("Handling " + this.statePhase + ". Moving to " + GAME_PHASE_1);

            this.statePhase = GAME_PHASE_1;

            builder.addResponse(getSoundLine(speechText, false))
                    .addBackgroundImageUrl(getBackgroundImageUrl(speechText));
        }
        return builder;
    }

    void resetActivityProgress() {
        this.activityProgress.reset();
    }

    private DialogItem.Builder handleWinState(DialogItem.Builder builder) {

        if (this.activityProgress.isStripeComplete()) {
            return endStripeAfterWin(builder);
        }
        else {
            return getNextActivityAfterWin(builder);
        }
    }

    private DialogItem.Builder getNextActivityAfterWin(DialogItem.Builder builder) {

        Activities nextActivity = missionManager.getNextActivity(this.currentActivity, currentMission);

        boolean invalidCondition = this.userProgress.getFinishedActivities().contains(nextActivity.name());

        while (invalidCondition) {
            nextActivity = missionManager.getNextActivity(nextActivity, currentMission);
            invalidCondition = this.userProgress.getFinishedActivities().contains(nextActivity.name());
        }

        currentActivity = nextActivity;
        this.userProgress.setCurrentActivity(nextActivity.name());
        builder = handleActivityIntroState(builder, nextActivity, this.userProgress.getStripeCount());

        return builder;
    }

    private DialogItem.Builder endStripeAfterWin(DialogItem.Builder builder) {

        int number = this.userProgress.getStripeCount() - 1; // get previous stripe outro

        this.currentActivity = missionManager.getFirstActivityForMission(currentMission);

        logger.debug("Handling " + this.statePhase + ". Moving to " + SUBMISSION_OUTRO);

        this.statePhase = SUBMISSION_OUTRO;

        List<BasePhraseContainer> dialog = missionPhraseManager.getStripeOutroByMission(currentMission, number);

        int iterationPointer = wrapAnyUserResponse(dialog, builder, WIN);

        if (iterationPointer >= dialog.size()) {
            builder = handleStripeOutroState(builder, this.currentMission);
        }

        return builder;
    }

    private DialogItem.Builder handleLoseState(DialogItem.Builder builder) {

        if (compare(getUserReply(NAVIGATION), UserReplies.AGAIN) || compare(getUserReply(SlotName.CONFIRMATION), UserReplies.YES)) {
            builder = handleActivityIntroState(builder, this.currentActivity, this.userProgress.getStripeCount());
        }
        else {
            builder = getSelectMissionDialog(builder);
        }
        savePersistentAttributes();

        return builder;
    }

    private DialogItem.Builder getSelectMissionDialog(DialogItem.Builder builder) {

        logger.debug("Handling " + this.statePhase + ". Moving to " + MISSION_INTRO);

        isLeaveMission = true;

        this.statePhase = MISSION_INTRO;

        this.getSessionAttributes().remove(CURRENT_MISSION);

        return builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(SELECT_MISSION_PHRASE)));
    }

    private DialogItem.Builder handleStripeOutroState(DialogItem.Builder builder, UserMission currentMission) {

        builder.addBackgroundImageUrl(cardManager.getValueByKey("mission-outro-" + currentMission.key + "-" + (userProgress.getStripeCount() - 1)));  // get previous stripe outro

        calculateStripeProgress();

        if (this.userProgress.getMistakesInStripe() == 0 && !this.userProgress.isPerfectStripe()) {
            this.userProgress.setPerfectStripe(true);
            builder = appendEarnPerfectStripeByLevel(builder);
        }

        if (this.activityProgress.isMissionFinished()) {

            logger.debug("Handling " + this.statePhase + ". Moving to " + MISSION_OUTRO);

            this.statePhase = MISSION_OUTRO;

            if (this.userProgress.getMistakesInMission() == 0 && !this.userProgress.isPerfectMission()) {
                this.userProgress.setPerfectMission(true);
                builder = appendEarnPerfectMissionByLevel(builder);
            }

            builder = handleMissionOutroState(builder);

            return builder;
        }
        else {
            return handleStripeIntroState(builder, this.currentMission, this.userProgress.getStripeCount());
        }
    }

    DialogItem.Builder handleWiningEnd(DialogItem.Builder builder) {
        calculateActivityProgress();
        return handleWinDialog(builder);
    }

    private DialogItem.Builder handleMissionOutroState(DialogItem.Builder builder) {

        List<BasePhraseContainer> missionOutro = missionPhraseManager.getMissionOutro(currentMission);

        int iterationPoint = wrapAnyUserResponse(missionOutro, builder, SUBMISSION_OUTRO);

        if (iterationPoint >= missionOutro.size()) {
            this.isLeaveMission = true;

            if (this.userProgress.isGameFinished()) {

                logger.debug("Handling " + this.statePhase + ". Moving to " + GAME_OUTRO);

                this.statePhase = GAME_OUTRO;

                builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(GAME_FINISHED_PHRASE)));
            }
            else {

                logger.debug("Handling " + this.statePhase + ". Moving to " + MISSION_INTRO);

                builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(REDIRECT_TO_SELECT_MISSION_PHRASE)));
                builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(SELECT_MISSION_PHRASE)))
                        .addBackgroundImageUrl(cardManager.getValueByKey("mission-selection"));

            }

            this.userProgress.setMissionFinished(true);
            savePersistentAttributes();
        }

        return builder;
    }

    private void calculateStripeProgress() {

        if (this.userProgress.getStripeCount() == missionManager.getContainer().getStripesAtMissionCount()) {
            this.finishedMissions.add(this.currentMission.name());
            this.userProgress.setMistakesInStripe(0);
            this.activityProgress.setMissionFinished(true);
            savePersistentAttributes();
        }

        if (this.starCount == missionManager.getContainer().getMaxStarCount()) {
            this.userProgress.setGameFinished(true);
            savePersistentAttributes();
        }
    }

    private void calculateActivityProgress() {

        this.userProgress.addFinishedActivities(this.currentActivity.name());

        logger.info("Current user progress finished activities " + String.join(", ", this.userProgress.getFinishedActivities()));
        if (this.userProgress.getFinishedActivities().size() == Activities.values().length) {

            this.userProgress.iterateStripeCount();
            this.userProgress.resetFinishRounds();

            logger.info("Stripe has been just compete");
            this.activityProgress.setStripeComplete(true);
        }
        savePersistentAttributes();
    }

    private void appendReadyToStart(DialogItem.Builder builder) {

        SpeechSettings speechSettings = activityPhraseManager.getSpeechForActivityByStripeNumberAtMission(this.currentActivity, this.userProgress.getStripeCount(), this.currentMission);

        builder.addResponse(getDialogTranslator().translate(speechSettings.getReadyToStartPhrase()));
    }

    private DialogItem.Builder handleWinDialog(DialogItem.Builder builder) {

        this.statePhase = WIN;

        updateUserMistakeStory();

        builder.removeLastResponse();
        builder.removeAllBackgroundImageUrls();
        builder.addBackgroundImageUrl(cardManager.getValueByKey("mission-selection-" + currentMission.key));

        if (this.activityManager.isActivityCompetition(this.currentActivity)) {
            WordReaction randomIngredient = getRandomIngredient(this.activityProgress.getPreviousIngredient());

            builder
                    .replaceResponse(getSoundLine(randomIngredient.getIngredient(), false))
                    .addResponse(getSoundLine(getWrongReplyOnIngredient(randomIngredient.getIngredient()), true))
                    .withReprompt(getDialogTranslator().translate(regularPhraseManager.getValueByKey(WON_RE_PROMPT_PHRASE)));
        }

        SpeechSettings speechForActivityByStripeNumberAtMission;

        if (this.activityProgress.isStripeComplete()) {
            speechForActivityByStripeNumberAtMission = activityPhraseManager.getSpeechForActivityByStripeNumberAtMission(this.currentActivity, this.userProgress.getStripeCount() - 1, this.currentMission);
        }
        else {
            speechForActivityByStripeNumberAtMission = activityPhraseManager.getSpeechForActivityByStripeNumberAtMission(this.currentActivity, this.userProgress.getStripeCount(), this.currentMission);
        }

        List<BasePhraseContainer> outro = speechForActivityByStripeNumberAtMission.getOutro();

        int iterationPointer = wrapAnyUserResponse(outro, builder, GAME_PHASE_1);

        if (iterationPointer >= outro.size()) {

            if (this.activityProgress.getMistakesCount() == 0 && !this.userProgress.isPerfectActivity()) {
                this.userProgress.setPerfectActivity(true);
                builder = appendEarnPerfectActivityByLevel(builder);
            }

            if (this.activityProgress.isStripeComplete()) {
                builder = endStripeAfterWin(builder);
            }
            else {
                builder = handleWinState(builder);
            }
        }

        return builder;
    }

    @SuppressWarnings("Duplicates")
    private DialogItem.Builder appendEarnPerfectMissionByLevel(DialogItem.Builder builder) {
        switch (this.currentMission) {
            case LOW_MISSION:
                return builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(PERFECT_MISSION_EARN_LOW_PHRASE)));
            case MEDIUM_MISSION:
                return builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(PERFECT_MISSION_EARN_MID_PHRASE)));
            case HIGH_MISSION:
                return builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(PERFECT_MISSION_EARN_HIGH_PHRASE)));
        }
        return builder;
    }

    @SuppressWarnings("Duplicates")
    private DialogItem.Builder appendEarnPerfectStripeByLevel(DialogItem.Builder builder) {
        switch (this.currentMission) {
            case LOW_MISSION:
                return builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(PERFECT_STRIPE_EARN_LOW_PHRASE)));
            case MEDIUM_MISSION:
                return builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(PERFECT_STRIPE_EARN_MID_PHRASE)));
            case HIGH_MISSION:
                return builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(PERFECT_STRIPE_EARN_HIGH_PHRASE)));
        }
        return builder;
    }

    @SuppressWarnings("Duplicates")
    private DialogItem.Builder appendEarnPerfectActivityByLevel(DialogItem.Builder builder) {
        switch (this.currentMission) {
            case LOW_MISSION:
                return builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(PERFECT_ACTIVITY_EARN_LOW_PHRASE)));
            case MEDIUM_MISSION:
                return builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(PERFECT_ACTIVITY_EARN_MID_PHRASE)));
            case HIGH_MISSION:
                return builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(PERFECT_ACTIVITY_EARN_HIGH_PHRASE)));
        }
        return builder;
    }

    DialogItem.Builder getRePromptSuccessDialog(DialogItem.Builder builder) {
        return builder
                .addResponse(getSoundLine(this.activityProgress.getPreviousIngredient(), false))
                .addBackgroundImageUrl(getBackgroundImageUrl(this.activityProgress.getPreviousIngredient()))
                .turnOffReprompt();
    }

    DialogItem.Builder getSuccessDialog(DialogItem.Builder builder) {

        String ingredient = nextIngredient(this.activityProgress.getPreviousIngredient());

        builder.addResponse(getSoundLine(ingredient, false))
                .addBackgroundImageUrl(getBackgroundImageUrl(ingredient));

        if (this.activityManager.isActivityCompetition(this.currentActivity)) {
            return appendMockCompetitionAnswer(builder);
        }

        return builder.turnOffReprompt();
    }

    private DialogItem.Builder appendMockCompetitionAnswer(DialogItem.Builder builder) {

        Speech speech = builder.popLastSpeech();

        WordReaction randomIngredient = getRandomIngredient(speech.getContent());

        builder.addResponse(getSoundLine(randomIngredient.getIngredient(), false))
                .addResponse(getSoundLine(randomIngredient.getUserReply(), true))
                .addResponse(speech);

        return builder;
    }

    private Speech getSoundLine(String source, boolean isReaction) {
        if (isReaction) {
            source = "reaction_" + source;
        }
        if (stripe.isUseVocabulary()) {
            String path = "https://s3.amazonaws.com/samurai-chef-store/words/" + source + ".mp3";
            logger.info("Try to get sound by url " + path);
            return new Speech(SpeechType.AUDIO, path, 0);
        }
        else {
            Speech sound = getDialogTranslator().getSound(source, true);
            logger.info("Try to get sound by url " + sound.getContent());
            return sound;
        }
    }

    DialogItem.Builder getFailureDialog(DialogItem.Builder builder, List<PhraseContainer> speechText) {
        String ingredient = nextIngredient(this.activityProgress.getPreviousIngredient());
        return builder
                .addResponse(getDialogTranslator().translate(speechText))
                .addResponse(getSoundLine(ingredient, false))
                .addBackgroundImageUrl(getBackgroundImageUrl(ingredient))
                .turnOffReprompt();
    }

    DialogItem.Builder getLoseRoundDialog(DialogItem.Builder builder, String value) {
        this.statePhase = LOSE;
        return builder
                .addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(value)))
                .addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(FAILURE_PHRASE)))

                .withReprompt(getDialogTranslator().translate(regularPhraseManager.getValueByKey(FAILURE_RE_PROMPT_PHRASE)));
    }

    private WordReaction getRandomIngredient(String ingredient) {
        return activityManager.getNextWord(this.stripe, ingredient);
    }

    private String getWrongReplyOnIngredient(String ingredient) {
        WordReaction nextIngredient = activityManager.getNextWord(this.stripe, ingredient);
        return nextIngredient.getUserReply();
    }

    private String getBackgroundImageUrl(String ingredient) {
        String url;
        if (activityManager.isActivityUseVocabulary(this.currentActivity)) {
            SpeechSettings settings = activityPhraseManager.getSpeechForActivityByStripeNumberAtMission(this.currentActivity, this.userProgress.getStripeCount(), this.currentMission);
            url = settings.getInstructionImageUrl();
        }
        else {
            url = "https://s3.amazonaws.com/samurai-chef-store/images/{size}/icons/" + ingredient.replace(" ", "-") + ".jpg";
        }
        logger.info("Going to load image by url: " + url);
        return url;
    }

    private String nextIngredient(String ingredient) {

        WordReaction nextIngredient = activityManager.getNextWord(this.stripe, ingredient);

        this.activityProgress.setCurrentIngredientReaction(nextIngredient.getUserReply());
        this.activityProgress.setPreviousIngredient(nextIngredient.getIngredient());

        return nextIngredient.getIngredient();
    }
}
