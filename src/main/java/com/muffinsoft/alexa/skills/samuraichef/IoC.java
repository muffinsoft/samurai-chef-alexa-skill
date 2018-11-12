package com.muffinsoft.alexa.skills.samuraichef;

import com.muffinsoft.alexa.skills.samuraichef.components.SessionStateFabric;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivityManager;
import com.muffinsoft.alexa.skills.samuraichef.content.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.content.MissionManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.AliasManager;
import com.muffinsoft.alexa.skills.samuraichef.content.UserReplyManager;

public class IoC {

    private static final PhraseManager phraseManager;
    private static final CardManager cardManager;
    private static final UserReplyManager userReplyManager;
    private static final ActivityManager activityManager;
    private static final AliasManager aliasManager;
    private static final MissionManager missionManager;
    private static final SessionStateFabric sessionStateFabric;

    static {
        phraseManager = new PhraseManager("phrases/en-US.json");
        cardManager = new CardManager("phrases/cards.json");
        userReplyManager = new UserReplyManager("phrases/replies.json");
        aliasManager = new AliasManager("settings/aliases.json");
        missionManager = new MissionManager("settings/progress.json");
        activityManager = new ActivityManager();
        sessionStateFabric = new SessionStateFabric(phraseManager, activityManager, aliasManager, missionManager);
    }

    public static PhraseManager providePhraseManager() {
        return phraseManager;
    }

    public static ActivityManager provideIngredientsManager() {
        return activityManager;
    }

    public static CardManager provideCardManager() {
        return cardManager;
    }

    public static UserReplyManager provideUserReplyManager() {
        return userReplyManager;
    }

    public static AliasManager provideAliasManager() {
        return aliasManager;
    }

    public static MissionManager provideProgressManager() {
        return missionManager;
    }

    public static SessionStateFabric provideSessionStateFabric() {
        return sessionStateFabric;
    }
}
