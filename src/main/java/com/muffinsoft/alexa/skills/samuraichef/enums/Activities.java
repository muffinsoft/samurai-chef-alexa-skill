package com.muffinsoft.alexa.skills.samuraichef.enums;

import java.util.Arrays;
import java.util.List;

public enum Activities {

    SUSHI_SLICE,

    JUICE_WARRIOR,

    WORD_BOARD_KARATE,

    FOOD_TASTER;

    public static List<Activities> getGameActivities() {
        return Arrays.asList(SUSHI_SLICE, JUICE_WARRIOR, WORD_BOARD_KARATE, FOOD_TASTER);
    }
}
