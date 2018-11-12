package com.muffinsoft.alexa.skills.samuraichef;

import com.muffinsoft.alexa.skills.samuraichef.components.SessionStateFabric;
import com.muffinsoft.alexa.skills.samuraichef.content.ActivityManager;
import com.muffinsoft.alexa.skills.samuraichef.content.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.content.MissionManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.UserReplyManager;

public class IoC {

    private static final PhraseManager phraseManager;
    private static final CardManager cardManager;
    private static final UserReplyManager userReplyManager;
    private static final ActivityManager ACTIVITY_MANAGER;
    private static final PowerUpsManager powerUpsManager;
    private static final MissionManager MISSION_MANAGER;
    private static final SessionStateFabric sessionStateFabric;

    static {
        phraseManager = new PhraseManager("phrases/en-US.json");
        cardManager = new CardManager("phrases/cards.json");
        userReplyManager = new UserReplyManager("phrases/replies.json");
        powerUpsManager = new PowerUpsManager("settings/power-ups.json");
        MISSION_MANAGER = new MissionManager("settings/progress.json");
        ACTIVITY_MANAGER = new ActivityManager();
        sessionStateFabric = new SessionStateFabric(phraseManager, ACTIVITY_MANAGER, powerUpsManager, MISSION_MANAGER);
    }

    public static PhraseManager providePhraseManager() {
        return phraseManager;
    }

    public static ActivityManager provideIngredientsManager() {
        return ACTIVITY_MANAGER;
    }

    public static CardManager provideCardManager() {
        return cardManager;
    }

    public static UserReplyManager provideUserReplyManager() {
        return userReplyManager;
    }

    public static PowerUpsManager providePowerUpsManager() {
        return powerUpsManager;
    }

    public static MissionManager provideProgressManager() {
        return MISSION_MANAGER;
    }

    public static SessionStateFabric provideSessionStateFabric() {
        return sessionStateFabric;
    }
}
