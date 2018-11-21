package com.muffinsoft.alexa.skills.samuraichef.activities.action;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.SlotName;
import com.muffinsoft.alexa.sdk.model.Speech;
import com.muffinsoft.alexa.skills.samuraichef.components.UserReplyComparator;
import com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivityManager;
import com.muffinsoft.alexa.skills.samuraichef.content.AliasManager;
import com.muffinsoft.alexa.skills.samuraichef.content.MissionManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.Intents;
import com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;
import com.muffinsoft.alexa.skills.samuraichef.models.ActivityProgress;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.IngredientReaction;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseSettings;
import com.muffinsoft.alexa.skills.samuraichef.models.SpeechSettings;
import com.muffinsoft.alexa.skills.samuraichef.models.Stripe;
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.muffinsoft.alexa.skills.samuraichef.components.VoiceTranslator.translate;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.FAILURE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.FAILURE_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.GAME_FINISHED_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.MISSION_ALREADY_COMPLETE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.PERFECT_ACTIVITY_EARN_HIGH_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.PERFECT_ACTIVITY_EARN_LOW_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.PERFECT_ACTIVITY_EARN_MID_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.PERFECT_MISSION_EARN_HIGH_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.PERFECT_MISSION_EARN_LOW_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.PERFECT_MISSION_EARN_MID_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.PERFECT_STRIPE_EARN_HIGH_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.PERFECT_STRIPE_EARN_LOW_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.PERFECT_STRIPE_EARN_MID_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.READY_TO_START_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.READY_TO_START_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.REDIRECT_TO_SELECT_MISSION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.SELECT_MISSION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.SEVERAL_VALUES_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.TRY_AGAIN_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WANT_RESET_PROGRESS_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.WON_REPROMPT_PHRASE;
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
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.ACTIVITY_INTRO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.DEMO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.GAME_OUTRO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.LOSE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.MISSION_INTRO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.MISSION_OUTRO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.PHASE_1;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.READY_PHASE;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.STRIPE_INTRO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.STRIPE_OUTRO;
import static com.muffinsoft.alexa.skills.samuraichef.enums.StatePhase.WIN;

abstract class BaseSamuraiChefStateManager extends BaseStateManager {

    protected static final Logger logger = LogManager.getLogger(BaseSamuraiChefStateManager.class);
    protected final PhraseManager phraseManager;
    final ActivityManager activityManager;
    final AliasManager aliasManager;
    final MissionManager missionManager;
    private final String userFoodSlotReply;
    protected Activities currentActivity;
    protected ActivityProgress activityProgress;
    Stripe stripe;
    StatePhase statePhase;
    private UserProgress userProgress;
    private UserMission currentMission;
    private boolean isLeaveMission = false;
    private boolean isMoveToReset = false;
    private int starCount;
    private Set<String> finishedMissions;
    private Integer userReplyBreakpointPosition;

    BaseSamuraiChefStateManager(Map<String, Slot> slots, AttributesManager attributesManager, ConfigContainer configContainer) {
        super(slots, attributesManager);
        this.phraseManager = configContainer.getPhraseManager();
        this.activityManager = configContainer.getActivityManager();
        this.aliasManager = configContainer.getAliasManager();
        this.missionManager = configContainer.getMissionManager();
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

        this.userReplyBreakpointPosition = (Integer) this.getSessionAttributes().getOrDefault(USER_REPLY_BREAKPOINT, null);

        this.statePhase = StatePhase.valueOf(String.valueOf(getSessionAttributes().getOrDefault(STATE_PHASE, MISSION_INTRO)));

        LinkedHashMap rawUserProgress = (LinkedHashMap) getSessionAttributes().get(USER_PROGRESS);
        this.userProgress = rawUserProgress != null ? mapper.convertValue(rawUserProgress, UserProgress.class) : new UserProgress(this.currentMission, true);

        this.starCount = (int) getSessionAttributes().getOrDefault(STAR_COUNT, 0);

        //noinspection unchecked
        this.finishedMissions = (Set<String>) getSessionAttributes().getOrDefault(FINISHED_MISSIONS, new HashSet<>());

        LinkedHashMap rawActivityProgress = (LinkedHashMap) getSessionAttributes().get(ACTIVITY_PROGRESS);
        this.activityProgress = rawActivityProgress != null ? mapper.convertValue(rawActivityProgress, ActivityProgress.class) : new ActivityProgress();

        logger.debug("Session attributes on the start of handling: " + this.getSessionAttributes().toString());
    }

