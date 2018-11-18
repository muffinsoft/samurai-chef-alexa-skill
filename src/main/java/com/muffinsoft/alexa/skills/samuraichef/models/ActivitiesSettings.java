package com.muffinsoft.alexa.skills.samuraichef.models;

import java.util.Map;

public class ActivitiesSettings {

    private String name;
    private Map<String, Stripe> activitySettingsByStripeNumber;
    private Map<String, SpeechSettings> activitySpeechesByStripeNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Stripe getSettingsByStripeNumber(int number) {
        return activitySettingsByStripeNumber.get(String.valueOf(number));
    }

    public Map<String, Stripe> getActivitySettingsByStripeNumber() {
        return activitySettingsByStripeNumber;
    }

    public void setActivitySettingsByStripeNumber(Map<String, Stripe> activitySettingsByStripeNumber) {
        this.activitySettingsByStripeNumber = activitySettingsByStripeNumber;
    }

    public Map<String, SpeechSettings> getActivitySpeechesByStripeNumber() {
        return activitySpeechesByStripeNumber;
    }

    public void setActivitySpeechesByStripeNumber(Map<String, SpeechSettings> activitySpeechesByStripeNumber) {
        this.activitySpeechesByStripeNumber = activitySpeechesByStripeNumber;
    }

    public SpeechSettings getSpeechByStripeNumber(int number) {
        return activitySpeechesByStripeNumber.get(String.valueOf(number));
    }
}
