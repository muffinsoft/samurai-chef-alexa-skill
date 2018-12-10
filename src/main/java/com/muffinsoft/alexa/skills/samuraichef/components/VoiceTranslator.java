package com.muffinsoft.alexa.skills.samuraichef.components;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muffinsoft.alexa.sdk.enums.SpeechType;
import com.muffinsoft.alexa.sdk.model.Speech;
import com.muffinsoft.alexa.sdk.util.ContentLoader;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoiceTranslator {

    private static final Map<String, String> characters;
    private static final String PATH = "phrases/characters.json";

    static {
        characters = new ContentLoader(new ObjectMapper()).loadContent(new HashMap<>(), PATH, new TypeReference<HashMap>() {
        });
    }

    public static Speech translate(String content) {
        return translate(new PhraseSettings(content));
    }

    public static Speech translate(String content, String role) {
        return translate(new PhraseSettings(content, role));
    }

    public static List<Speech> translate(List<PhraseSettings> phraseSettings) {
        List<Speech> result = new ArrayList<>();
        for (PhraseSettings settings : phraseSettings) {
            if (settings != null) {
                Speech translatedSpeech = translate(settings);
                result.add(translatedSpeech);
            }
        }
        return result;
    }

    public static Speech translate(PhraseSettings phraseSettings) {

        if (phraseSettings.getRole() == null) {
            return new Speech(SpeechType.TEXT, phraseSettings.getContent());
        }

        switch (phraseSettings.getRole()) {
            case "Alexa":
                return new Speech(SpeechType.TEXT, phraseSettings.getContent());
            case "Speechcon":
                return new Speech(SpeechType.SPEECHCON, phraseSettings.getContent());
            case "Audio":
            case "Sound":
                return new Speech(SpeechType.AUDIO, phraseSettings.getAudio());
            default:
                SpeechType type = getRole(phraseSettings.getRole());
                return new Speech(type, phraseSettings.getContent());
        }
    }

    private static SpeechType getRole(String role) {
        String stringifyRole = characters.get(role);
        if (stringifyRole != null) {
            return SpeechType.valueOf(stringifyRole);
        }
        else {
            return SpeechType.TEXT;
        }
    }
}
