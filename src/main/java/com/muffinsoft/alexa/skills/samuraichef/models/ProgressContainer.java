package com.muffinsoft.alexa.skills.samuraichef.models;

import java.util.List;

public class ProgressContainer {

    private int stripesAtLevelCount;

    private int maxStarCount;

    private List<LevelActivities> levels;

    public int getStripesAtLevelCount() {
        return stripesAtLevelCount;
    }

    public void setStripesAtLevelCount(int stripesAtLevelCount) {
        this.stripesAtLevelCount = stripesAtLevelCount;
    }

    public int getMaxStarCount() {
        return maxStarCount;
    }

    public void setMaxStarCount(int maxStarCount) {
        this.maxStarCount = maxStarCount;
    }

    public List<LevelActivities> getLevels() {
        return levels;
    }

    public void setLevels(List<LevelActivities> levels) {
        this.levels = levels;
    }
}
