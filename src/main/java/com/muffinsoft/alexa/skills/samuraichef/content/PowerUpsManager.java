package com.muffinsoft.alexa.skills.samuraichef.content;

import com.muffinsoft.alexa.sdk.content.BaseContentManager;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static com.muffinsoft.alexa.skills.samuraichef.constants.ItemConstants.EQUIPMENT;

public class PowerUpsManager extends BaseContentManager<List<String>> {

    public PowerUpsManager(String path) {
        super(path);
    }

    public String getNextRandomItem(Set<String> alreadyExists) {

        List<String> allEquipmentByActivity = getValueByKey(EQUIPMENT);

        allEquipmentByActivity.removeAll(alreadyExists);

        return getRandomEquipmentFromList(allEquipmentByActivity);
    }

    private String getRandomEquipmentFromList(List<String> equipments) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int nextIngredient = random.nextInt(equipments.size());
        return equipments.get(nextIngredient);
    }
}
