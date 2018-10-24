package com.muffinsoft.alexa.skills.samuraichef;

import com.muffinsoft.alexa.skills.samuraichef.content.ActivitiesManager;
import com.muffinsoft.alexa.skills.samuraichef.content.CardManager;
import com.muffinsoft.alexa.skills.samuraichef.content.LevelManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PowerUpsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.UserReplyManager;

public class IoC {

    private static final ActivitiesManager activitiesManager;
    private static final PhraseManager phraseManager;
    private static final CardManager cardManager;
    private static final UserReplyManager userReplyManager;
    private static final LevelManager levelManager;
    private static final PowerUpsManager powerUpsManager;

    static {
        activitiesManager = new ActivitiesManager("settings/activities.json");
        phraseManager = new PhraseManager("phrases/en-US.json");
        cardManager = new CardManager("phrases/cards.json");
        userReplyManager = new UserReplyManager("phrases/replies.json");
        powerUpsManager = new PowerUpsManager("settings/power-ups.json");
        levelManager = new LevelManager();
    }

    public static PhraseManager providePhraseManager() {
        return phraseManager;
    }

    public static ActivitiesManager provideActivitiesManager() {
        return activitiesManager;
    }

    public static LevelManager provideIngredientsManager() {
        return levelManager;
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
}
