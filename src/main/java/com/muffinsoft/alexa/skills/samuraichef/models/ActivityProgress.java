package com.muffinsoft.alexa.skills.samuraichef.models;

public class ActivityProgress {
    private String currentIngredientReaction = "";
    private int successCount = 0;
    private int mistakesCount = 0;
    private int successInRow = 0;
    private String previousIngredient = "";
    private boolean isJustStripeUp = false;

    public String getCurrentIngredientReaction() {
        return currentIngredientReaction;
    }

    public void setCurrentIngredientReaction(String currentIngredientReaction) {
        this.currentIngredientReaction = currentIngredientReaction;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getMistakesCount() {
        return mistakesCount;
    }

    public void setMistakesCount(int mistakesCount) {
        this.mistakesCount = mistakesCount;
    }

    public String getPreviousIngredient() {
        return previousIngredient;
    }

    public void setPreviousIngredient(String previousIngredient) {
        this.previousIngredient = previousIngredient;
    }

    public boolean isJustStripeUp() {
        return isJustStripeUp;
    }

    public void setJustStripeUp(boolean justStripeUp) {
        isJustStripeUp = justStripeUp;
    }

    public void reset() {
        this.currentIngredientReaction = "";
        this.successCount = 0;
        this.mistakesCount = 0;
        this.previousIngredient = "";
        this.isJustStripeUp = false;
    }

    public void iterateSuccessCount() {
        this.successCount += 1;
    }

    public void iterateMistakeCount() {
        this.mistakesCount += 1;
    }

    public int getSuccessInRow() {
        return successInRow;
    }

    public void setSuccessInRow(int successInRow) {
        this.successInRow = successInRow;
    }
}
