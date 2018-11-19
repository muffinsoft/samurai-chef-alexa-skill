package com.muffinsoft.alexa.skills.samuraichef.models;

import com.muffinsoft.alexa.sdk.enums.SpeechType;
import com.muffinsoft.alexa.sdk.model.PhraseContainer;

public class PhraseSettngs implements PhraseContainer {

    private String content;
    private String role;
    private String audioPath;

    @Override
    public SpeechType getType() {
        return SpeechType.valueOf(role);
    }

    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }
}
