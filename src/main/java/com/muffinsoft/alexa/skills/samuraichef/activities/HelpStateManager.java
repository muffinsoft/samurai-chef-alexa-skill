package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.enums.IntentType;
import com.muffinsoft.alexa.sdk.model.BasePhraseContainer;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.PhraseContainer;
import com.muffinsoft.alexa.sdk.model.SlotName;
import com.muffinsoft.alexa.skills.samuraichef.components.BeltColorDefiner;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.ActivityPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.HelpPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.RegularPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.AplManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.HelpStates;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;
import com.muffinsoft.alexa.skills.samuraichef.models.ActivityProgress;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SpeechSettings;
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.components.UserReplyComparator.compare;
import static com.muffinsoft.alexa.skills.samuraichef.constants.HelpPhraseConstants.HELP_GENERAL_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.HelpPhraseConstants.HELP_LEARN_MORE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.HelpPhraseConstants.HELP_MISSION_HIGH_DESCRIPTION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.HelpPhraseConstants.HELP_MISSION_LOW_DESCRIPTION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.HelpPhraseConstants.HELP_MISSION_MID_DESCRIPTION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.HelpPhraseConstants.HELP_REPEAT_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.READY_TO_PLAY_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.RETURN_TO_GAME_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.HELP_STATE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.PREVIOUS_INTENT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.QUESTION_TIME;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.enums.HelpStates.LEARN_MORE_HELP;
import static com.muffinsoft.alexa.skills.samuraichef.enums.HelpStates.PROCEED_GAME;
import static com.muffinsoft.alexa.skills.samuraichef.enums.HelpStates.REPEAT_ACTIVITY_HELP;
import static com.muffinsoft.alexa.skills.samuraichef.enums.HelpStates.valueOf;

public class HelpStateManager extends BaseStateManager {

    private static final Logger logger = LogManager.getLogger(HelpStateManager.class);

    private final RegularPhraseManager regularPhraseManager;
    private final HelpPhraseManager helpPhraseManager;
    private final ActivityPhraseManager activityPhraseManager;
    private final AplManager aplManager;

    private ActivityProgress activityProgress;

    private UserProgress userProgress;
    private UserMission currentMission;
    private Activities currentActivity;
    private IntentType currentIntent;

    public HelpStateManager(Map<String, Slot> slots, AttributesManager attributesManager, SettingsDependencyContainer settingsDependencyContainer, PhraseDependencyContainer phraseDependencyContainer) {
        super(slots, attributesManager, settingsDependencyContainer.getDialogTranslator());
        this.regularPhraseManager = phraseDependencyContainer.getRegularPhraseManager();
        this.helpPhraseManager = phraseDependencyContainer.getHelpPhraseManager();
        this.activityPhraseManager = phraseDependencyContainer.getActivityPhraseManager();
        this.aplManager = settingsDependencyContainer.getAplManager();
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

        this.currentIntent = IntentType.valueOf(String.valueOf(getSessionAttributes().getOrDefault(INTENT, IntentType.GAME.name())));

        LinkedHashMap rawUserProgress = (LinkedHashMap) getSessionAttributes().get(USER_PROGRESS);
        this.userProgress = rawUserProgress != null ? mapper.convertValue(rawUserProgress, UserProgress.class) : new UserProgress(this.currentMission, true);

        this.currentActivity = this.userProgress.getCurrentActivity() != null ? Activities.valueOf(this.userProgress.getCurrentActivity()) : null;

        LinkedHashMap rawActivityProgress = (LinkedHashMap) getSessionAttributes().get(ACTIVITY_PROGRESS);
        this.activityProgress = rawActivityProgress != null ? mapper.convertValue(rawActivityProgress, ActivityProgress.class) : new ActivityProgress();

        logger.debug("Session attributes on the start of handling: " + this.getSessionAttributes().toString());
    }

    @Override
    public DialogItem nextResponse() {

        if (this.currentIntent == IntentType.HELP) {
            return handleSecondLoopHelp();
        }
        else {
            return handleFirstLoopHelp();
        }
    }

    private DialogItem handleSecondLoopHelp() {

        HelpStates helpState = valueOf(String.valueOf(getSessionAttributes().get(HELP_STATE)));

        DialogItem.Builder builder = DialogItem.builder();

        switch (helpState) {

            case REPEAT_ACTIVITY_HELP:
                if (compare(getUserReply(SlotName.CONFIRMATION), UserReplies.YES)) {
                    handleActivityHelp(builder);
                }
                else {
                    getSessionAttributes().put(HELP_STATE, LEARN_MORE_HELP);
                    builder.addResponse(getDialogTranslator().translate(helpPhraseManager.getValueByKey(HELP_LEARN_MORE_PHRASE)));
                    getSessionAttributes().put(INTENT, IntentType.HELP);
                }
                break;

            case PROCEED_GAME:

                if (compare(getUserReply(SlotName.CONFIRMATION), UserReplies.YES)) {
                    handleProceedGame(builder);
                }
                else {
                    getSessionAttributes().put(HELP_STATE, PROCEED_GAME);
                    builder.addResponse(getDialogTranslator().translate(helpPhraseManager.getValueByKey(HELP_GENERAL_PHRASE)));
                    builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(READY_TO_PLAY_PHRASE)));
                    getSessionAttributes().put(INTENT, IntentType.HELP);
                }
                break;

