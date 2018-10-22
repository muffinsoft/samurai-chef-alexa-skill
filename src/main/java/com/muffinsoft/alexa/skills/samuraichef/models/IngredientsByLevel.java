package com.muffinsoft.alexa.skills.samuraichef.models;

import java.util.List;

public class IngredientsByLevel {

    private String name;
    private List<Level> levels;

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
        for(Level it : levels ) {
            if(it.getNumber() == level) {
                return it;
            }
        }
        throw new IllegalStateException("Can't find level with number " + level);
    }
}
