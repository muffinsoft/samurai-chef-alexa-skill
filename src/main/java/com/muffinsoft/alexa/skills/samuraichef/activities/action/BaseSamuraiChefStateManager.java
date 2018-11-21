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

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.muffinsoft.alexa.sdk.model.Speech.ofAlexa;
import static com.muffinsoft.alexa.sdk.model.Speech.ofIvy;
import static com.muffinsoft.alexa.skills.samuraichef.components.VoiseTranslator.translate;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.FAILURE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.FAILURE_REPROMPT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.GAME_FINISHED_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.MISSION_ALREADY_COMPLETE_PHRASE;
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

    protected final String foodSlotName = SlotName.AMAZON_FOOD.text;

    protected final PhraseManager phraseManager;
    protected final ActivityManager activityManager;
    final AliasManager aliasManager;
    final MissionManager missionManager;
    private final String userFoodSlotReply;
    protected Activities currentActivity;
    protected Stripe stripe;
    protected ActivityProgress activityProgress;
    StatePhase statePhase;
    private UserProgress userProgress;
    private UserMission currentMission;
    private boolean isLeaveMission = false;
    private boolean isMoveToReset = false;
    private int starCount;
    private Integer userReplyBreakpointPosition;

    BaseSamuraiChefStateManager(Map<String, Slot> slots, AttributesManager attributesManager, ConfigContainer configContainer) {
        super(slots, attributesManager);
        this.phraseManager = configContainer.getPhraseManager();
        this.activityManager = configContainer.getActivityManager();
        this.aliasManager = configContainer.getAliasManager();
        this.missionManager = configContainer.getMissionManager();
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

        currentMission = UserMission.valueOf(String.valueOf(getSessionAttributes().get(CURRENT_MISSION)));

        this.userReplyBreakpointPosition = (Integer) this.getSessionAttributes().getOrDefault(USER_REPLY_BREAKPOINT, null);

        statePhase = StatePhase.valueOf(String.valueOf(getSessionAttributes().getOrDefault(STATE_PHASE, MISSION_INTRO)));

        LinkedHashMap rawUserProgress = (LinkedHashMap) getSessionAttributes().get(USER_PROGRESS);
        userProgress = rawUserProgress != null ? mapper.convertValue(rawUserProgress, UserProgress.class) : new UserProgress(this.currentMission, true);

        starCount = (int) getSessionAttributes().getOrDefault(STAR_COUNT, 0);

        LinkedHashMap rawActivityProgress = (LinkedHashMap) getSessionAttributes().get(ACTIVITY_PROGRESS);
        activityProgress = rawActivityProgress != null ? mapper.convertValue(rawActivityProgress, ActivityProgress.class) : new ActivityProgress();

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
            updateMissionCompleteInAllLevels();
            logger.debug("Was updated completed missions in all missions");
        }

        logger.debug("Persistent attributes on the end of handling: " + this.getPersistentAttributes().toString());
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

    private void updateMissionCompleteInAllLevels() {
        if (this.currentMission == UserMission.LOW_MISSION) {
            updateMissionCompleteInForLevel(USER_MID_PROGRESS_DB);
            updateMissionCompleteInForLevel(USER_HIGH_PROGRESS_DB);
        }
        else if (this.currentMission == UserMission.MEDIUM_MISSION) {
            updateMissionCompleteInForLevel(USER_LOW_PROGRESS_DB);
            updateMissionCompleteInForLevel(USER_HIGH_PROGRESS_DB);
        }
        else {
            updateMissionCompleteInForLevel(USER_LOW_PROGRESS_DB);
            updateMissionCompleteInForLevel(USER_MID_PROGRESS_DB);
        }
    }

    private void updateMissionCompleteInForLevel(String value) {
        try {
            UserProgress missionUserProgress = null;
            if (getPersistentAttributes().containsKey(value)) {
                String jsonInString = String.valueOf(getPersistentAttributes().get(value));
                LinkedHashMap rawUserProgress = mapper.readValue(jsonInString, LinkedHashMap.class);
                missionUserProgress = mapper.convertValue(rawUserProgress, UserProgress.class);
            }
            if (missionUserProgress == null) {
                missionUserProgress = new UserProgress(this.currentMission);
            }
            missionUserProgress.addFinishedMission(this.currentMission.name());
            getPersistentAttributes().put(value, mapper.writeValueAsString(missionUserProgress));
        }
        catch (IOException e) {
            throw new IllegalStateException("Exception while updating Star Count in Persistent Attributes", e);
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
                .addResponse(ofAlexa(phraseManager.getValueByKey(MISSION_ALREADY_COMPLETE_PHRASE)))
                .addResponse(ofAlexa(phraseManager.getValueByKey(WANT_RESET_PROGRESS_PHRASE)))
                .build();
    }

    private DialogItem handleMultipleResponses(DialogItem.Builder builder) {

        return builder.withSlotName(actionSlotName)
                .addResponse(ofAlexa(phraseManager.getValueByKey(SEVERAL_VALUES_PHRASE)))
                .addResponse(ofAlexa(String.join(", ", this.getUserMultipleReplies())))
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

            builder.addResponse(ofAlexa(demoSpeechSettings.getShouldRunDemoPhrase()));
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

            builder.addResponse(ofAlexa(phraseManager.getValueByKey(READY_TO_START_PHRASE)));
            builder.withReprompt(ofAlexa(phraseManager.getValueByKey(READY_TO_START_REPROMPT_PHRASE)));
        }
        else {

            SpeechSettings speechSettings = activityManager.getSpeechForActivityByStripeNumberAtMission(this.currentActivity, this.userProgress.getStripeCount(), this.currentMission);

            wrapAnyUserResponse(speechSettings.getDemo(), builder, DEMO);

            builder = appendReadyToStart(builder);

            builder.withReprompt(ofAlexa(phraseManager.getValueByKey(READY_TO_START_REPROMPT_PHRASE)));
        }

        return builder.withSlotName(actionSlotName);
    }

    private DialogItem.Builder handleReadyToStartState(DialogItem.Builder builder) {

        String speechText = nextIngredient();

        logger.debug("Handling " + this.statePhase + ". Moving to " + PHASE_1);

        this.statePhase = PHASE_1;

        return builder.addResponse(ofAlexa(speechText)).withSlotName(actionSlotName);
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

        List<PhraseSettings> dialog = missionManager.getStripeOutroByMission(currentMission, number);

        wrapAnyUserResponse(dialog, builder, WIN);

        return builder.withSlotName(actionSlotName);
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

        return builder.withSlotName(actionSlotName).addResponse(ofAlexa(phraseManager.getValueByKey(SELECT_MISSION_PHRASE)));
    }

    private DialogItem.Builder handleStripeOutroState(DialogItem.Builder builder, UserMission currentMission) {

        calculateStripeProgress();

        if (this.activityProgress.isMissionFinished()) {

            logger.debug("Handling " + this.statePhase + ". Moving to " + MISSION_OUTRO);

            this.statePhase = MISSION_OUTRO;

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
        return getWinDialog(builder);
    }

    private DialogItem.Builder handleMissionOutroState(DialogItem.Builder builder) {

        this.isLeaveMission = true;

        if (this.userProgress.isGameFinished()) {

            logger.debug("Handling " + this.statePhase + ". Moving to " + GAME_OUTRO);

            this.statePhase = GAME_OUTRO;

            builder.addResponse(ofAlexa(phraseManager.getValueByKey(GAME_FINISHED_PHRASE)));
        }
        else {

            logger.debug("Handling " + this.statePhase + ". Moving to " + MISSION_INTRO);

            builder.addResponse(ofAlexa(phraseManager.getValueByKey(REDIRECT_TO_SELECT_MISSION_PHRASE)));
            builder.addResponse(ofAlexa(phraseManager.getValueByKey(SELECT_MISSION_PHRASE)));
        }

        this.userProgress.setMissionFinished(true);
        savePersistentAttributes();

        return builder.withSlotName(actionSlotName);
    }

    private void calculateStripeProgress() {

        if (this.userProgress.getStripeCount() == missionManager.getContainer().getStripesAtMissionCount()) {
            this.userProgress.addFinishedMission(this.currentMission.name());
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

        builder.addResponse(ofAlexa(speechSettings.getReadyToStartPhrase()));

        return builder;
    }

    DialogItem.Builder getWinDialog(DialogItem.Builder builder) {

        this.statePhase = WIN;

        SpeechSettings speechForActivityByStripeNumberAtMission = activityManager.getSpeechForActivityByStripeNumberAtMission(this.currentActivity, this.userProgress.getStripeCount(), this.currentMission);

        builder.removeLastResponse();

        List<PhraseSettings> outro = speechForActivityByStripeNumberAtMission.getOutro();

        wrapAnyUserResponse(outro, builder, PHASE_1);

        return builder
                .withSlotName(actionSlotName)
                .withReprompt(ofAlexa(phraseManager.getValueByKey(WON_REPROMPT_PHRASE)));
    }

    DialogItem.Builder getRePromptSuccessDialog(DialogItem.Builder builder) {
        return builder
                .addResponse(ofAlexa(phraseManager.getValueByKey(TRY_AGAIN_PHRASE)))
                .addResponse(ofAlexa(this.activityProgress.getPreviousIngredient()))
                .withSlotName(actionSlotName);
    }

    DialogItem.Builder getSuccessDialog(DialogItem.Builder builder) {
        String ingredient = nextIngredient();
        builder.addResponse(ofAlexa(ingredient)).withSlotName(actionSlotName);

        if (this.activityManager.isActivityCompetition(this.currentActivity)) {
            return appendMockCompetitionAnswer(builder);
        }

        return builder;
    }

    private DialogItem.Builder appendMockCompetitionAnswer(DialogItem.Builder builder) {

        Speech speech = builder.popLastSpeech();

        IngredientReaction randomIngredient = getRandomIngredient();

        builder.addResponse(ofAlexa(randomIngredient.getIngredient()))
                .addResponse(ofIvy(randomIngredient.getUserReply()))
                .addResponse(speech);

        return builder;
    }

    DialogItem.Builder getFailureDialog(DialogItem.Builder builder, String speechText) {
        String ingredient = nextIngredient();
        return builder
                .addResponse(ofAlexa(speechText))
                .addResponse(ofAlexa(ingredient))
                .withSlotName(actionSlotName);
    }

    DialogItem.Builder getLoseRoundDialog(DialogItem.Builder builder, String value) {
        this.statePhase = LOSE;
        return builder
                .addResponse(ofAlexa(phraseManager.getValueByKey(value)))
                .addResponse(ofAlexa(phraseManager.getValueByKey(FAILURE_PHRASE)))
                .withSlotName(actionSlotName)
                .withReprompt(ofAlexa(phraseManager.getValueByKey(FAILURE_REPROMPT_PHRASE)));
    }

    IngredientReaction getRandomIngredient() {
        return activityManager.getNextIngredient(this.stripe, null);
    }

    String getWrongReplyOnIngredient(String ingredient) {
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
