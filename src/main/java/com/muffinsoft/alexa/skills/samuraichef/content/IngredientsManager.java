package com.muffinsoft.alexa.skills.samuraichef.content;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class IngredientsManager {

    private static final Logger logger = LoggerFactory.getLogger(IngredientsManager.class);

    private static final String PATH = "phrases/ingredients.json";

    private static Map<String, String> ingredients;

    static {
        File file = new File(PATH);
        try {
            ingredients = new ObjectMapper().readValue(file, new TypeReference<HashMap<String, String>>() {
            });
        }
        catch (IOException e) {
            logger.error("Exception", e);
        }
    }

    public static String getIngredient() {

        ThreadLocalRandom random = ThreadLocalRandom.current();

        List<String> ingredientsList = new ArrayList<>(ingredients.keySet());

        int nextIngredient = random.nextInt(ingredients.size());

        return ingredientsList.get(nextIngredient);
    }

    public static String getIngredientResponse(String key) {
        return ingredients.get(key);
    }
}
