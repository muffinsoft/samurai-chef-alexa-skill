package com.muffinsoft.alexa.skills.samuraichef.content.phrases;

import com.muffinsoft.alexa.sdk.content.BaseContentManager;
import com.muffinsoft.alexa.sdk.model.PhraseContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseSettings;

import java.util.ArrayList;
import java.util.List;

public class HelpPhraseManager extends BaseContentManager<List<PhraseContainer>> {

    public HelpPhraseManager(String path) {
        super(path);
    }

    @Override
    public List<PhraseContainer> getValueByKey(String key) {

        List valueByKey = super.getValueByKey(key);

        List<PhraseContainer> resultList = new ArrayList<>(valueByKey.size() * 2);

        for (Object raw : valueByKey) {
            resultList.add(getObjectMapper().convertValue(raw, PhraseSettings.class));
        }
        return resultList;
    }
}
