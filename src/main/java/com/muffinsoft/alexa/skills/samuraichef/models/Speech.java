package com.muffinsoft.alexa.skills.samuraichef.models;

import java.util.List;

public class Speech {

    private List<String> intro;
    private boolean shouldRunDemo;
    private List<String> demo;
    private String moveToPhaseTwo;
    private String shouldRunDemoPhrase;
    private String readyToStartPhrase;
    private String listOfEquipmentPhrase;
    private String wantWearEquipmentPhrase;

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

    public String getListOfEquipmentPhrase() {
        return listOfEquipmentPhrase;
    }

    public void setListOfEquipmentPhrase(String listOfEquipmentPhrase) {
        this.listOfEquipmentPhrase = listOfEquipmentPhrase;
    }

    public String getReadyToStartPhrase() {
        return readyToStartPhrase;
    }

    public void setReadyToStartPhrase(String readyToStartPhrase) {
        this.readyToStartPhrase = readyToStartPhrase;
    }

    public String getWantWearEquipmentPhrase() {
        return wantWearEquipmentPhrase;
    }

    public void setWantWearEquipmentPhrase(String wantWearEquipmentPhrase) {
        this.wantWearEquipmentPhrase = wantWearEquipmentPhrase;
    }
}
