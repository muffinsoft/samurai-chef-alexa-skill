package com.muffinsoft.alexa.skills.samuraichef.models;

import java.util.List;

public class SpeechSettings {

    private List<PhraseSettings> intro;
    private List<PhraseSettings> outro;
    private List<PhraseSettings> demo;
    private boolean shouldRunDemo;
    private String moveToPhaseTwo;
    private String shouldRunDemoPhrase;
    private String readyToStartPhrase;

    public boolean isShouldRunDemo() {
        return shouldRunDemo;
    }

    public void setShouldRunDemo(boolean shouldRunDemo) {
        this.shouldRunDemo = shouldRunDemo;
    }

    public List<PhraseSettings> getIntro() {
        return intro;
    }

    public void setIntro(List<PhraseSettings> intro) {
        this.intro = intro;
    }

    public List<PhraseSettings> getOutro() {
        return outro;
    }

    public void setOutro(List<PhraseSettings> outro) {
        this.outro = outro;
    }

    public List<PhraseSettings> getDemo() {
        return demo;
    }

    public void setDemo(List<PhraseSettings> demo) {
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
