package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.SlotName;
import com.muffinsoft.alexa.skills.samuraichef.components.UserReplyComparator;
import com.muffinsoft.alexa.skills.samuraichef.content.AliasManager;
import com.muffinsoft.alexa.skills.samuraichef.content.PhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.enums.Intents;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserMission;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.components.VoiceTranslator.translate;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.READY_TO_START_MISSION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.REPEAT_LAST_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.PhraseConstants.SELECT_MISSION_PHRASE;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.CURRENT_MISSION;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;

public class ResetMissionSelectionStateManager extends BaseStateManager {

    private static final Logger logger = LogManager.getLogger(CancelStateManager.class);

    private final String userFoodSlotReply;

    private final PhraseManager phraseManager;
    private final AliasManager aliasManager;

    private UserMission currentMission;

    public ResetMissionSelectionStateManager(Map<String, Slot> slots, AttributesManager attributesManager, ConfigContainer configContainer) {
        super(slots, attributesManager);
        this.phraseManager = configContainer.getPhraseManager();
        this.aliasManager = configContainer.getAliasManager();
        String foodSlotName = SlotName.AMAZON_FOOD.text;
        this.userFoodSlotReply = slots != null ? (slots.containsKey(foodSlotName) ? slots.get(foodSlotName).getValue() : null) : null;
    }

    @Override
    public String getUserReply() {
        String userReply = super.getUserReply();
        if (userReply != null && !userReply.isEmpty()) {
            return userReply;
        }
        else {
            return this.userFoodSlotReply;
        }
    }

    @Override
    protected void populateActivityVariables() {

        Object isMissionPresent = getSessionAttributes().get(CURRENT_MISSION);
        this.currentMission = UserMission.valueOf(String.valueOf(isMissionPresent));

        logger.debug("Session attributes on the start of handling: " + this.getSessionAttributes().toString());
    }

    @Override
    public DialogItem nextResponse() {

        logger.debug("Available session attributes: " + getSessionAttributes());

        DialogItem.Builder builder = DialogItem.builder();

        if (UserReplyComparator.compare(getUserReply(), UserReplies.NO)) {
            PhraseSettings phraseSettings = phraseManager.getValueByKey(READY_TO_START_MISSION_PHRASE);
            phraseSettings.setContent(phraseSettings.getContent() + " " + aliasManager.getValueByKey(currentMission.name()) + "?");

            builder.addResponse(translate(phraseSettings));

            getSessionAttributes().put(INTENT, Intents.GAME);
        }
        else if (UserReplyComparator.compare(getUserReply(), UserReplies.YES)) {
            builder.addResponse(translate(phraseManager.getValueByKey(SELECT_MISSION_PHRASE)));
            getSessionAttributes().remove(CURRENT_MISSION);
            getSessionAttributes().put(INTENT, Intents.GAME);
        }
        else {
            builder.addResponse(translate(phraseManager.getValueByKey(REPEAT_LAST_PHRASE)));
        }

        return builder.build();
    }
}