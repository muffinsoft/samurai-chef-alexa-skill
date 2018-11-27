package com.muffinsoft.alexa.skills.samuraichef.models;

import com.muffinsoft.alexa.skills.samuraichef.content.phrases.ActivityPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.GreetingsPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.HelpPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.MissionPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.RegularPhraseManager;

public class PhraseDependencyContainer {

    private final RegularPhraseManager regularPhraseManager;
    private final HelpPhraseManager helpPhraseManager;
    private final ActivityPhraseManager activityPhraseManager;
    private final GreetingsPhraseManager greetingsPhraseManager;
    private final MissionPhraseManager missionPhraseManager;

    public PhraseDependencyContainer(RegularPhraseManager regularPhraseManager, ActivityPhraseManager activityPhraseManager, HelpPhraseManager helpPhraseManager, GreetingsPhraseManager greetingsPhraseManager, MissionPhraseManager missionPhraseManager) {
        this.regularPhraseManager = regularPhraseManager;
        this.activityPhraseManager = activityPhraseManager;
        this.helpPhraseManager = helpPhraseManager;
        this.greetingsPhraseManager = greetingsPhraseManager;
        this.missionPhraseManager = missionPhraseManager;
    }

    public HelpPhraseManager getHelpPhraseManager() {
        return helpPhraseManager;
    }

    public ActivityPhraseManager getActivityPhraseManager() {
        return activityPhraseManager;
    }

    public RegularPhraseManager getRegularPhraseManager() {
        return regularPhraseManager;
    }

    public GreetingsPhraseManager getGreetingsPhraseManager() {
        return greetingsPhraseManager;
    }

    public MissionPhraseManager getMissionPhraseManager() {
        return missionPhraseManager;
    }
}
