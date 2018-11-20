package com.muffinsoft.alexa.skills.samuraichef.content;

import com.fasterxml.jackson.core.type.TypeReference;
import com.muffinsoft.alexa.sdk.util.ContentLoader;
import com.muffinsoft.alexa.skills.samuraichef.enums.Activities;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.models.MissionActivities;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseSettings;
import com.muffinsoft.alexa.skills.samuraichef.models.ProgressContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MissionManager {

    private ProgressContainer container;

    public MissionManager(String path) {
        this.container = new ContentLoader().loadContent(new ProgressContainer(), path, new TypeReference<ProgressContainer>() {
        });
    }

    public ProgressContainer getContainer() {
        return container;
    }

    public void setContainer(ProgressContainer container) {
        this.container = container;
    }

    public Activities getFirstActivityForMission(UserMission userMission) {

        List<MissionActivities> allLevels = container.getMissions();

        for (MissionActivities level : allLevels) {

            if (Objects.equals(level.getTitle(), userMission.name())) {
                return getFirstActivity(level.getActivitiesOrder());
            }
        }
        return null;
    }

    public Activities getNextActivityForMission(UserMission userMission, Set<String> finishedActivities) {

        List<MissionActivities> allLevels = container.getMissions();

        for (MissionActivities level : allLevels) {

            if (Objects.equals(level.getTitle(), userMission.name())) {
                return getNextPossibleActivity(level.getActivitiesOrder(), finishedActivities);
            }
        }
        return null;
    }

    private Activities getNextPossibleActivity(Map<String, Integer> activitiesOrder, Set<String> finishedActivities) {

        Map<String, Integer> temp = new HashMap<>(activitiesOrder);

        for (String finishedMission : finishedActivities) {
            temp.remove(finishedMission);
        }

        String possibleActivity = null;
        Integer minimalValue = null;

        for (Map.Entry<String, Integer> entry : temp.entrySet()) {
            if (minimalValue == null) {
                minimalValue = entry.getValue();
                possibleActivity = entry.getKey();
            }

            if (entry.getValue() <= minimalValue) {
                possibleActivity = entry.getKey();
                minimalValue = entry.getValue();
            }
        }

        if (possibleActivity == null) {
            throw new IllegalStateException("Can't get next possible activity from " + activitiesOrder.toString() + " with " + String.join(", ", finishedActivities) + " finished activities");
        }

        return Activities.valueOf(possibleActivity);
    }

    private Activities getFirstActivity(Map<String, Integer> activities) {

        String possibleActivity = null;
        int minimalValue = 0;

        for (Map.Entry<String, Integer> entry : activities.entrySet()) {
            if (entry.getValue() <= minimalValue) {
                possibleActivity = entry.getKey();
            }
        }
        return Activities.valueOf(possibleActivity);
    }

    public Activities getNextActivity(Activities currentActivity, UserMission currentMission) {
        MissionActivities mission = container.getMissionByTitle(currentMission);

        Map<String, Integer> activitiesOrder = mission.getActivitiesOrder();

        Integer valueByKey = activitiesOrder.get(currentActivity.name());

        int searchedOderValue = valueByKey + 1;

        if (searchedOderValue > Activities.getGameActivities().size() - 1) {
            searchedOderValue = 0;
        }

        for (Map.Entry<String, Integer> entry : activitiesOrder.entrySet()) {
            if (entry.getValue() == searchedOderValue) {
                return Activities.valueOf(String.valueOf(entry.getKey()));
            }
        }
        return currentActivity;
    }

    public List<PhraseSettings> getMissionIntro(UserMission mission) {
        MissionActivities missionContainer = container.getMissionByTitle(mission);
        return missionContainer.getMissionIntro();
    }

    public List<PhraseSettings> getMissionOutro(UserMission mission) {
        MissionActivities missionContainer = container.getMissionByTitle(mission);
        return missionContainer.getMissionOutro();
    }

    public List<PhraseSettings> getStripeOutroByMission(UserMission mission, int number) {
        MissionActivities missionContainer = container.getMissionByTitle(mission);
        return missionContainer.getStripeOutroByNumber(number);
    }

    public int getSuccessInRowForPowerUp() {
        return container.getSuccessInRowForPowerUp();
    }

    public List<PhraseSettings> getStripeIntroByMission(UserMission mission, int number) {
        MissionActivities missionContainer = container.getMissionByTitle(mission);
        return missionContainer.getStripeIntroByNumber(number);
    }
}
