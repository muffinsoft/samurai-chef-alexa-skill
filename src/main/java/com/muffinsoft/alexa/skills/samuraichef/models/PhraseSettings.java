package com.muffinsoft.alexa.skills.samuraichef.models;

import com.muffinsoft.alexa.sdk.model.PhraseContainer;

public class PhraseSettings implements PhraseContainer {

    private String content;
    private String role;
    private String audio;
    private boolean userResponse;

    public PhraseSettings() {
    }

    public PhraseSettings(String content) {
        this.content = content;
    }

    public PhraseSettings(String content, String role) {
        this.content = content;
        this.role = role;
    }

    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean isUserResponse() {
        return userResponse;
    }

    public void setUserResponse(boolean userResponse) {
        this.userResponse = userResponse;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String getSource() {
        return null;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }
}
