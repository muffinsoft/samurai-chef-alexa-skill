package com.muffinsoft.alexa.skills.samuraichef.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Equipments {

    EMPTY_SLOT(Collections.emptyList()),

    SECOND_CHANCE_SLOT(Arrays.asList(Activities.values())),

    CORRECT_ANSWER_SLOT(Arrays.asList(Activities.values()));

    private final List<Activities> availableActivities;

    Equipments(List<Activities> availableActivities) {
        this.availableActivities = availableActivities;
    }

    public List<Activities> getAvailableActivities() {
        return availableActivities;
    }
}
