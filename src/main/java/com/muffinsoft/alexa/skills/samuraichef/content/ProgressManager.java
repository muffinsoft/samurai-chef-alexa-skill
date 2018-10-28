package com.muffinsoft.alexa.skills.samuraichef.content;

import com.fasterxml.jackson.core.type.TypeReference;
import com.muffinsoft.alexa.sdk.util.ContentLoader;
import com.muffinsoft.alexa.skills.samuraichef.models.RewardContainer;

public class ProgressManager {

    private RewardContainer container;

    public ProgressManager(String path) {
        this.container = new ContentLoader().loadContent(this.container, path, new TypeReference<RewardContainer>() {
        });
    }

    public RewardContainer getContainer() {
        return container;
    }

    public void setContainer(RewardContainer container) {
        this.container = container;
    }
}
