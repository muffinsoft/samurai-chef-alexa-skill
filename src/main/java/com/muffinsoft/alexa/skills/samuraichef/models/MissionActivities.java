package com.muffinsoft.alexa.skills.samuraichef.models;

import java.util.Map;

public class MissionActivities {

    private String title;

    private String missionIntro;

    private String missionOutro;

    private Map<String, Integer> activitiesOrder;

    private Map<String, String> stripeIntrosByNumber;

    private Map<String, String> stripeOutrosByNumber;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMissionIntro() {
        return missionIntro;
    }

    public void setMissionIntro(String missionIntro) {
        this.missionIntro = missionIntro;
    }

    public Map<String, Integer> getActivitiesOrder() {
        return activitiesOrder;
    }

    public void setActivitiesOrder(Map<String, Integer> activitiesOrder) {
        this.activitiesOrder = activitiesOrder;
    }

    public Map<String, String> getStripeIntrosByNumber() {
        return stripeIntrosByNumber;
    }

    public void setStripeIntrosByNumber(Map<String, String> stripeIntrosByNumber) {
        this.stripeIntrosByNumber = stripeIntrosByNumber;
    }

    public String getStripeIntroByNumber(int number) {
        return stripeIntrosByNumber.get(String.valueOf(number));
    }

    public Map<String, String> getStripeOutrosByNumber() {
        return stripeOutrosByNumber;
    }

    public void setStripeOutrosByNumber(Map<String, String> stripeOutrosByNumber) {
        this.stripeOutrosByNumber = stripeOutrosByNumber;
    }

    public String getMissionOutro() {
        return missionOutro;
    }

    public void setMissionOutro(String missionOutro) {
        this.missionOutro = missionOutro;
    }

    public String getStripeOutroByNumber(int number) {
        return stripeOutrosByNumber.get(String.valueOf(number));
    }
}
