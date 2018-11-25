package com.muffinsoft.alexa.skills.samuraichef.content;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muffinsoft.alexa.sdk.util.ContentLoader;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.models.ActivitiesSettings;
import com.muffinsoft.alexa.skills.samuraichef.models.SpeechSettings;
import com.muffinsoft.alexa.skills.samuraichef.models.Stripe;
import com.muffinsoft.alexa.skills.samuraichef.models.WordReaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ActivityManager {

    private static final String SUSHI_SLICE = "settings/sushi-slice.json";
    private static final String JUICE_WARRIOR = "settings/juice-warrior.json";
    private static final String WORD_BOARD_KARATE = "settings/word-board-karate.json";
    private static final String FOOD_TASTER = "settings/food-taster.json";

    private final ContentLoader contentLoader = new ContentLoader(new ObjectMapper());

    private final Map<Activities, ActivitiesSettings> containerByActivity;

    private final Map<String, Map<String, List<String>>> vocabularies = new HashMap<>();

    public ActivityManager() {

        containerByActivity = new HashMap<>();

        containerByActivity.put(Activities.SUSHI_SLICE, contentLoader.loadContent(new ActivitiesSettings(), SUSHI_SLICE, new TypeReference<ActivitiesSettings>() {
        }));
        containerByActivity.put(Activities.JUICE_WARRIOR, contentLoader.loadContent(new ActivitiesSettings(), JUICE_WARRIOR, new TypeReference<ActivitiesSettings>() {
        }));
        containerByActivity.put(Activities.WORD_BOARD_KARATE, contentLoader.loadContent(new ActivitiesSettings(), WORD_BOARD_KARATE, new TypeReference<ActivitiesSettings>() {
        }));
        containerByActivity.put(Activities.FOOD_TASTER, contentLoader.loadContent(new ActivitiesSettings(), FOOD_TASTER, new TypeReference<ActivitiesSettings>() {
        }));
    }

    public WordReaction getNextWord(Stripe stripe, String previousWord) {

        Map<String, String> wordsByActivity = stripe.getIngredients();

        List<String> wordList = new ArrayList<>(wordsByActivity.keySet());

        String word;
        String wordSound;

        if (stripe.isUseVocabulary()) {

            String nextSound = wordSound = getRandomWordFromList(wordList);

            Map<String, List<String>> vocabulary = getVocabulary(stripe);

            List<String> iterationList = new ArrayList<>(vocabulary.get(nextSound));

            if (previousWord != null) {
                iterationList.remove(previousWord);
            }

            word = getRandomWordFromList(iterationList);

        }
        else {

            if (previousWord != null) {
                wordList.remove(previousWord);
            }

            word = wordSound = getRandomWordFromList(wordList);

        }

        return new WordReaction(word, wordsByActivity.get(wordSound));
    }

    private Map<String, List<String>> getVocabulary(Stripe stripe) {

        String vocabularySource = stripe.getVocabularySource();

        if (!vocabularies.containsKey(vocabularySource)) {
            vocabularies.put(vocabularySource, contentLoader.loadContent(new HashMap<>(), vocabularySource, new TypeReference<Map<String, List<String>>>() {
            }));
        }

        return vocabularies.get(vocabularySource);
    }

    private String getRandomWordFromList(List<String> ingredients) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int nextIngredient = random.nextInt(ingredients.size());
        return ingredients.get(nextIngredient);
    }

    public Stripe getStripeForActivityAtMission(Activities currentActivity, int number, UserMission mission) {

        ActivitiesSettings activitiesSettings = containerByActivity.get(currentActivity);

        Stripe settingsByStripeNumberAtMission = activitiesSettings.getSettingsByStripeNumberAtMission(number, mission);

        if (activitiesSettings.isUseVocabulary()) {
            settingsByStripeNumberAtMission.setUseVocabulary(true);
            settingsByStripeNumberAtMission.setVocabularySource(activitiesSettings.getVocabularySource());
        }

        return settingsByStripeNumberAtMission;
    }

    public SpeechSettings getSpeechForActivityByStripeNumberAtMission(Activities currentActivity, int number, UserMission level) {
        ActivitiesSettings activitiesSettings = containerByActivity.get(currentActivity);
        return activitiesSettings.getSpeechByStripeNumberAtLevel(number, level);
    }

    public boolean isActivityCompetition(Activities currentActivity) {
        ActivitiesSettings activitiesSettings = containerByActivity.get(currentActivity);
        return activitiesSettings.isCompetition();
    }

    public String getCompetitionPartnerRole(Activities currentActivity) {
        ActivitiesSettings activitiesSettings = containerByActivity.get(currentActivity);
        return activitiesSettings.getCompetitionPartnerRole();
    }
}
