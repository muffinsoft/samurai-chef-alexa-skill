package com.muffinsoft.alexa.skills.samuraichef;

import com.muffinsoft.alexa.skills.samuraichef.components.SessionStateFabric;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.ActivityPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.GreetingsPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.HelpPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.MissionPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.RegularPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.ActivityManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.AliasManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.MissionManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.UserReplyManager;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;

public class IoC {

    private static final RegularPhraseManager regularPhraseManager;
    private static final HelpPhraseManager helpPhraseManager;
    private static final ActivityPhraseManager activityPhraseManager;
    private static final GreetingsPhraseManager greetingsPhraseManager;
    private static final MissionPhraseManager missionPhraseManager;
    private static final CardManager cardManager;
    private static final UserReplyManager userReplyManager;
    private static final ActivityManager activityManager;
    private static final AliasManager aliasManager;
    private static final MissionManager missionManager;
    private static final SessionStateFabric sessionStateFabric;
    private static final SettingsDependencyContainer settingsDependencyContainer;
    private static final PhraseDependencyContainer phraseDependencyContainer;

    static {
        regularPhraseManager = new RegularPhraseManager("phrases/en-US.json");
        cardManager = new CardManager("phrases/cards.json");
        userReplyManager = new UserReplyManager("settings/replies.json");
        aliasManager = new AliasManager("settings/aliases.json");
        missionManager = new MissionManager("settings/progress.json");
        greetingsPhraseManager = new GreetingsPhraseManager("phrases/greetings.json");
        helpPhraseManager = new HelpPhraseManager("phrases/help.json");
        missionPhraseManager = new MissionPhraseManager("phrases/mission-phrases.json");
        activityManager = new ActivityManager();
        activityPhraseManager = new ActivityPhraseManager();
        settingsDependencyContainer = new SettingsDependencyContainer(cardManager, activityManager, aliasManager, missionManager);
        phraseDependencyContainer = new PhraseDependencyContainer(regularPhraseManager, activityPhraseManager, helpPhraseManager, greetingsPhraseManager, missionPhraseManager);
        sessionStateFabric = new SessionStateFabric(settingsDependencyContainer, phraseDependencyContainer);
    }

    public static GreetingsPhraseManager provideGreetingsManager() {
        return greetingsPhraseManager;
    }

    public static UserReplyManager provideUserReplyManager() {
        return userReplyManager;
    }

    public static SettingsDependencyContainer provideSettingsDependencies() {
        return settingsDependencyContainer;
    }

    public static PhraseDependencyContainer providePhraseDependencies() {
        return phraseDependencyContainer;
    }

    public static SessionStateFabric provideSessionStateFabric() {
        return sessionStateFabric;
    }
}
