package com.muffinsoft.alexa.skills.samuraichef.content;

import com.muffinsoft.alexa.sdk.content.BaseContentManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.PowerUps;

import java.util.Set;

import static com.muffinsoft.alexa.skills.samuraichef.enums.PowerUps.CORRECT_ANSWER_SLOT;
import static com.muffinsoft.alexa.skills.samuraichef.enums.PowerUps.SECOND_CHANCE_SLOT;

public class PowerUpsManager extends BaseContentManager<String> {

    public PowerUpsManager(String path) {
        super(path);
    }

    public PowerUps getNextPowerUp(Set<String> existingPowerUps) {

        if (existingPowerUps.isEmpty()) {
            return SECOND_CHANCE_SLOT;
        }
        else if (existingPowerUps.size() == 2) {
            return null;
        }
        else if (existingPowerUps.contains(SECOND_CHANCE_SLOT.name())) {
            return CORRECT_ANSWER_SLOT;
        }
        else {
            return SECOND_CHANCE_SLOT;
        }
    }
}
