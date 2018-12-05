package com.muffinsoft.alexa.skills.samuraichef.content.phrases;

import com.muffinsoft.alexa.sdk.content.BaseContentManager;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseSettings;

import java.util.ArrayList;
import java.util.List;

public class GreetingsPhraseManager extends BaseContentManager<List<PhraseSettings>> {

    public GreetingsPhraseManager(String path) {
        super(path);
    }

    @Override
    public List<PhraseSettings> getValueByKey(String key) {

        List valueByKey = super.getValueByKey(key);

        List<PhraseSettings> resultList = new ArrayList<>(valueByKey.size() * 2);

        for (Object raw : valueByKey) {
            resultList.add(getObjectMapper().convertValue(raw, PhraseSettings.class));
        }
        return resultList;
    }
}
