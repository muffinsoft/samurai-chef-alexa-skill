package com.muffinsoft.alexa.skills.samuraichef.components;

import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.Equipments;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ActivityEquipmentFilter {

    public static Set<String> filterAllAvailableForActivity(Set<String> allAvailableEquipment, Activities activity) {

        Set<String> result = new HashSet<>();

        for (String element : allAvailableEquipment) {
            Equipments equipments = Equipments.valueOf(element);
            List<Activities> availableActivities = equipments.getAvailableActivities();
            if (availableActivities.contains(activity)) {
                result.add(element);
            }
        }
        return result;
    }
}