    @Override
    protected void updatePersistentAttributes() {

        updateMissionUserProgress();

        if (this.activityProgress.isStripeComplete()) {
            ++this.starCount;
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
            if (this.isMoveToReset) {
                getSessionAttributes().remove(CURRENT_MISSION);
            }
            getSessionAttributes().remove(USER_PROGRESS);
            getSessionAttributes().remove(ACTIVITY_PROGRESS);
            getSessionAttributes().remove(STATE_PHASE);
            getSessionAttributes().remove(QUESTION_TIME);
        }
        else {
            getSessionAttributes().put(USER_PROGRESS, this.userProgress);
            getSessionAttributes().put(ACTIVITY_PROGRESS, this.activityProgress);
            getSessionAttributes().put(STATE_PHASE, this.statePhase);
        }

        logger.debug("Session attributes on the end of handling: " + this.getSessionAttributes().toString());
    }

    @Override
    public DialogItem nextResponse() {

        DialogItem.Builder builder = DialogItem.builder();

        if (this.userProgress.isMissionFinished()) {
            return handleAlreadyFinishedMission(builder);
        }

        if (!getUserMultipleReplies().isEmpty()) {
            return handleMultipleResponses(builder);
        }

        stripe = activityManager.getStripeForActivityAtMission(this.currentActivity, this.userProgress.getStripeCount(), this.currentMission);

        switch (this.statePhase) {
            case MISSION_INTRO:
                builder = handleMissionIntroState(builder, this.currentMission);
                break;
            case STRIPE_INTRO:
                builder = handleStripeIntroStrate(builder, this.currentMission, this.userProgress.getStripeCount());
                break;
            case ACTIVITY_INTRO:
                builder = handleActivityIntroState(builder, this.currentActivity, this.userProgress.getStripeCount());
                break;
            case DEMO:
                builder = handleDemoState(builder);
                break;
            case READY_PHASE:
                builder = handleReadyToStartState(builder);
                break;
            case LOSE:
                builder = handleLoseState(builder);
                break;
            case WIN:
                builder = handleWinState(builder, this.currentMission);
                break;
            case STRIPE_OUTRO:
                builder = handleStripeOutroState(builder, this.currentMission);
                break;
            case MISSION_OUTRO:
                builder = handleMissionOutroState(builder);
                break;
            default:
                builder = handleActivePhaseState(builder);
                break;
        }

        return builder.build();
    }

    private DialogItem handleAlreadyFinishedMission(DialogItem.Builder builder) {

        this.isLeaveMission = true;
        this.isMoveToReset = true;

        getSessionAttributes().put(INTENT, Intents.RESET);

        return builder.withSlotName(actionSlotName)
                .addResponse(translate(phraseManager.getValueByKey(MISSION_ALREADY_COMPLETE_PHRASE)))
                .addResponse(translate(phraseManager.getValueByKey(WANT_RESET_PROGRESS_PHRASE)))
                .build();
    }

    private DialogItem handleMultipleResponses(DialogItem.Builder builder) {

        return builder.withSlotName(actionSlotName)
                .addResponse(translate(phraseManager.getValueByKey(SEVERAL_VALUES_PHRASE)))
                .addResponse(translate(String.join(", ", this.getUserMultipleReplies())))
                .build();
    }

    protected abstract DialogItem.Builder handleActivePhaseState(DialogItem.Builder builder);

    private DialogItem.Builder handleMissionIntroState(DialogItem.Builder builder, UserMission currentMission) {

        logger.debug("Handling " + this.statePhase + ". Moving to " + STRIPE_INTRO);

        this.statePhase = STRIPE_INTRO;

        List<PhraseSettings> dialog = missionManager.getMissionIntro(currentMission);

        int iterationPointer = wrapAnyUserResponse(dialog, builder, MISSION_INTRO);

        if (iterationPointer >= dialog.size()) {
            builder = handleStripeIntroStrate(builder, currentMission, this.userProgress.getStripeCount());
        }

        return builder.withSlotName(actionSlotName);
    }

    private int wrapAnyUserResponse(List<PhraseSettings> dialog, DialogItem.Builder builder, StatePhase statePhase) {

        if (this.userReplyBreakpointPosition != null) {
            this.getSessionAttributes().remove(USER_REPLY_BREAKPOINT);
        }

        int index = 0;

        for (PhraseSettings phraseSettings : dialog) {

            index++;

            if (this.userReplyBreakpointPosition != null && index <= this.userReplyBreakpointPosition) {
                continue;
            }

            if (phraseSettings.isUserResponse()) {
                this.getSessionAttributes().put(SessionConstants.USER_REPLY_BREAKPOINT, index + 1);
                this.statePhase = statePhase;
                break;
            }
            builder.addResponse(translate(phraseSettings));
        }
        return index;
    }

