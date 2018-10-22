package com.muffinsoft.alexa.skills.samuraichef.models;

import java.util.Map;

public class Level {

    private int number;
    private Integer wonSuccessCount;
    private Integer phaseTwoSuccessCount;
    private Long timeLimitPhaseOneInMillis;
    private Long timeLimitPhaseTwoInMillis;
    private Integer maxMistakeCount;
    private Map<String, String> ingredients;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

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
}
