package com.muffinsoft.alexa.skills.samuraichef.content.phrases;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muffinsoft.alexa.sdk.model.BasePhraseContainer;
import com.muffinsoft.alexa.sdk.util.ContentLoader;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.models.MissionPhrases;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MissionPhraseManager {

    private Map<String, MissionPhrases> container;

    public MissionPhraseManager(String path) {
        this.container = new ContentLoader(new ObjectMapper()).loadContent(new HashMap<>(), path, new TypeReference<Map<String, MissionPhrases>>() {
        });
    }

    public List<BasePhraseContainer> getMissionIntro(UserMission mission) {
        MissionPhrases missionPhrases = container.get(mission.name());
        return missionPhrases.getMissionIntro();
    }

    public List<BasePhraseContainer> getMissionOutro(UserMission mission) {
        MissionPhrases missionPhrases = container.get(mission.name());
        return missionPhrases.getMissionOutro();
    }

    public List<BasePhraseContainer> getStripeOutroByMission(UserMission mission, int number) {
        MissionPhrases missionPhrases = container.get(mission.name());
        return missionPhrases.getStripeOutroByNumber(number);
    }

    public List<BasePhraseContainer> getStripeIntroByMission(UserMission mission, int number) {
        MissionPhrases missionPhrases = container.get(mission.name());
        return missionPhrases.getStripeIntroByNumber(number);
    }
}
