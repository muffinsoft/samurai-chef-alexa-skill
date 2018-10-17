package com.muffinsoft.alexa.skills.samuraichef;

import com.muffinsoft.alexa.skills.samuraichef.content.ActivitiesManager;
import com.muffinsoft.alexa.skills.samuraichef.content.IngredientsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;

public class DependenciesContainer {

    private static final IngredientsManager ingredientsManager;
    private static final ActivitiesManager activitiesManager;
    private static final PhraseManager phraseManager;

    static {
        activitiesManager = new ActivitiesManager("settings/activities.json");
        ingredientsManager = new IngredientsManager("phrases/ingredients.json");
        phraseManager = new PhraseManager("phrases/en-US.json");
    }

    public static IngredientsManager provideIngredientsManager() {
        return ingredientsManager;
    }

    public static PhraseManager providePhraseManager() {
        return phraseManager;
    }

    public static ActivitiesManager provideActivitiesManager() {
        return activitiesManager;
    }
}
