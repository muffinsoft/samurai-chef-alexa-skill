package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.model.BasePhraseContainer;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.SlotName;
import com.muffinsoft.alexa.skills.samuraichef.constants.GreetingsPhraseConstants;
import com.muffinsoft.alexa.skills.samuraichef.constants.RegularPhraseConstants;
import com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.GreetingsPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.RegularPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;

import java.util.List;
import java.util.Map;

import static com.muffinsoft.alexa.sdk.enums.IntentType.GAME;
import static com.muffinsoft.alexa.sdk.enums.IntentType.INITIAL_GREETING;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_REPLY_BREAKPOINT;

public class InitialGreetingStateManager extends BaseStateManager {

    private final GreetingsPhraseManager greetingsPhraseManager;
    private final RegularPhraseManager regularPhraseManager;

    private Integer userReplyBreakpointPosition;

    public InitialGreetingStateManager(Map<String, Slot> inputSlots, AttributesManager attributesManager, SettingsDependencyContainer settingsDependencyContainer, PhraseDependencyContainer phraseDependencyContainer) {
        super(inputSlots, attributesManager, settingsDependencyContainer.getDialogTranslator());
        this.greetingsPhraseManager = phraseDependencyContainer.getGreetingsPhraseManager();
        this.regularPhraseManager = phraseDependencyContainer.getRegularPhraseManager();
    }

    @Override
    protected void populateActivityVariables() {
        this.userReplyBreakpointPosition = (Integer) this.getSessionAttributes().getOrDefault(USER_REPLY_BREAKPOINT, null);
    }

    @Override
    public DialogItem nextResponse() {

        DialogItem.Builder builder = DialogItem.builder();

        List<BasePhraseContainer> dialog = greetingsPhraseManager.getValueByKey(GreetingsPhraseConstants.FIRST_TIME_GREETING);

        this.getSessionAttributes().remove(USER_REPLY_BREAKPOINT);
        this.getSessionAttributes().put(INTENT, GAME);

        int index = 0;
        for (BasePhraseContainer phraseContainer : dialog) {

            index++;

            if (this.userReplyBreakpointPosition != null && index <= this.userReplyBreakpointPosition) {
                continue;
            }

            if (phraseContainer.isUserResponse()) {
                this.getSessionAttributes().put(SessionConstants.USER_REPLY_BREAKPOINT, index);
                this.getSessionAttributes().put(INTENT, INITIAL_GREETING);
                break;
            }
            builder.addResponse(getDialogTranslator().translate(phraseContainer));
        }

        if (index >= dialog.size()) {
            builder.addResponse(getDialogTranslator().translate(regularPhraseManager.getValueByKey(RegularPhraseConstants.SELECT_MISSION_PHRASE)));
        }

        return builder
                .withSlotName(SlotName.MISSION)
                .turnOffReprompt()
                .build();
    }
}
