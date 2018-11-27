package com.muffinsoft.alexa.skills.samuraichef.models;

import java.util.List;
import java.util.Map;

public class MissionPhrases {

    private List<PhraseSettings> missionIntro;

    private List<PhraseSettings> missionOutro;

    private Map<String, List<PhraseSettings>> stripeIntrosByNumber;

    private Map<String, List<PhraseSettings>> stripeOutrosByNumber;

    public List<PhraseSettings> getMissionIntro() {
        return missionIntro;
    }

    public void setMissionIntro(List<PhraseSettings> missionIntro) {
        this.missionIntro = missionIntro;
    }

    public List<PhraseSettings> getMissionOutro() {
        return missionOutro;
    }

    public void setMissionOutro(List<PhraseSettings> missionOutro) {
        this.missionOutro = missionOutro;
    }

    public Map<String, List<PhraseSettings>> getStripeIntrosByNumber() {
        return stripeIntrosByNumber;
    }

    public void setStripeIntrosByNumber(Map<String, List<PhraseSettings>> stripeIntrosByNumber) {
        this.stripeIntrosByNumber = stripeIntrosByNumber;
    }

    public Map<String, List<PhraseSettings>> getStripeOutrosByNumber() {
        return stripeOutrosByNumber;
    }

    public void setStripeOutrosByNumber(Map<String, List<PhraseSettings>> stripeOutrosByNumber) {
        this.stripeOutrosByNumber = stripeOutrosByNumber;
    }

    public List<PhraseSettings> getStripeOutroByNumber(int number) {
        return stripeOutrosByNumber.get(String.valueOf(number));
    }

    public List<PhraseSettings> getStripeIntroByNumber(int number) {
        return stripeIntrosByNumber.get(String.valueOf(number));
    }
}
