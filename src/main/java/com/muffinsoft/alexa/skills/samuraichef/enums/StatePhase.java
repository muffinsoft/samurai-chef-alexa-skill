package com.muffinsoft.alexa.skills.samuraichef.enums;

public enum StatePhase {
    /**
     * Here intro dialog will be played
     */
    INTRO,

    /**
     * Example of the round will be played
     */
    DEMO,

    /**
     * We are waiting for user's response yes/no on question "do you want to equip smth?"
     */
    EQUIPMENT_PHASE,

    /**
     * We are waiting for user's response yes/no on question "ready to start?"
     */
    READY_PHASE,

    /**
     * Active phase of the activity
     */
    PHASE_1,

    /**
     * Second phase of the activity (if present)
     */
    PHASE_2,

    /**
     * Waiting when user will be ready for the next activity
     */
    WIN,

    /**
     * We are waiting for user's response try again / start new mission.
     */
    LOSE
}
