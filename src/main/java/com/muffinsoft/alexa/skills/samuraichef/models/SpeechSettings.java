package com.muffinsoft.alexa.skills.samuraichef.models;

import java.util.List;

public class SpeechSettings {

    private List<String> intro;
    private boolean shouldRunDemo;
    private List<String> demo;
    private String moveToPhaseTwo;
    private String shouldRunDemoPhrase;
    private String readyToStartPhrase;

    public List<String> getIntro() {
        return intro;
    }

    public void setIntro(List<String> intro) {
        this.intro = intro;
    }

    public boolean isShouldRunDemo() {
        return shouldRunDemo;
    }

    public void setShouldRunDemo(boolean shouldRunDemo) {
        this.shouldRunDemo = shouldRunDemo;
    }

    public List<String> getDemo() {
        return demo;
    }

    public void setDemo(List<String> demo) {
        this.demo = demo;
    }

    public String getMoveToPhaseTwo() {
        return moveToPhaseTwo;
    }

    public void setMoveToPhaseTwo(String moveToPhaseTwo) {
        this.moveToPhaseTwo = moveToPhaseTwo;
    }

    public String getShouldRunDemoPhrase() {
        return shouldRunDemoPhrase;
    }

    public void setShouldRunDemoPhrase(String shouldRunDemoPhrase) {
        this.shouldRunDemoPhrase = shouldRunDemoPhrase;
    }

    public String getReadyToStartPhrase() {
        return readyToStartPhrase;
    }

    public void setReadyToStartPhrase(String readyToStartPhrase) {
        this.readyToStartPhrase = readyToStartPhrase;
    }
}
