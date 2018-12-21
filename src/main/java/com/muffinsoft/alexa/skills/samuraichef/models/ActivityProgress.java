package com.muffinsoft.alexa.skills.samuraichef.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.muffinsoft.alexa.skills.samuraichef.enums.PowerUps;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ActivityProgress {

    private boolean isStripeComplete;
    private boolean isStarUpdated;
    private boolean isMissionFinished;
    private String currentIngredientReaction = "";
    private int successCount = 0;
    private int mistakesCount = 0;
    private int successInRow = 0;
    private String previousIngredient = "";
    private Set<String> existingPowerUps = new HashSet<>();
    private String activePowerUp;

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

    public void reset() {
        this.currentIngredientReaction = "";
        this.successCount = 0;
        this.mistakesCount = 0;
        this.previousIngredient = "";
        this.setStarUpdated(false);
        this.setStripeComplete(false);
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

    public void iterateSuccessInARow() {
        this.successInRow += 1;
    }

    public void resetSuccessInRow() {
        this.successInRow = 0;
    }

    public Set<String> getExistingPowerUps() {
        return existingPowerUps;
    }

    public void setExistingPowerUps(String[] existingPowerUps) {
        this.existingPowerUps = new HashSet<>(Arrays.asList(existingPowerUps));
    }

    public void addPowerUp(PowerUps nextPowerUp) {
        if (activePowerUp == null) {
            this.activePowerUp = nextPowerUp.name();
        }
        this.existingPowerUps.add(nextPowerUp.name());
    }

    public boolean isStripeComplete() {
        return this.isStripeComplete;
    }

    public void setStripeComplete(boolean stripeComplete) {
        this.isStripeComplete = stripeComplete;
    }

    public boolean isMissionFinished() {
        return isMissionFinished;
    }

    public void setMissionFinished(boolean missionFinished) {
        this.isMissionFinished = missionFinished;
    }

    public String getActivePowerUp() {
        return activePowerUp;
    }

    public void setActivePowerUp(String activePowerUp) {
        this.activePowerUp = activePowerUp;
    }

    public boolean isStarUpdated() {
        return isStarUpdated;
    }

    public void setStarUpdated(boolean starUpdated) {
        isStarUpdated = starUpdated;
    }

    @JsonIgnore
    public boolean isPowerUpEquipped() {
        return this.activePowerUp != null && !this.activePowerUp.isEmpty();
    }

    @JsonIgnore
    public void removePowerUp() {
        this.existingPowerUps.remove(this.activePowerUp);
        this.activePowerUp = null;
    }

    @JsonIgnore
    public void equipIfAvailable() {
        if (!existingPowerUps.isEmpty()) {
            String title = String.valueOf(existingPowerUps.toArray()[0]);
            this.activePowerUp = title;
            PowerUps.valueOf(title);
        }
    }
}
