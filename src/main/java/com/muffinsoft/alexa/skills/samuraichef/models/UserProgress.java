package com.muffinsoft.alexa.skills.samuraichef.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UserProgress {

    private Set<String> finishedActivities = new HashSet<>();
    private Set<String> finishedMissions = new HashSet<>();
    private int stripeCount = 0;
    private int starCount = 0;
    private String lastActivity;
    private boolean justCreated = false;

    public UserProgress() {
    }

    public UserProgress(boolean isNew) {
        this.justCreated = isNew;
    }

    public Set<String> getFinishedMissions() {
        return finishedMissions;
    }

    public void setFinishedMissions(String[] finishedMissions) {
        this.finishedMissions = new HashSet<>(Arrays.asList(finishedMissions));
    }

    public Set<String> getFinishedActivities() {
        return finishedActivities;
    }

    public void setFinishedActivities(String[] finishedActivities) {
        this.finishedActivities = new HashSet<>(Arrays.asList(finishedActivities));
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

    public void iterateStripeCount() {
        this.justCreated = false;
        this.stripeCount += 1;
    }

    public void resetFinishRounds() {
        this.justCreated = false;
        this.finishedActivities = new HashSet<>();
    }

    public void iterateStarCount() {
        this.justCreated = false;
        this.starCount += 1;
    }

    public void addFinishedMission(String name) {
        this.justCreated = false;
        this.finishedActivities.add(name);
    }

    public void addFinishedActivities(String name) {
        this.justCreated = false;
        this.finishedActivities.add(name);
    }

    public String getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(String lastActivity) {
        this.lastActivity = lastActivity;
    }

    public boolean isJustCreated() {
        return justCreated;
    }

    public void setJustCreated(boolean justCreated) {
        this.justCreated = justCreated;
    }

    @Override
    @JsonIgnore
    public String toString() {
        return "class UserProgress {" +
                " stripeCount: " + stripeCount + ";" +
                " starCount: " + starCount + ";" +
                " lastActivity: " + lastActivity + ";" +
                " finishedActivities: " + String.join(", ", finishedActivities) + ";" +
                " finishedMissions: " + String.join(", ", finishedMissions) +
                "}";
    }

}
