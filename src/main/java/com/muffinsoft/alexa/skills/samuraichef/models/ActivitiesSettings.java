package com.muffinsoft.alexa.skills.samuraichef.models;

import java.util.List;
import java.util.Map;

public class ActivitiesSettings {

    private String name;
    private List<Stripe> stripes;
    private Map<String, Speech> speeches;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Stripe> getStripes() {
        return stripes;
    }

    public void setStripes(List<Stripe> stripes) {
        this.stripes = stripes;
    }

    public Stripe getStripe(int number) {
        for (Stripe it : stripes) {
            if (it.getNumber() == number) {
                return it;
            }
        }
        throw new IllegalStateException("Can't find stripe with number " + number);
    }

    public Map<String, Speech> getSpeeches() {
        return speeches;
    }

    public void setSpeeches(Map<String, Speech> speeches) {
        this.speeches = speeches;
    }

    public Speech getSpeech(int number) {
        return speeches.get(String.valueOf(number));
    }
}
