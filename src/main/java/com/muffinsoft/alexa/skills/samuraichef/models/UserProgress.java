package com.muffinsoft.alexa.skills.samuraichef.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UserProgress {

    private String mission = null;
    private Set<String> finishedActivities = new HashSet<>();
    private int stripeCount = 0;
    private String currentActivity;
    private String previousActivity;
    private boolean justCreated = false;
    private boolean isGameFinished = false;
    private boolean isMissionFinished = false;

    private int mistakesInStripe = 0;
    private int mistakesInMission = 0;

    private boolean perfectActivity = false;
    private boolean perfectStripe = false;
    private boolean perfectMission = false;

    public UserProgress() {
    }

    public UserProgress(boolean isNew) {
        this.justCreated = isNew;
    }

    public UserProgress(UserMission mission) {
        this.mission = mission.name();
        this.justCreated = true;
    }

    public UserProgress(UserMission mission, boolean isNew) {
        this.justCreated = isNew;
        if (mission != null) {
            this.mission = mission.name();
        }
    }

    public void resetMissionProgress() {
        this.finishedActivities = new HashSet<>();
        this.stripeCount = 0;
        this.currentActivity = null;
        this.previousActivity = null;
        this.justCreated = false;
        this.isGameFinished = false;
        this.isMissionFinished = false;
    }

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
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

    public void iterateStripeCount() {
        this.justCreated = false;
        this.stripeCount += 1;
        this.mistakesInStripe = 0;
    }

    public void resetFinishRounds() {
        this.justCreated = false;
        this.finishedActivities = new HashSet<>();
    }

    public void addFinishedActivities(String name) {
        this.justCreated = false;
        this.previousActivity = name;
        this.finishedActivities.add(name);
    }

    public boolean isGameFinished() {
        return isGameFinished;
    }

    public void setGameFinished(boolean gameFinished) {
        this.isGameFinished = gameFinished;
    }

    public String getPreviousActivity() {
        return previousActivity;
    }

    public void setPreviousActivity(String previousActivity) {
        this.previousActivity = previousActivity;
    }

    public String getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(String currentActivity) {
        this.currentActivity = currentActivity;
    }

    public boolean isJustCreated() {
        return justCreated;
    }

    public void setJustCreated(boolean justCreated) {
        this.justCreated = justCreated;
    }

    public boolean isMissionFinished() {
        return isMissionFinished;
    }

    public void setMissionFinished(boolean missionFinished) {
        isMissionFinished = missionFinished;
    }

    public int getMistakesInStripe() {
        return mistakesInStripe;
    }

    public void setMistakesInStripe(int mistakesInStripe) {
        this.mistakesInStripe = mistakesInStripe;
    }

    public int getMistakesInMission() {
        return mistakesInMission;
    }

    public void setMistakesInMission(int mistakesInMission) {
        this.mistakesInMission = mistakesInMission;
    }

    public boolean isPerfectActivity() {
        return perfectActivity;
    }

    public void setPerfectActivity(boolean perfectActivity) {
        this.perfectActivity = perfectActivity;
    }

    public boolean isPerfectStripe() {
        return perfectStripe;
    }

    public void setPerfectStripe(boolean perfectStripe) {
        this.perfectStripe = perfectStripe;
    }

    public boolean isPerfectMission() {
        return perfectMission;
    }

    public void setPerfectMission(boolean perfectMission) {
        this.perfectMission = perfectMission;
    }

    @Override
    @JsonIgnore
    public String toString() {
        return "class UserProgress {" +
                " stripeCount: " + stripeCount + ";" +
                " currentActivity: " + currentActivity + ";" +
                " finishedActivities: " + String.join(", ", finishedActivities) +
                "}";
    }

    public void addMistakeCount(int mistakesCount) {
        this.mistakesInStripe += mistakesCount;
        this.mistakesInMission += mistakesCount;
    }
}
