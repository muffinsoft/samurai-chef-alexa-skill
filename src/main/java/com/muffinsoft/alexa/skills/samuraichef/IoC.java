package com.muffinsoft.alexa.skills.samuraichef;

import com.muffinsoft.alexa.skills.samuraichef.components.SessionStateFabric;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivityManager;
import com.muffinsoft.alexa.skills.samuraichef.content.AliasManager;
import com.muffinsoft.alexa.skills.samuraichef.content.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.content.GreetingsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.MissionManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.UserReplyManager;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;

public class IoC {

    private static final PhraseManager phraseManager;
    private static final CardManager cardManager;
    private static final UserReplyManager userReplyManager;
    private static final ActivityManager activityManager;
    private static final AliasManager aliasManager;
    private static final GreetingsManager greetingsManager;
    private static final MissionManager missionManager;
    private static final SessionStateFabric sessionStateFabric;
    private static final ConfigContainer configContainer;

    static {
        phraseManager = new PhraseManager("phrases/en-US.json");
        cardManager = new CardManager("phrases/cards.json");
        userReplyManager = new UserReplyManager("phrases/replies.json");
        aliasManager = new AliasManager("settings/aliases.json");
        missionManager = new MissionManager("settings/progress.json");
        greetingsManager = new GreetingsManager("phrases/greetings.json");
        activityManager = new ActivityManager();
        configContainer = new ConfigContainer(phraseManager, cardManager, userReplyManager, activityManager, aliasManager, missionManager, greetingsManager);
        sessionStateFabric = new SessionStateFabric(configContainer);
    }

    public static GreetingsManager provideGreetingsManager() {
        return greetingsManager;
    }

    public static UserReplyManager provideUserReplyManager() {
        return userReplyManager;
    }

    public static ConfigContainer provideConfigurationContainer() {
        return configContainer;
    }

    public static SessionStateFabric provideSessionStateFabric() {
        return sessionStateFabric;
    }
}
