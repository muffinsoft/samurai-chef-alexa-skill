package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.SlotName;
import com.muffinsoft.alexa.skills.samuraichef.components.UserReplyComparator;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.HelpPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.RegularPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.ActivityManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.HelpStates;
import com.muffinsoft.alexa.skills.samuraichef.enums.Intents;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;
import com.muffinsoft.alexa.skills.samuraichef.models.ActivityProgress;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseSettings;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.UserProgress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.muffinsoft.alexa.skills.samuraichef.components.VoiceTranslator.translate;
import static com.muffinsoft.alexa.skills.samuraichef.constants.HelpPhraseConstants.HELP_GENERAL_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.HelpPhraseConstants.HELP_LEARN_MORE_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.HelpPhraseConstants.HELP_MISSION_HIGH_DESCRIPTION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.HelpPhraseConstants.HELP_MISSION_LOW_DESCRIPTION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.HelpPhraseConstants.HELP_MISSION_MID_DESCRIPTION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.RETURN_TO_GAME_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.SELECT_MISSION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants.WANT_START_MISSION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.ACTIVITY_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.FINISHED_MISSIONS;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.HELP_STATE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.QUESTION_TIME;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_PROGRESS;
import static com.muffinsoft.alexa.skills.samuraichef.enums.HelpStates.LEARN_MORE_HELP;
import static com.muffinsoft.alexa.skills.samuraichef.enums.HelpStates.PROCEED_GAME;
import static com.muffinsoft.alexa.skills.samuraichef.enums.HelpStates.valueOf;

public class HelpStateManager extends BaseStateManager {

    private static final Logger logger = LogManager.getLogger(HelpStateManager.class);

    private final String userFoodSlotReply;

    private final RegularPhraseManager regularPhraseManager;
    private final HelpPhraseManager helpPhraseManager;
    private final ActivityManager activityManager;

    private ActivityProgress activityProgress;

    private UserProgress userProgress;
    private UserMission currentMission;
    private Activities currentActivity;
    private Intents currentIntent;
    private Set<String> finishedMissions;

    public HelpStateManager(Map<String, Slot> slots, AttributesManager attributesManager, SettingsDependencyContainer settingsDependencyContainer, PhraseDependencyContainer phraseDependencyContainer) {
        super(slots, attributesManager);
        this.regularPhraseManager = phraseDependencyContainer.getRegularPhraseManager();
        this.helpPhraseManager = phraseDependencyContainer.getHelpPhraseManager();
        this.activityManager = settingsDependencyContainer.getActivityManager();
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

        String stringifyMission = String.valueOf(getSessionAttributes().get(CURRENT_MISSION));
        if (stringifyMission != null && !stringifyMission.isEmpty() && !stringifyMission.equals("null")) {
            this.currentMission = UserMission.valueOf(stringifyMission);
        }
        else {
            this.currentMission = null;
        }

        this.currentIntent = Intents.valueOf(String.valueOf(getSessionAttributes().getOrDefault(INTENT, Intents.GAME.name())));

        LinkedHashMap rawUserProgress = (LinkedHashMap) getSessionAttributes().get(USER_PROGRESS);
        this.userProgress = rawUserProgress != null ? mapper.convertValue(rawUserProgress, UserProgress.class) : new UserProgress(this.currentMission, true);

        this.currentActivity = this.userProgress.getCurrentActivity() != null ? Activities.valueOf(this.userProgress.getCurrentActivity()) : null;

        LinkedHashMap rawActivityProgress = (LinkedHashMap) getSessionAttributes().get(ACTIVITY_PROGRESS);
        this.activityProgress = rawActivityProgress != null ? mapper.convertValue(rawActivityProgress, ActivityProgress.class) : new ActivityProgress();

        List<String> finishedMissionArray = (List<String>) getSessionAttributes().getOrDefault(FINISHED_MISSIONS, new ArrayList<String>());
        this.finishedMissions = new HashSet<>(finishedMissionArray);

        logger.debug("Session attributes on the start of handling: " + this.getSessionAttributes().toString());
    }

    @Override
    public DialogItem nextResponse() {

        if (this.currentIntent == Intents.GAME || this.currentIntent == Intents.INITIAL_GREETING) {
            return handleFirstLoopHelp();
        }
        else {
            return handleSecondLoopHelp();
        }
    }

