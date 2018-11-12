package com.muffinsoft.alexa.skills.samuraichef.content;

import com.muffinsoft.alexa.sdk.content.BaseContentManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.PowerUps;

import java.util.Set;

import static com.muffinsoft.alexa.skills.samuraichef.enums.PowerUps.CORRECT_ANSWER_SLOT;
import static com.muffinsoft.alexa.skills.samuraichef.enums.PowerUps.SECOND_CHANCE_SLOT;

public class AliasManager extends BaseContentManager<String> {

    public AliasManager(String path) {
        super(path);
    }
}
