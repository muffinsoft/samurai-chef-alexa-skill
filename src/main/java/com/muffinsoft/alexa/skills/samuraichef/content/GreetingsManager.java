package com.muffinsoft.alexa.skills.samuraichef.content;

import com.muffinsoft.alexa.sdk.content.BaseContentManager;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseSettings;

import java.util.List;

public class GreetingsManager extends BaseContentManager<List<PhraseSettings>> {

    public GreetingsManager(String path) {
        super(path);
    }
}
