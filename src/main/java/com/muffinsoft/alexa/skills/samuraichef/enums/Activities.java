package com.muffinsoft.alexa.skills.samuraichef.enums;

public enum Activities {
    NAME_HANDLER("intro"),
    SUSHI_SLICE("sushiSlice"),
    JUICE_WARRIOR("juiceWarrior"),
    WORD_BOARD_KARATE("wordBoardKarate");

    private final String title;


    Activities(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
