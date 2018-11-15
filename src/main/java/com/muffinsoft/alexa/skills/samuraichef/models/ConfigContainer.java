package com.muffinsoft.alexa.skills.samuraichef.models;

import com.muffinsoft.alexa.skills.samuraichef.content.ActivityManager;
import com.muffinsoft.alexa.skills.samuraichef.content.AliasManager;
import com.muffinsoft.alexa.skills.samuraichef.content.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.content.MissionManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.UserReplyManager;

public class ConfigContainer {

    private final PhraseManager phraseManager;
    private final CardManager cardManager;
    private final UserReplyManager userReplyManager;
    private final ActivityManager activityManager;
    private final AliasManager aliasManager;
    private final MissionManager missionManager;

    public ConfigContainer(PhraseManager phraseManager, CardManager cardManager, UserReplyManager userReplyManager, ActivityManager activityManager, AliasManager aliasManager, MissionManager missionManager) {
        this.phraseManager = phraseManager;
        this.cardManager = cardManager;
        this.userReplyManager = userReplyManager;
        this.activityManager = activityManager;
        this.aliasManager = aliasManager;
        this.missionManager = missionManager;
    }

    public PhraseManager getPhraseManager() {
        return phraseManager;
    }

    public CardManager getCardManager() {
        return cardManager;
    }

    public UserReplyManager getUserReplyManager() {
        return userReplyManager;
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
}