    private DialogItem handleSecondLoopHelp() {

        HelpStates helpState = valueOf(String.valueOf(getSessionAttributes().get(HELP_STATE)));

        DialogItem.Builder builder = DialogItem.builder();

        switch (helpState) {

            case PROCEED_GAME:

                if (UserReplyComparator.compare(getUserReply(), UserReplies.YES)) {
                    getSessionAttributes().put(INTENT, Intents.GAME);
                    getSessionAttributes().remove(HELP_STATE);
                    getSessionAttributes().remove(CURRENT_MISSION);
                    builder.addResponse(translate(regularPhraseManager.getValueByKey(SELECT_MISSION_PHRASE)));
                }
                else {
                    handleProceedGame(builder);
                }

            case LEARN_MORE_HELP:
                if (UserReplyComparator.compare(getUserReply(), UserReplies.YES)) {
                    List<PhraseSettings> missionDescriptionHelp;

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
                        for (PhraseSettings settings : missionDescriptionHelp) {
                            String replace = settings.getContent().replace("#", String.valueOf(stripeCount + 1));
                            settings.setContent(replace);
                        }
                    }

                    builder.addResponse(translate(missionDescriptionHelp));
                    builder.addResponse(translate(regularPhraseManager.getValueByKey(WANT_START_MISSION_PHRASE)));
                }
                else {
                    handleProceedGame(builder);
                }
                break;
        }

        return builder.build();
    }

    private DialogItem handleFirstLoopHelp() {

        getSessionAttributes().put(INTENT, Intents.HELP);

        DialogItem.Builder builder = DialogItem.builder();

        if (this.currentActivity != null && this.currentMission != null) {
            getSessionAttributes().put(HELP_STATE, LEARN_MORE_HELP);
            String key = "help" + this.currentMission.key + "Stripe" + userProgress.getStripeCount() + this.currentActivity.key;
            List<PhraseSettings> activityHelp = helpPhraseManager.getValueByKey(key);
            if (activityHelp == null) {
                throw new IllegalStateException("Can't find help information for key: " + key);
            }
            builder.addResponse(translate(activityHelp));
            builder.addResponse(translate(helpPhraseManager.getValueByKey(HELP_LEARN_MORE_PHRASE)));
        }
        else {
            getSessionAttributes().put(HELP_STATE, PROCEED_GAME);
            // General Help %Game  description%
            builder.addResponse(translate(helpPhraseManager.getValueByKey(HELP_GENERAL_PHRASE)));
            builder.addResponse(translate(regularPhraseManager.getValueByKey(WANT_START_MISSION_PHRASE)));
        }

        getSessionAttributes().put(INTENT, Intents.HELP);
        return builder.build();
    }


//    private DialogItem handleSecondLoopHelp() {
//
//        HelpStates helpState = valueOf(String.valueOf(getSessionAttributes().get(HELP_STATE)));
//
//        DialogItem.Builder builder = DialogItem.builder();
//
//        switch (helpState) {
//
//            case GENERAL_HELP:
//
//                if (UserReplyComparator.compare(getUserReply(), UserReplies.YES)) {
//                    getSessionAttributes().put(INTENT, Intents.GAME);
//                    getSessionAttributes().remove(HELP_STATE);
//                    getSessionAttributes().remove(CURRENT_MISSION);
//                    builder.addResponse(translate(regularPhraseManager.getValueByKey(SELECT_MISSION_PHRASE)));
//                }
//                else {
//                    handleProceedGame(builder);
//                }
//                break;
//
//            case LEARN_MORE_HELP:
//                if (UserReplyComparator.compare(getUserReply(), UserReplies.YES)) {
//                    handleMissionDescription(builder);
//                }
//                else {
//                    handleProceedGame(builder);
//                }
//                break;
//
//            case CONTINUE_PLAYING_HELP:
//                if (UserReplyComparator.compare(getUserReply(), UserReplies.YES)) {
//                    handleProceedGame(builder);
//                }
//                else {
//                    handleGeneralHelp(builder);
//                }
//                break;
//
//            case MORE_DETAILS_HELP:
//                if (UserReplyComparator.compare(getUserReply(), UserReplies.YES)) {
//                    handleActivityDescription(builder);
//                }
//                handleLearnMore(builder);
//                break;
//
//            case COMPETITION_REMINDER_HELP:
//                if (UserReplyComparator.compare(getUserReply(), UserReplies.YES)) {
//                    handleLearnMore(builder);
//                }
//                else {
//                    handleGeneralHelp(builder);
//                }
//                break;
//        }
//
//        return builder.build();
//    }
//
//    private DialogItem handleFirstLoopHelp() {
//
//        getSessionAttributes().put(INTENT, Intents.HELP);
//
//        DialogItem.Builder builder = DialogItem.builder();
//
//        if (this.currentActivity != null) {
//            if (activityManager.isActivityCompetition(this.currentActivity)) {
//                // Competition Rules & Examples
//                builder.addResponse(translate(helpPhraseManager.getValueByKey(HELP_COMPETITION_RULES_PHRASE)));
//                getSessionAttributes().put(HELP_STATE, COMPETITION_REMINDER_HELP);
//            }
//            else {
//                builder.addResponse(translate(helpPhraseManager.getValueByKey(HELP_NEW_WORDS_PHRASE)));
//                if (finishedMissions.isEmpty() && userProgress.getFinishedActivities().isEmpty()) {
//                    handleLearnMore(builder);
//                }
//                else {
//                    getSessionAttributes().put(HELP_STATE, MORE_DETAILS_HELP);
//                    builder.addResponse(translate(helpPhraseManager.getValueByKey(HELP_MORE_DETAILS_PHRASE)));
//                }
//            }
//        }
//        else if (this.currentMission != null) {
//            handleMissionDescription(builder);
//        }
//        else {
//            handleGeneralHelp(builder);
//        }
//
//        getSessionAttributes().put(INTENT, Intents.HELP);
//        return builder.build();
//    }