            case LEARN_MORE_HELP:
                if (compare(getUserReply(SlotName.CONFIRMATION), UserReplies.YES)) {
                    List<PhraseContainer> missionDescriptionHelp;

                    if (currentMission == UserMission.LOW_MISSION) {
                        missionDescriptionHelp = helpPhraseManager.getValueByKey(HELP_MISSION_LOW_DESCRIPTION_PHRASE);

                    }
                    else if (currentMission == UserMission.MEDIUM_MISSION) {
                        missionDescriptionHelp = helpPhraseManager.getValueByKey(HELP_MISSION_MID_DESCRIPTION_PHRASE);
                    }
                    else {
                        missionDescriptionHelp = helpPhraseManager.getValueByKey(HELP_MISSION_HIGH_DESCRIPTION_PHRASE);
                    }

                    if (this.userProgress != null) {
                        int stripeCount = this.userProgress.getStripeCount();
                        for (PhraseContainer settings : missionDescriptionHelp) {
                            String replace = settings.getContent().replace("#", getColorByStripe(stripeCount));
                            ((BasePhraseContainer) settings).setContent(replace);
                        }
                    }

                    builder.addResponse(getDialogTranslator().translate(missionDescriptionHelp));
                    getSessionAttributes().put(HELP_STATE, PROCEED_GAME);
                    builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(READY_TO_PLAY_PHRASE)));
                }
                else {
                    handleProceedGame(builder);
                }
                break;
        }

        return builder.build();
    }

    private void handleActivityHelp(DialogItem.Builder builder) {
        getSessionAttributes().put(HELP_STATE, REPEAT_ACTIVITY_HELP);
        String key = "help" + this.currentMission.key + "Stripe" + userProgress.getStripeCount() + this.currentActivity.key;
        List<PhraseContainer> activityHelp = helpPhraseManager.getValueByKey(key);
        builder.addResponse(getDialogTranslator().translate(activityHelp));
        builder.addResponse(getDialogTranslator().translate(helpPhraseManager.getValueByKey(HELP_REPEAT_PHRASE)));
        builder.withAplDocument(aplManager.getContainer());
        SpeechSettings settings = activityPhraseManager.getSpeechForActivityByStripeNumberAtMission(this.currentActivity, this.userProgress.getStripeCount(), this.currentMission);
        builder.addBackgroundImageUrl(settings.getInstructionImageUrl());
    }

    private String getColorByStripe(int stripe) {
        return BeltColorDefiner.defineColor(stripe);
    }

    private DialogItem handleFirstLoopHelp() {

        DialogItem.Builder builder = DialogItem.builder();

        if (this.currentActivity != null && this.currentMission != null) {
            handleActivityHelp(builder);
        }
        else {
            getSessionAttributes().put(HELP_STATE, PROCEED_GAME);
            builder.addResponse(getDialogTranslator().translate(helpPhraseManager.getValueByKey(HELP_GENERAL_PHRASE)));
            builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(READY_TO_PLAY_PHRASE)));
        }

        getSessionAttributes().put(PREVIOUS_INTENT, this.currentIntent);
        getSessionAttributes().put(INTENT, IntentType.HELP);
        return builder.build();
    }

    private void handleProceedGame(DialogItem.Builder builder) {
        getSessionAttributes().put(INTENT, IntentType.GAME);
        getSessionAttributes().remove(HELP_STATE);
        builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(RETURN_TO_GAME_PHRASE)));
        if (activityProgress != null && activityProgress.getPreviousIngredient() != null) {
            String ingredient = activityProgress.getPreviousIngredient();
            builder.addResponse(getDialogTranslator().translate(ingredient));
            builder.withAplDocument(aplManager.getContainer());
            builder.addBackgroundImageUrl(getBackgroundImageUrl(ingredient));
        }
        else {
            builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(READY_TO_PLAY_PHRASE)));
        }
        getSessionAttributes().put(QUESTION_TIME, System.currentTimeMillis());
        getSessionAttributes().remove(HELP_STATE);
    }

    String getBackgroundImageUrl(String ingredient) {
        String url = "https://s3.amazonaws.com/samurai-audio/images/{size}/icons/" + ingredient + ".jpg";
        logger.info("Going to load icon by url: " + url);
        return url;
    }
}
