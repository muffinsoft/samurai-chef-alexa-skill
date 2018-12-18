package com.muffinsoft.alexa.skills.samuraichef.models;

import com.muffinsoft.alexa.sdk.model.BasePhraseContainer;

import java.util.List;

public class SpeechSettings {

    private List<BasePhraseContainer> intro;
    private List<BasePhraseContainer> outro;
    private List<BasePhraseContainer> demo;
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

    public List<BasePhraseContainer> getIntro() {
        return intro;
    }

    public void setIntro(List<BasePhraseContainer> intro) {
        this.intro = intro;
    }

    public List<BasePhraseContainer> getOutro() {
        return outro;
    }

    public void setOutro(List<BasePhraseContainer> outro) {
        this.outro = outro;
    }

    public List<BasePhraseContainer> getDemo() {
        return demo;
    }

    public void setDemo(List<BasePhraseContainer> demo) {
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
