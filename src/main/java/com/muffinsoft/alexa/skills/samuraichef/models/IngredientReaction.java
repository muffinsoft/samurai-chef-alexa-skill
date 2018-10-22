package com.muffinsoft.alexa.skills.samuraichef.models;

public class IngredientReaction {

    private final String ingredient;
    private final String userReply;

    public IngredientReaction(String ingredient, String userReply) {
        this.ingredient = ingredient;
        this.userReply = userReply;
    }

    public String getIngredient() {
        return ingredient;
    }

    public String getUserReply() {
        return userReply;
    }
}
