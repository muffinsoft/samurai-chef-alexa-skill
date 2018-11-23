package com.muffinsoft.alexa.skills.samuraichef.enums;

import java.util.Arrays;
import java.util.List;

public enum StatePhase {

    MISSION_INTRO,

    STRIPE_INTRO,

    ACTIVITY_INTRO,

    DEMO,

    READY_PHASE,

    WRAP_READY_RESULT,

    PHASE_1,

    PHASE_2,

    WIN,

    LOSE,

    STRIPE_OUTRO,

    MISSION_OUTRO,

    GAME_OUTRO;

    public static List<StatePhase> getActivityStates() {
        return Arrays.asList(ACTIVITY_INTRO, DEMO, READY_PHASE, PHASE_1, PHASE_2, WIN, LOSE);
    }
}
