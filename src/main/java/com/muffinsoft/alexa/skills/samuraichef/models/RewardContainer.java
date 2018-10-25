package com.muffinsoft.alexa.skills.samuraichef.models;

public class RewardContainer {

    private int stripesToStarCount;

    private int winInARowCount;

    private int stripesToLevelCount;


    public int getStripesToStarCount() {
        return stripesToStarCount;
    }

    public void setStripesToStarCount(int stripesToStarCount) {
        this.stripesToStarCount = stripesToStarCount;
    }

    public int getWinInARowCount() {
        return winInARowCount;
    }

    public void setWinInARowCount(int winInARowCount) {
        this.winInARowCount = winInARowCount;
    }

    public int getStripesToLevelCount() {
        return stripesToLevelCount;
    }

    public void setStripesToLevelCount(int stripesToLevelCount) {
        this.stripesToLevelCount = stripesToLevelCount;
    }
}
