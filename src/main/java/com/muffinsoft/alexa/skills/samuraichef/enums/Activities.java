package com.muffinsoft.alexa.skills.samuraichef.enums;

public enum Activities {

    SUSHI_SLICE("sushiSlice"),
    JUICE_WARRIOR("juiceWarrior"),
    WORD_BOARD_KARATE("wordBoardKarate"),
    FOOD_TASTER("foodTaster");

    private final String title;


    Activities(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
