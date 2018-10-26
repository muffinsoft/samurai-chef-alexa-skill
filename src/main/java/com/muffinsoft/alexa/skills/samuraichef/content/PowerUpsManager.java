package com.muffinsoft.alexa.skills.samuraichef.content;

import com.muffinsoft.alexa.sdk.content.BaseContentManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Equipments;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class PowerUpsManager extends BaseContentManager<String> {

    public PowerUpsManager(String path) {
        super(path);
    }

    public Equipments getNextRandomItem(Set<String> alreadyExists) {


        List<Equipments> allEquipment = Arrays.stream(Equipments.values())
                .filter(equipment -> equipment != Equipments.EMPTY_SLOT)
                .collect(Collectors.toList());

        allEquipment.remove(Equipments.EMPTY_SLOT);

        List<Equipments> exists = alreadyExists.stream()
                .map(Equipments::valueOf)
                .collect(Collectors.toList());

        allEquipment.removeAll(exists);

        return getRandomEquipmentFromList(allEquipment);
    }

    private Equipments getRandomEquipmentFromList(List<Equipments> equipments) {

        ThreadLocalRandom random = ThreadLocalRandom.current();

        int nextIngredient = random.nextInt(equipments.size());

        return equipments.get(nextIngredient);
    }
}
