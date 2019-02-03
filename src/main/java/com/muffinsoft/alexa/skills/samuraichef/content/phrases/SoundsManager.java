package com.muffinsoft.alexa.skills.samuraichef.content.phrases;

import com.muffinsoft.alexa.sdk.content.BaseContentManager;
import com.muffinsoft.alexa.sdk.model.BasePhraseContainer;
import com.muffinsoft.alexa.sdk.model.PhraseContainer;

import java.util.Map;

public class SoundsManager extends BaseContentManager<PhraseContainer> {

    public SoundsManager(String path) {
        super(path);
        for (Map.Entry entry : getContainer().entrySet()) {
            BasePhraseContainer convertedValue = getObjectMapper().convertValue(entry.getValue(), BasePhraseContainer.class);
            getContainer().put(entry.getKey().toString(), convertedValue);
        }
    }
}


