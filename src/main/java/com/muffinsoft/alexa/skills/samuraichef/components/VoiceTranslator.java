package com.muffinsoft.alexa.skills.samuraichef.components;

import com.muffinsoft.alexa.sdk.enums.SpeechType;
import com.muffinsoft.alexa.sdk.model.Speech;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseSettings;

public class VoiceTranslator {

    public static Speech translate(String content) {
        return translate(new PhraseSettings(content));
    }

    public static Speech translate(String content, String role) {
        return translate(new PhraseSettings(content, role));
    }

    public static Speech translate(PhraseSettings phraseSettings) {

        if (phraseSettings.getRole() == null) {
            return new Speech(SpeechType.TEXT, phraseSettings.getContent());
        }

        switch (phraseSettings.getRole()) {
            case "Mary":
                return new Speech(SpeechType.JOANNA, phraseSettings.getContent());
            case "Sensei":
                return new Speech(SpeechType.JOEY, phraseSettings.getContent());
            case "Alexa":
                return new Speech(SpeechType.TEXT, phraseSettings.getContent());
            case "Speechcon":
                return new Speech(SpeechType.SPEECHCON, phraseSettings.getContent());
            default:
                return new Speech(SpeechType.TEXT, phraseSettings.getContent());
        }
    }
}
