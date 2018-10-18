package com.muffinsoft.alexa.skills.samuraichef.content;

import com.muffinsoft.alexa.sdk.content.BaseContentManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class IngredientsManager extends BaseContentManager<Map<String, String>> {

    public IngredientsManager(String path) {
        super(path);
    }

    public String getNextIngredient(Activities activity, LinkedList<String> previousIngredients) {

        Map<String, String> ingredientsByActivity = getValueByKey(activity.name());

        List<String> ingredientsList = new ArrayList<>(ingredientsByActivity.keySet());

        if (previousIngredients.isEmpty()) {
            return getRandomIngredientFromList(ingredientsList);
        }
        else {
            HashSet<String> uniqueIngredients = new HashSet<>(previousIngredients);
            if (previousIngredients.size() - uniqueIngredients.size() >= 1) {

                List<String> updateIngredientList = ingredientsList.stream()
                        .filter(ingredient -> !uniqueIngredients.contains(ingredient))
                        .collect(Collectors.toList());

                return getRandomIngredientFromList(updateIngredientList);
            }
            else {
                return getRandomIngredientFromList(ingredientsList);
            }
        }
    }

    private String getRandomIngredientFromList(List<String> ingredients) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int nextIngredient = random.nextInt(ingredients.size());
        return ingredients.get(nextIngredient);
    }
}
