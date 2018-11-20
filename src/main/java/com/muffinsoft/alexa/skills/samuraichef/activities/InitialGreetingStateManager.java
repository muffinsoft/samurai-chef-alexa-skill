package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.sdk.model.SlotName;
import com.muffinsoft.alexa.skills.samuraichef.constants.GreetingsConstants;
import com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants;
import com.muffinsoft.alexa.skills.samuraichef.content.GreetingsManager;
import com.muffinsoft.alexa.skills.samuraichef.models.ConfigContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseSettings;

import java.util.List;
import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.components.VoiseTranslator.translate;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.INTENT;
import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_REPLY_BREAKPOINT;
import static com.muffinsoft.alexa.skills.samuraichef.enums.Intents.GAME;
import static com.muffinsoft.alexa.skills.samuraichef.enums.Intents.INITIAL_GREETING;

public class InitialGreetingStateManager extends BaseStateManager {

    private final GreetingsManager greetingsManager;

    private Integer userReplyBreakpointPosition;

    public InitialGreetingStateManager(Map<String, Slot> inputSlots, AttributesManager attributesManager, ConfigContainer configContainer) {
        super(inputSlots, attributesManager);
        this.greetingsManager = configContainer.getGreetingsManager();
    }

    @Override
    protected void populateActivityVariables() {
        this.userReplyBreakpointPosition = (Integer) this.getSessionAttributes().getOrDefault(USER_REPLY_BREAKPOINT, null);
    }

    @Override
    public DialogItem nextResponse() {

        DialogItem.Builder builder = DialogItem.builder();

        List dialog = greetingsManager.getValueByKey(GreetingsConstants.FIRST_TIME_GREETING);

        this.getSessionAttributes().remove(USER_REPLY_BREAKPOINT);
        this.getSessionAttributes().put(INTENT, GAME);

        int index = 0;
        for (Object rawPhraseSettings : dialog) {
            PhraseSettings phraseSettings = mapper.convertValue(rawPhraseSettings, PhraseSettings.class);
            index++;

            if (this.userReplyBreakpointPosition != null && index <= this.userReplyBreakpointPosition) {
                continue;
            }

            if (phraseSettings.isUserResponse()) {
                this.getSessionAttributes().put(SessionConstants.USER_REPLY_BREAKPOINT, index + 1);
                this.getSessionAttributes().put(INTENT, INITIAL_GREETING);
                break;
            }
            builder.addResponse(translate(phraseSettings));
        }

        return builder.withSlotName(SlotName.ACTION.text).build();
    }
}
