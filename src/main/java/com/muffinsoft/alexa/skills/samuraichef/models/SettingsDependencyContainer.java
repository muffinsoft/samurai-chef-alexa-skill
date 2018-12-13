package com.muffinsoft.alexa.skills.samuraichef.models;

import com.muffinsoft.alexa.sdk.components.DialogTranslator;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.ActivityManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.AliasManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.MissionManager;

public class SettingsDependencyContainer {

    private final CardManager cardManager;
    private final ActivityManager activityManager;
    private final AliasManager aliasManager;
    private final MissionManager missionManager;
    private final DialogTranslator dialogTranslator;

    public SettingsDependencyContainer(CardManager cardManager, ActivityManager activityManager, AliasManager aliasManager, MissionManager missionManager, DialogTranslator dialogTranslator) {
        this.cardManager = cardManager;
        this.activityManager = activityManager;
        this.aliasManager = aliasManager;
        this.missionManager = missionManager;
        this.dialogTranslator = dialogTranslator;
    }

    public CardManager getCardManager() {
        return cardManager;
    }

    public ActivityManager getActivityManager() {
        return activityManager;
    }

    public AliasManager getAliasManager() {
        return aliasManager;
    }

    public MissionManager getMissionManager() {
        return missionManager;
    }

    public DialogTranslator getDialogTranslator() {
        return dialogTranslator;
    }
}