    private DialogItem.Builder handleStripeIntroStrate(DialogItem.Builder builder, UserMission currentMission, int number) {

        logger.debug("Handling " + this.statePhase + ". Moving to " + ACTIVITY_INTRO);

        this.statePhase = ACTIVITY_INTRO;

        List<PhraseSettings> dialog = missionManager.getStripeIntroByMission(currentMission, number);

        int iterationPointer = wrapAnyUserResponse(dialog, builder, STRIPE_INTRO);

        if (iterationPointer >= dialog.size()) {
            builder = handleActivityIntroState(builder, this.currentActivity, number);
        }

        return builder.withSlotName(actionSlotName);
    }

    private DialogItem.Builder handleActivityIntroState(DialogItem.Builder builder, Activities activity, int number) {

        SpeechSettings speechSettings = activityManager.getSpeechForActivityByStripeNumberAtMission(activity, number, this.currentMission);

        for (PhraseSettings partOfSpeech : speechSettings.getIntro()) {
            builder.addResponse(translate(partOfSpeech));
        }

        if (speechSettings.isShouldRunDemo()) {

            logger.debug("Handling " + this.statePhase + ". Moving to " + DEMO);

            this.statePhase = DEMO;

            SpeechSettings demoSpeechSettings = activityManager.getSpeechForActivityByStripeNumberAtMission(this.currentActivity, this.userProgress.getStripeCount(), this.currentMission);

            builder.addResponse(translate(demoSpeechSettings.getShouldRunDemoPhrase()));
        }
        else {

            logger.debug("Handling " + this.statePhase + ". Moving to " + READY_PHASE);

            this.statePhase = READY_PHASE;
            builder = appendReadyToStart(builder);
        }

        return builder.withSlotName(actionSlotName);
    }

    private DialogItem.Builder handleDemoState(DialogItem.Builder builder) {

        logger.debug("Handling " + this.statePhase + ". Moving to " + READY_PHASE);

        this.statePhase = READY_PHASE;

        if (UserReplyComparator.compare(getUserReply(), UserReplies.NO)) {

            builder.addResponse(translate(phraseManager.getValueByKey(READY_TO_START_PHRASE)));
            builder.withReprompt(translate(phraseManager.getValueByKey(READY_TO_START_REPROMPT_PHRASE)));
        }
        else {

            SpeechSettings speechSettings = activityManager.getSpeechForActivityByStripeNumberAtMission(this.currentActivity, this.userProgress.getStripeCount(), this.currentMission);

            wrapAnyUserResponse(speechSettings.getDemo(), builder, DEMO);

            builder = appendReadyToStart(builder);

            builder.withReprompt(translate(phraseManager.getValueByKey(READY_TO_START_REPROMPT_PHRASE)));
        }

        return builder.withSlotName(actionSlotName);
    }

    private DialogItem.Builder handleReadyToStartState(DialogItem.Builder builder) {

        String speechText = nextIngredient();

        logger.debug("Handling " + this.statePhase + ". Moving to " + PHASE_1);

        this.statePhase = PHASE_1;

        return builder.addResponse(translate(speechText)).withSlotName(actionSlotName);
    }


    protected void resetActivityProgress() {
        this.statePhase = ACTIVITY_INTRO;
        this.activityProgress.reset();
    }

