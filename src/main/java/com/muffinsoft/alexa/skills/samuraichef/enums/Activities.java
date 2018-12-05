package com.muffinsoft.alexa.skills.samuraichef.enums;

public enum Activities {

    SUSHI_SLICE("SushiSlice"),

    JUICE_WARRIOR("JuiceWarrior"),

    WORD_BOARD_KARATE("WordBoardKarate"),

    FOOD_TASTER("FoodTaster");

    public final String key;

    Activities(String key) {
        this.key = key;
    }
}
