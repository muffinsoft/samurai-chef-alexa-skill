package com.muffinsoft.alexa.skills.samuraichef.models;

import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;

import java.util.List;
import java.util.Objects;

public class ProgressContainer {

    private int stripesAtMissionCount;

    private int maxStarCount;

    private int successInRowForPowerUp;

    private List<MissionActivities> missions;

    public int getStripesAtMissionCount() {
        return stripesAtMissionCount;
    }

    public void setStripesAtMissionCount(int stripesAtMissionCount) {
        this.stripesAtMissionCount = stripesAtMissionCount;
    }

    public int getMaxStarCount() {
        return maxStarCount;
    }

    public void setMaxStarCount(int maxStarCount) {
        this.maxStarCount = maxStarCount;
    }

    public List<MissionActivities> getMissions() {
        return missions;
    }

    public void setMissions(List<MissionActivities> missions) {
        this.missions = missions;
    }

    public MissionActivities getMissionByTitle(UserMission mission) {
        for (MissionActivities it : missions) {
            if (Objects.equals(it.getTitle(), mission.name())) {
                return it;
            }
        }
        throw new IllegalArgumentException("Can't find Mission by title: " + mission);
    }

    public int getSuccessInRowForPowerUp() {
        return successInRowForPowerUp;
    }
}
