package com.muffinsoft.alexa.skills.samuraichef.enums;

import java.util.Arrays;
import java.util.List;

public enum Activities {

    SUSHI_SLICE(true),

    JUICE_WARRIOR(false),

    WORD_BOARD_KARATE(true),

    FOOD_TASTER(false);

    private final boolean isCompetition;

    Activities(boolean isCompetition) {
        this.isCompetition = isCompetition;
    }

    public static List<Activities> getGameActivities() {
        return Arrays.asList(SUSHI_SLICE, JUICE_WARRIOR, WORD_BOARD_KARATE, FOOD_TASTER);
    }

    public static boolean checkIfCompetition(Activities currentActivity) {
        return currentActivity.isCompetition;
    }
}
