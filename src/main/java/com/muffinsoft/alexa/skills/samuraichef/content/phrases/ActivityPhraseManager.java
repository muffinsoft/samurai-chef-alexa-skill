package com.muffinsoft.alexa.skills.samuraichef.content.phrases;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muffinsoft.alexa.sdk.util.ContentLoader;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.models.SpeechSettings;

import java.util.HashMap;
import java.util.Map;

public class ActivityPhraseManager {

    private static final String SUSHI_SLICE = "phrases/sushi-slice-phrases.json";
    private static final String JUICE_WARRIOR = "phrases/juice-warrior-phrases.json";
    private static final String WORD_BOARD_KARATE = "phrases/word-board-karate-phrases.json";
    private static final String FOOD_TASTER = "phrases/food-taster-phrases.json";

    private final ContentLoader contentLoader = new ContentLoader(new ObjectMapper());

    private final Map<Activities, Map<String, Map<String, SpeechSettings>>> containerByActivity;


    public ActivityPhraseManager() {

        containerByActivity = new HashMap<>();

        containerByActivity.put(Activities.SUSHI_SLICE, contentLoader.loadContent(new HashMap<>(), SUSHI_SLICE, new TypeReference<Map<String, Map<String, SpeechSettings>>>() {
        }));
        containerByActivity.put(Activities.JUICE_WARRIOR, contentLoader.loadContent(new HashMap<>(), JUICE_WARRIOR, new TypeReference<Map<String, Map<String, SpeechSettings>>>() {
        }));
        containerByActivity.put(Activities.WORD_BOARD_KARATE, contentLoader.loadContent(new HashMap<>(), WORD_BOARD_KARATE, new TypeReference<Map<String, Map<String, SpeechSettings>>>() {
        }));
        containerByActivity.put(Activities.FOOD_TASTER, contentLoader.loadContent(new HashMap<>(), FOOD_TASTER, new TypeReference<Map<String, Map<String, SpeechSettings>>>() {
        }));
    }

    public SpeechSettings getSpeechForActivityByStripeNumberAtMission(Activities currentActivity, int number, UserMission level) {
        Map<String, Map<String, SpeechSettings>> file = containerByActivity.get(currentActivity);
        Map<String, SpeechSettings> stripes = file.get(level.name());
        return stripes.get(String.valueOf(number));
    }

}
