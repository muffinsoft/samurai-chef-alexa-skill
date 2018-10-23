package com.muffinsoft.alexa.skills.samuraichef.models;

import java.util.List;
import java.util.Map;

public class ActivitiesSettings {

    private String name;
    private List<Level> levels;
    private Map<String, Speech> speeches;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Level> getLevels() {
        return levels;
    }

    public void setLevels(List<Level> levels) {
        this.levels = levels;
    }

    public Level getLevel(int level) {
        for (Level it : levels) {
            if (it.getNumber() == level) {
                return it;
            }
        }
        throw new IllegalStateException("Can't find level with number " + level);
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