    private DialogItem.Builder handleWinState(DialogItem.Builder builder, UserMission currentMission) {

        boolean stripeComplete = this.activityProgress.isStripeComplete();

        resetActivityProgress();

        if (stripeComplete) {
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

        logger.debug("Handling " + this.statePhase + ". Moving to " + STRIPE_OUTRO);

        this.statePhase = STRIPE_OUTRO;

        calculateStripeProgress();

        List<PhraseSettings> dialog = missionManager.getStripeOutroByMission(currentMission, number);

        wrapAnyUserResponse(dialog, builder, WIN);

        return handleStripeOutroState(builder, this.currentMission);
    }

    private DialogItem.Builder handleLoseState(DialogItem.Builder builder) {

        if (UserReplyComparator.compare(getUserReply(), UserReplies.AGAIN) || UserReplyComparator.compare(getUserReply(), UserReplies.YES)) {
            resetActivityProgress();
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

        return builder.withSlotName(actionSlotName).addResponse(translate(phraseManager.getValueByKey(SELECT_MISSION_PHRASE)));
    }

    private DialogItem.Builder handleStripeOutroState(DialogItem.Builder builder, UserMission currentMission) {

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

            List<PhraseSettings> missionOutro = missionManager.getMissionOutro(currentMission);

            wrapAnyUserResponse(missionOutro, builder, STRIPE_OUTRO);

            return builder.withSlotName(actionSlotName);
        }
        else {
            return handleStripeIntroStrate(builder, this.currentMission, this.userProgress.getStripeCount());
        }
    }

    DialogItem.Builder handleWiningEnd(DialogItem.Builder builder) {
        calculateActivityProgress();
        return handleWinDialog(builder);
    }

    private DialogItem.Builder handleMissionOutroState(DialogItem.Builder builder) {

        this.isLeaveMission = true;

        if (this.userProgress.isGameFinished()) {

            logger.debug("Handling " + this.statePhase + ". Moving to " + GAME_OUTRO);

            this.statePhase = GAME_OUTRO;

            builder.addResponse(translate(phraseManager.getValueByKey(GAME_FINISHED_PHRASE)));
        }
        else {

            logger.debug("Handling " + this.statePhase + ". Moving to " + MISSION_INTRO);

            builder.addResponse(translate(phraseManager.getValueByKey(REDIRECT_TO_SELECT_MISSION_PHRASE)));
            builder.addResponse(translate(phraseManager.getValueByKey(SELECT_MISSION_PHRASE)));
        }

        this.userProgress.setMissionFinished(true);
        savePersistentAttributes();

        return builder.withSlotName(actionSlotName);
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

        if (this.userProgress.getFinishedActivities().size() == Activities.values().length) {

            this.userProgress.iterateStripeCount();
            this.userProgress.resetFinishRounds();

            this.activityProgress.setStripeComplete(true);
        }
        savePersistentAttributes();
    }

    private DialogItem.Builder appendReadyToStart(DialogItem.Builder builder) {

        SpeechSettings speechSettings = activityManager.getSpeechForActivityByStripeNumberAtMission(this.currentActivity, this.userProgress.getStripeCount(), this.currentMission);

        builder.addResponse(translate(speechSettings.getReadyToStartPhrase()));

        return builder;
    }

    private DialogItem.Builder handleWinDialog(DialogItem.Builder builder) {

        this.statePhase = WIN;

        updateUserMistakeStory();

        builder.removeLastResponse();

        if (this.activityManager.isActivityCompetition(this.currentActivity)) {
            IngredientReaction randomIngredient = getRandomIngredient();

            String wrongReplyOnIngredient = getWrongReplyOnIngredient(randomIngredient.getIngredient());

            builder
                    .replaceResponse(translate(randomIngredient.getIngredient()))
                    .addResponse(translate(wrongReplyOnIngredient))
                    .withSlotName(actionSlotName)
                    .withReprompt(translate(phraseManager.getValueByKey(WON_REPROMPT_PHRASE)));
        }

        SpeechSettings speechForActivityByStripeNumberAtMission;

        if (this.activityProgress.isStripeComplete()) {
            speechForActivityByStripeNumberAtMission = activityManager.getSpeechForActivityByStripeNumberAtMission(this.currentActivity, this.userProgress.getStripeCount() - 1, this.currentMission);
        }
        else {
            speechForActivityByStripeNumberAtMission = activityManager.getSpeechForActivityByStripeNumberAtMission(this.currentActivity, this.userProgress.getStripeCount(), this.currentMission);
        }

        List<PhraseSettings> outro = speechForActivityByStripeNumberAtMission.getOutro();

        wrapAnyUserResponse(outro, builder, PHASE_1);

        if (this.activityProgress.getMistakesCount() == 0 && !this.userProgress.isPerfectActivity()) {
            this.userProgress.setPerfectActivity(true);
            builder = appendEarnPerfectActivityByLevel(builder);
        }

        if (this.activityProgress.isStripeComplete()) {
            builder = endStripeAfterWin(builder);
        }
        else {
            builder = handleWinState(builder, this.currentMission);
        }

        return builder.withSlotName(actionSlotName);
    }

    @SuppressWarnings("Duplicates")
    private DialogItem.Builder appendEarnPerfectMissionByLevel(DialogItem.Builder builder) {
        switch (this.currentMission) {
            case LOW_MISSION:
                return builder.addResponse(translate(phraseManager.getValueByKey(PERFECT_MISSION_EARN_LOW_PHRASE)));
            case MEDIUM_MISSION:
                return builder.addResponse(translate(phraseManager.getValueByKey(PERFECT_MISSION_EARN_MID_PHRASE)));
            case HIGH_MISSION:
                return builder.addResponse(translate(phraseManager.getValueByKey(PERFECT_MISSION_EARN_HIGH_PHRASE)));
        }
        return builder;
    }

    @SuppressWarnings("Duplicates")
    private DialogItem.Builder appendEarnPerfectStripeByLevel(DialogItem.Builder builder) {
        switch (this.currentMission) {
            case LOW_MISSION:
                return builder.addResponse(translate(phraseManager.getValueByKey(PERFECT_STRIPE_EARN_LOW_PHRASE)));
            case MEDIUM_MISSION:
                return builder.addResponse(translate(phraseManager.getValueByKey(PERFECT_STRIPE_EARN_MID_PHRASE)));
            case HIGH_MISSION:
                return builder.addResponse(translate(phraseManager.getValueByKey(PERFECT_STRIPE_EARN_HIGH_PHRASE)));
        }
        return builder;
    }

    @SuppressWarnings("Duplicates")
    private DialogItem.Builder appendEarnPerfectActivityByLevel(DialogItem.Builder builder) {
        switch (this.currentMission) {
            case LOW_MISSION:
                return builder.addResponse(translate(phraseManager.getValueByKey(PERFECT_ACTIVITY_EARN_LOW_PHRASE)));
            case MEDIUM_MISSION:
                return builder.addResponse(translate(phraseManager.getValueByKey(PERFECT_ACTIVITY_EARN_MID_PHRASE)));
            case HIGH_MISSION:
                return builder.addResponse(translate(phraseManager.getValueByKey(PERFECT_ACTIVITY_EARN_HIGH_PHRASE)));
        }
        return builder;
    }

    DialogItem.Builder getRePromptSuccessDialog(DialogItem.Builder builder) {
        return builder
                .addResponse(translate(this.activityProgress.getPreviousIngredient()))
                .withSlotName(actionSlotName);
    }

    DialogItem.Builder getSuccessDialog(DialogItem.Builder builder) {

        String ingredient = nextIngredient();

        builder.addResponse(translate(ingredient)).withSlotName(actionSlotName);

        if (this.activityManager.isActivityCompetition(this.currentActivity)) {
            return appendMockCompetitionAnswer(builder);
        }

        return builder;
    }

    private DialogItem.Builder appendMockCompetitionAnswer(DialogItem.Builder builder) {

        Speech speech = builder.popLastSpeech();

        IngredientReaction randomIngredient = getRandomIngredient();

        builder.addResponse(translate(randomIngredient.getIngredient()))
                .addResponse(translate(randomIngredient.getUserReply(), this.activityManager.getCompetitionPartnerRole(this.currentActivity)))
                .addResponse(speech);

        return builder;
    }

    DialogItem.Builder getFailureDialog(DialogItem.Builder builder, PhraseSettings speechText) {
        String ingredient = nextIngredient();
        return builder
                .addResponse(translate(speechText))
                .addResponse(translate(ingredient))
                .withSlotName(actionSlotName);
    }

    DialogItem.Builder getLoseRoundDialog(DialogItem.Builder builder, String value) {
        this.statePhase = LOSE;
        return builder
                .addResponse(translate(phraseManager.getValueByKey(value)))
                .addResponse(translate(phraseManager.getValueByKey(FAILURE_PHRASE)))
                .withSlotName(actionSlotName)
                .withReprompt(translate(phraseManager.getValueByKey(FAILURE_REPROMPT_PHRASE)));
    }

    private IngredientReaction getRandomIngredient() {
        return activityManager.getNextIngredient(this.stripe, null);
    }

    private String getWrongReplyOnIngredient(String ingredient) {
        IngredientReaction nextIngredient = activityManager.getNextIngredient(this.stripe, ingredient);
        return nextIngredient.getUserReply();
    }

    private String nextIngredient() {

        IngredientReaction nextIngredient = activityManager.getNextIngredient(this.stripe, this.activityProgress.getPreviousIngredient());

        this.activityProgress.setCurrentIngredientReaction(nextIngredient.getUserReply());
        this.activityProgress.setPreviousIngredient(nextIngredient.getIngredient());

        return nextIngredient.getIngredient();
    }
}
