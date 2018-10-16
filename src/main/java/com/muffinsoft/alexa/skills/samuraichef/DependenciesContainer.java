package com.muffinsoft.alexa.skills.samuraichef;

import com.muffinsoft.alexa.skills.samuraichef.content.IngredientsManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;

public class DependenciesContainer {

    private static final IngredientsManager ingredientsManager;
    private static final PhraseManager phraseManager;

    static {
        ingredientsManager = new IngredientsManager("phrases/ingredients.json");
        phraseManager = new PhraseManager("phrases/en-US.json");
    }

    static IngredientsManager provideIngredientsManager() {
        return ingredientsManager;
    }

    static PhraseManager providePhraseManager() {
        return phraseManager;
    }
}
