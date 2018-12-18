package com.muffinsoft.alexa.skills.samuraichef.content.phrases;

import com.muffinsoft.alexa.sdk.content.BaseContentManager;
import com.muffinsoft.alexa.sdk.model.BasePhraseContainer;

import java.util.ArrayList;
import java.util.List;

public class GreetingsPhraseManager extends BaseContentManager<List<BasePhraseContainer>> {

    public GreetingsPhraseManager(String path) {
        super(path);
    }

    @Override
    public List<BasePhraseContainer> getValueByKey(String key) {

        List valueByKey = super.getValueByKey(key);

        if (valueByKey == null) {
            throw new IllegalArgumentException("Can't find text by key: " + key);
        }

        List<BasePhraseContainer> resultList = new ArrayList<>(valueByKey.size() * 2);

        for (Object raw : valueByKey) {
            resultList.add(getObjectMapper().convertValue(raw, BasePhraseContainer.class));
        }
        return resultList;
    }
}