//    private void handleLearnMore(DialogItem.Builder builder) {
//        getSessionAttributes().put(HELP_STATE, LEARN_MORE_HELP);
//        builder.addResponse(translate(helpPhraseManager.getValueByKey(HELP_LEARN_MORE_PHRASE)));
//    }

//    private void handleActivityDescription(DialogItem.Builder builder) {
//
//        if (this.currentMission == null || this.currentActivity == null) {
//            throw new IllegalStateException("Can't find help for mission " + this.currentMission + " and activity " + this.currentActivity);
//        }
//
//        String key = "help" + this.currentMission.key + "Stripe" + userProgress.getStripeCount() + this.currentActivity.key;
//
//        builder.addResponse(translate(regularPhraseManager.getValueByKey(key)));
//    }

//    private void handleMissionDescription(DialogItem.Builder builder) {
//        // Mission specific goal description
//        // Current Star progress
//        //  Do you want to continue playing?
//        List<PhraseSettings> missionDescriptionHelp;
//
//        if (currentMission == UserMission.LOW_MISSION) {
//            missionDescriptionHelp = helpPhraseManager.getValueByKey(HELP_MISSION_LOW_DESCRIPTION_PHRASE);
//
//        }
//        else if (currentMission == UserMission.MEDIUM_MISSION) {
//            missionDescriptionHelp = helpPhraseManager.getValueByKey(HELP_MISSION_MID_DESCRIPTION_PHRASE);
//        }
//        else {
//            missionDescriptionHelp = helpPhraseManager.getValueByKey(HELP_MISSION_HIGH_DESCRIPTION_PHRASE);
//        }
//
//        if (this.userProgress != null) {
//            int stripeCount = this.userProgress.getStripeCount();
//            for (PhraseSettings settings : missionDescriptionHelp) {
//                String replace = settings.getContent().replace("#", String.valueOf(stripeCount + 1));
//                settings.setContent(replace);
//            }
//        }
//
//        builder.addResponse(translate(missionDescriptionHelp));
//
//        getSessionAttributes().put(INTENT, Intents.GAME);
//        getSessionAttributes().remove(HELP_STATE);
//        getSessionAttributes().remove(CURRENT_MISSION);
//        builder.addResponse(translate(regularPhraseManager.getValueByKey(SELECT_MISSION_PHRASE)));
//    }

    private void handleProceedGame(DialogItem.Builder builder) {
        getSessionAttributes().put(INTENT, Intents.GAME);
        getSessionAttributes().remove(HELP_STATE);
        builder.addResponse(translate(regularPhraseManager.getValueByKey(RETURN_TO_GAME_PHRASE)));
        if (activityProgress != null && activityProgress.getPreviousIngredient() != null) {
            builder.addResponse(translate(activityProgress.getPreviousIngredient()));
        }
        getSessionAttributes().put(QUESTION_TIME, System.currentTimeMillis());
        getSessionAttributes().remove(HELP_STATE);
    }

//    private void handleGeneralHelp(DialogItem.Builder builder) {
//        getSessionAttributes().put(HELP_STATE, GENERAL_HELP);
//        // General Help %Game  description%
//        builder.addResponse(translate(helpPhraseManager.getValueByKey(HELP_GENERAL_PHRASE)));
//        builder.addResponse(translate(regularPhraseManager.getValueByKey(WANT_START_MISSION_PHRASE)));
//    }
}
