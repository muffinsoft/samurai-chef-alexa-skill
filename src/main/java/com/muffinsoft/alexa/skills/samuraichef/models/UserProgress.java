package com.muffinsoft.alexa.skills.samuraichef.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UserProgress {

    private Set<String> finishedRounds = new HashSet<>();
    private Set<String> earnedPowerUps = new HashSet<>();
    private int stripeCount = 0;
    private int starCount = 0;
    private int winInARowCount = 0;
    private int currentLevel = 0;
    private String equippedPowerUp = "";

    public Set<String> getFinishedRounds() {
        return finishedRounds;
    }

    public void setFinishedRounds(String[] finishedRounds) {
        this.finishedRounds = new HashSet<>(Arrays.asList(finishedRounds));
    }

    public Set<String> getEarnedPowerUps() {
        return earnedPowerUps;
    }

    public void setEarnedPowerUps(String[] earnedPowerUps) {
        this.earnedPowerUps = new HashSet<>(Arrays.asList(earnedPowerUps));
    }

    public int getStripeCount() {
        return stripeCount;
    }

    public void setStripeCount(int stripeCount) {
        this.stripeCount = stripeCount;
    }

    public int getStarCount() {
        return starCount;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    public int getWinInARowCount() {
        return winInARowCount;
    }

    public void setWinInARowCount(int winInARowCount) {
        this.winInARowCount = winInARowCount;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public void increaseWinInARow() {
        this.winInARowCount += 1;
    }

    public void iterateStripeCount() {
        this.stripeCount += 1;
    }

    public void resetFinishRounds() {
        this.finishedRounds = new HashSet<>();
    }

    public void iterateLevel() {
        this.currentLevel += 1;
    }

    public void iterateStarCount() {
        this.starCount += 1;
    }

    public String getEquippedPowerUp() {
        return equippedPowerUp;
    }

    public void setEquippedPowerUp(String equipment) {
        this.equippedPowerUp = equipment;
    }

    @JsonIgnore
    public boolean isPowerUpEquipped() {
        return equippedPowerUp != null && !equippedPowerUp.isEmpty();
    }

    public void removePowerUp() {
        this.equippedPowerUp = null;
    }

    public void addEquipment(String name) {
        this.earnedPowerUps.add(name);
    }

    public void addFinishedRound(String name) {
        this.finishedRounds.add(name);
    }

    public void equipPowerUp(String equipment) {
        this.earnedPowerUps.remove(equipment);
        this.equippedPowerUp = equipment;
    }
}