package com.muffinsoft.alexa.skills.samuraichef.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Equipments {

    EMPTY_SLOT(Collections.emptyList()),

    SUSHI_BLADE(Arrays.asList(Activities.SUSHI_SLICE, Activities.JUICE_WARRIOR)),

    CUISINE_KATANA(Arrays.asList(Activities.SUSHI_SLICE, Activities.JUICE_WARRIOR)),

    SUPER_SPATULE(Arrays.asList(Activities.values())),

    SECRET_SAUCE(Arrays.asList(Activities.values())),

    CHEF_HAT(Arrays.asList(Activities.values())),

    KARATE_GI(Arrays.asList(Activities.values())),

    HACHIMAKI(Arrays.asList(Activities.values())),

    SUMO_MAWASHI(Collections.singletonList(Activities.FOOD_TASTER));

    private final List<Activities> availableActivities;

    Equipments(List<Activities> availableActivities) {
        this.availableActivities = availableActivities;
    }

    public List<Activities> getAvailableActivities() {
        return availableActivities;
    }
}
