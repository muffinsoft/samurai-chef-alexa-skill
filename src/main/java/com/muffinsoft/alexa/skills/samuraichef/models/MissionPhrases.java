package com.muffinsoft.alexa.skills.samuraichef.models;

import com.muffinsoft.alexa.sdk.model.BasePhraseContainer;

import java.util.List;
import java.util.Map;

public class MissionPhrases {

    private List<BasePhraseContainer> missionIntro;

    private List<BasePhraseContainer> missionOutro;

    private Map<String, List<BasePhraseContainer>> stripeIntrosByNumber;

    private Map<String, List<BasePhraseContainer>> stripeOutrosByNumber;

    public List<BasePhraseContainer> getMissionIntro() {
        return missionIntro;
    }

    public void setMissionIntro(List<BasePhraseContainer> missionIntro) {
        this.missionIntro = missionIntro;
    }

    public List<BasePhraseContainer> getMissionOutro() {
        return missionOutro;
    }

    public void setMissionOutro(List<BasePhraseContainer> missionOutro) {
        this.missionOutro = missionOutro;
    }

    public Map<String, List<BasePhraseContainer>> getStripeIntrosByNumber() {
        return stripeIntrosByNumber;
    }

    public void setStripeIntrosByNumber(Map<String, List<BasePhraseContainer>> stripeIntrosByNumber) {
        this.stripeIntrosByNumber = stripeIntrosByNumber;
    }

    public Map<String, List<BasePhraseContainer>> getStripeOutrosByNumber() {
        return stripeOutrosByNumber;
    }

    public void setStripeOutrosByNumber(Map<String, List<BasePhraseContainer>> stripeOutrosByNumber) {
        this.stripeOutrosByNumber = stripeOutrosByNumber;
    }

    public List<BasePhraseContainer> getStripeOutroByNumber(int number) {
        return stripeOutrosByNumber.get(String.valueOf(number));
    }

    public List<BasePhraseContainer> getStripeIntroByNumber(int number) {
        return stripeIntrosByNumber.get(String.valueOf(number));
    }
}
