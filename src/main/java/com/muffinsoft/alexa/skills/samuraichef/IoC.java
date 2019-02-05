package com.muffinsoft.alexa.skills.samuraichef;

import com.muffinsoft.alexa.sdk.components.BaseDialogTranslator;
import com.muffinsoft.alexa.sdk.components.IntentFactory;
import com.muffinsoft.alexa.skills.samuraichef.components.SamuraiIntentFactory;
import com.muffinsoft.alexa.skills.samuraichef.components.SessionStateFabric;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.ActivityPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.CharactersManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.GreetingsPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.HelpPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.MissionPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.RegularPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.SoundsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.ActivityManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.AliasManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.AplManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.MissionManager;
import com.muffinsoft.alexa.skills.samuraichef.content.settings.UserReplyManager;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;

public class IoC {

    public static final AplManager aplManager;
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
    private static final BaseDialogTranslator dialogTranslator;
    private static final CharactersManager charactersManager;
    private static final SoundsManager soundsManager;
    private static final SessionStateFabric sessionStateFabric;
    private static final SamuraiIntentFactory intentFactory;
    private static final SettingsDependencyContainer settingsDependencyContainer;
    private static final PhraseDependencyContainer phraseDependencyContainer;

    static {
        regularPhraseManager = new RegularPhraseManager("phrases/en-US.json");
        cardManager = new CardManager("phrases/cards.json");
        userReplyManager = new UserReplyManager("settings/replies.json");
        aliasManager = new AliasManager("settings/aliases.json");
        aplManager = new AplManager("settings/apl.json");
        missionManager = new MissionManager("settings/progress.json");
        greetingsPhraseManager = new GreetingsPhraseManager("phrases/greetings.json");
        helpPhraseManager = new HelpPhraseManager("phrases/help.json");
        missionPhraseManager = new MissionPhraseManager("phrases/mission-phrases.json");
        activityManager = new ActivityManager();
        charactersManager = new CharactersManager("phrases/characters.json");
        soundsManager = new SoundsManager("phrases/sounds.json");
        dialogTranslator = new BaseDialogTranslator(charactersManager.getContainer(), soundsManager.getContainer());
        activityPhraseManager = new ActivityPhraseManager();
        settingsDependencyContainer = new SettingsDependencyContainer(cardManager, activityManager, aliasManager, missionManager, aplManager, dialogTranslator);
        phraseDependencyContainer = new PhraseDependencyContainer(regularPhraseManager, activityPhraseManager, helpPhraseManager, greetingsPhraseManager, missionPhraseManager);
        sessionStateFabric = new SessionStateFabric(settingsDependencyContainer, phraseDependencyContainer);
        intentFactory = new SamuraiIntentFactory(settingsDependencyContainer, phraseDependencyContainer, sessionStateFabric);
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

    public static IntentFactory provideIntentFactory() {
        return intentFactory;
    }
}
