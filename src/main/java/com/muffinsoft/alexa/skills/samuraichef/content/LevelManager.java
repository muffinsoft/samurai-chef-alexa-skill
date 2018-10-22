package com.muffinsoft.alexa.skills.samuraichef.content;

import com.fasterxml.jackson.core.type.TypeReference;
import com.muffinsoft.alexa.sdk.util.ContentLoader;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.models.IngredientReaction;
import com.muffinsoft.alexa.skills.samuraichef.models.IngredientsByLevel;
import com.muffinsoft.alexa.skills.samuraichef.models.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class LevelManager {

    private static final String SUSHI_SLICE = "settings/sushi-slice.json";
    private static final String JUICE_WARRIOR = "settings/juice-warrior.json";
    private static final String WORD_BOARD_KARATE = "settings/word-board-karate.json";
    private static final String FOOD_TASTER = "settings/food-taster.json";

    private final Map<Activities, IngredientsByLevel> ingredientsByActivity;

    public LevelManager() {

        ingredientsByActivity = new HashMap<>();

        ContentLoader contentLoader = new ContentLoader();

        ingredientsByActivity.put(Activities.SUSHI_SLICE, contentLoader.loadContent(new IngredientsByLevel(), SUSHI_SLICE, new TypeReference<IngredientsByLevel>() {
        }));
        ingredientsByActivity.put(Activities.JUICE_WARRIOR, contentLoader.loadContent(new IngredientsByLevel(), JUICE_WARRIOR, new TypeReference<IngredientsByLevel>() {
        }));
        ingredientsByActivity.put(Activities.WORD_BOARD_KARATE, contentLoader.loadContent(new IngredientsByLevel(), WORD_BOARD_KARATE, new TypeReference<IngredientsByLevel>() {
        }));
        ingredientsByActivity.put(Activities.FOOD_TASTER, contentLoader.loadContent(new IngredientsByLevel(), FOOD_TASTER, new TypeReference<IngredientsByLevel>() {
        }));
    }

    public IngredientReaction getNextIngredient(Level level, String previousIngredient) {

        Map<String, String> ingredientsByActivity = level.getIngredients();

        List<String> ingredientsList = new ArrayList<>(ingredientsByActivity.keySet());

        if (previousIngredient != null) {
            ingredientsList.remove(previousIngredient);
        }

        String ingredient = getRandomIngredientFromList(ingredientsList);

        return new IngredientReaction(ingredient, ingredientsByActivity.get(ingredient));
    }

    private String getRandomIngredientFromList(List<String> ingredients) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int nextIngredient = random.nextInt(ingredients.size());
        return ingredients.get(nextIngredient);
    }

    public Level getLevelForActivity(Activities currentActivity, int level) {

        IngredientsByLevel ingredientsByLevel = ingredientsByActivity.get(currentActivity);

        return ingredientsByLevel.getLevel(level);
    }
}
