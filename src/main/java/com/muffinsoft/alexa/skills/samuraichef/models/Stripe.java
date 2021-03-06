package com.muffinsoft.alexa.skills.samuraichef.models;

import java.util.Map;

public class Stripe {

    private Integer wonSuccessCount;
    private Integer phaseTwoSuccessCount;
    private Long timeLimitPhaseOneInMillis;
    private Long timeLimitPhaseTwoInMillis;
    private Integer maxMistakeCount;
    private Map<String, String> ingredients;
    private boolean useVocabulary;
    private boolean withTimer;
    private String vocabularySource;

    public Integer getWonSuccessCount() {
        return wonSuccessCount;
    }

    public void setWonSuccessCount(Integer wonSuccessCount) {
        this.wonSuccessCount = wonSuccessCount;
    }

    public Integer getPhaseTwoSuccessCount() {
        return phaseTwoSuccessCount;
    }

    public void setPhaseTwoSuccessCount(Integer phaseTwoSuccessCount) {
        this.phaseTwoSuccessCount = phaseTwoSuccessCount;
    }

    public Long getTimeLimitPhaseOneInMillis() {
        return timeLimitPhaseOneInMillis;
    }

    public void setTimeLimitPhaseOneInMillis(Long timeLimitPhaseOneInMillis) {
        this.timeLimitPhaseOneInMillis = timeLimitPhaseOneInMillis;
    }

    public Long getTimeLimitPhaseTwoInMillis() {
        return timeLimitPhaseTwoInMillis;
    }

    public void setTimeLimitPhaseTwoInMillis(Long timeLimitPhaseTwoInMillis) {
        this.timeLimitPhaseTwoInMillis = timeLimitPhaseTwoInMillis;
    }

    public Integer getMaxMistakeCount() {
        return maxMistakeCount;
    }

    public void setMaxMistakeCount(Integer maxMistakeCount) {
        this.maxMistakeCount = maxMistakeCount;
    }

    public Map<String, String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Map<String, String> ingredients) {
        this.ingredients = ingredients;
    }

    public boolean isUseVocabulary() {
        return useVocabulary;
    }

    public void setUseVocabulary(boolean useVocabulary) {
        this.useVocabulary = useVocabulary;
    }

    public boolean isWithTimer() {
        return withTimer;
    }

    public void setWithTimer(boolean withTimer) {
        this.withTimer = withTimer;
    }

    public String getVocabularySource() {
        return vocabularySource;
    }

    public void setVocabularySource(String vocabularySource) {
        this.vocabularySource = vocabularySource;
    }
}
