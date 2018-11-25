package com.muffinsoft.alexa.skills.samuraichef.enums;

public enum UserMission {

    LOW_MISSION("LowMission"),

    MEDIUM_MISSION("MidMission"),

    HIGH_MISSION("HighMission");

    public final String key;

    UserMission(String key) {
        this.key = key;
    }
}
