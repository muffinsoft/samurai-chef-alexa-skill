package com.muffinsoft.alexa.skills.samuraichef.content;

import com.muffinsoft.alexa.sdk.content.BaseContentManager;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseSettings;

public class PhraseManager extends BaseContentManager<PhraseSettings> {

    public PhraseManager(String path) {
        super(path);
    }

    @Override
    public PhraseSettings getValueByKey(String key) {
        Object value = super.getValueByKey(key);
        return getObjectMapper().convertValue(value, PhraseSettings.class);
    }
}
