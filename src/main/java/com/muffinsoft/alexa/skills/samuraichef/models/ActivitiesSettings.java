package com.muffinsoft.alexa.skills.samuraichef.models;

import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;

import java.util.Map;

public class ActivitiesSettings {

    private String name;
    private boolean competition;
    private boolean useVocabulary;
    private String vocabularySource;
    private String competitionPartnerRole;
    private Map<String, Map<String, Stripe>> activitySettingsByStripeNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCompetition() {
        return competition;
    }

    public void setCompetition(boolean competition) {
        this.competition = competition;
    }

    public boolean isUseVocabulary() {
        return useVocabulary;
    }

    public void setUseVocabulary(boolean useVocabulary) {
        this.useVocabulary = useVocabulary;
    }

    public Map<String, Map<String, Stripe>> getActivitySettingsByStripeNumber() {
        return activitySettingsByStripeNumber;
    }

    public void setActivitySettingsByStripeNumber(Map<String, Map<String, Stripe>> activitySettingsByStripeNumber) {
        this.activitySettingsByStripeNumber = activitySettingsByStripeNumber;
    }

    public Stripe getSettingsByStripeNumberAtMission(int stripeNumber, UserMission mission) {
        return this.activitySettingsByStripeNumber.get(mission.name()).get(String.valueOf(stripeNumber));
    }

    public String getCompetitionPartnerRole() {
        return competitionPartnerRole;
    }

    public void setCompetitionPartnerRole(String competitionPartnerRole) {
        this.competitionPartnerRole = competitionPartnerRole;
    }

    public String getVocabularySource() {
        return vocabularySource;
    }

    public void setVocabularySource(String vocabularySource) {
        this.vocabularySource = vocabularySource;
    }
}
