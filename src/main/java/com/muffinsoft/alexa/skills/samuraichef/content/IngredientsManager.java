package com.muffinsoft.alexa.skills.samuraichef.content;

import com.muffinsoft.alexa.sdk.content.BaseContentManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class IngredientsManager extends BaseContentManager<Map<String, String>> {

    public IngredientsManager(String path) {
        super(path);
    }

    public String getNextIngredient(Activities activity, String previousIngredient) {

        Map<String, String> ingredientsByActivity = getValueByKey(activity.name());

        List<String> ingredientsList = new ArrayList<>(ingredientsByActivity.keySet());

        if (previousIngredient != null) {
            ingredientsList.remove(previousIngredient);
        }
        return getRandomIngredientFromList(ingredientsList);
    }

    private String getRandomIngredientFromList(List<String> ingredients) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int nextIngredient = random.nextInt(ingredients.size());
        return ingredients.get(nextIngredient);
    }
}
