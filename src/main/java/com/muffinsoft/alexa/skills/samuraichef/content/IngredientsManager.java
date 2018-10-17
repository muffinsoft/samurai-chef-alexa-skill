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

        Map<String, String> ingredients = getValueByKey(activity.name());

        List<String> ingredientsList = new ArrayList<>(ingredients.keySet());

        if (previousIngredients.isEmpty()) {
            return getRandomIngredientFromList(activity, ingredientsList);
        }
        else {
            HashSet<String> uniqueIngredients = new HashSet<>(previousIngredients);
            if (previousIngredients.size() - uniqueIngredients.size() >= 2) {

                List<String> updateIngredientList = ingredientsList.stream()
                        .filter(ingredient -> !uniqueIngredients.contains(ingredient))
                        .collect(Collectors.toList());

                return getRandomIngredientFromList(activity, updateIngredientList);
            }
            else {
                return getRandomIngredientFromList(activity, ingredientsList);
            }
        }
    }

    private String getRandomIngredientFromList(Activities activity, List<String> ingredients) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int nextIngredient = random.nextInt(getValueByKey(activity.name()).size());
        return ingredients.get(nextIngredient);
    }
}
