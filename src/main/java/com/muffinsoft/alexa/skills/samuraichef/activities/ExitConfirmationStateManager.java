package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseStateManager;
import com.muffinsoft.alexa.sdk.model.BasePhraseContainer;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.constants.GreetingsPhraseConstants;
import com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants;
import com.muffinsoft.alexa.skills.samuraichef.content.phrases.GreetingsPhraseManager;
import com.muffinsoft.alexa.skills.samuraichef.models.PhraseDependencyContainer;
import com.muffinsoft.alexa.skills.samuraichef.models.SettingsDependencyContainer;

import java.util.List;
import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_REPLY_BREAKPOINT;

public class ExitConfirmationStateManager extends BaseStateManager {

    private final GreetingsPhraseManager greetingsPhraseManager;

    private Integer userReplyBreakpointPosition;

    public ExitConfirmationStateManager(Map<String, Slot> inputSlots, AttributesManager attributesManager, SettingsDependencyContainer settingsDependencyContainer, PhraseDependencyContainer phraseDependencyContainer) {
        super(inputSlots, attributesManager, settingsDependencyContainer.getDialogTranslator());
        this.greetingsPhraseManager = phraseDependencyContainer.getGreetingsPhraseManager();
    }

    @Override
    protected void populateActivityVariables() {
        this.userReplyBreakpointPosition = (Integer) this.getSessionAttributes().getOrDefault(USER_REPLY_BREAKPOINT, null);
    }

    @Override
    public DialogItem nextResponse() {

        DialogItem.Builder builder = DialogItem.builder();

        List<BasePhraseContainer> dialog = greetingsPhraseManager.getValueByKey(GreetingsPhraseConstants.EXIT_PHRASE);

        this.getSessionAttributes().remove(USER_REPLY_BREAKPOINT);

        int index = 0;
        for (BasePhraseContainer BasePhraseContainer : dialog) {

            index++;

            if (this.userReplyBreakpointPosition != null && index <= this.userReplyBreakpointPosition) {
                continue;
            }

            if (BasePhraseContainer.isUserResponse()) {
                this.getSessionAttributes().put(SessionConstants.USER_REPLY_BREAKPOINT, index);
                this.getSessionAttributes().put("ANY_RESPONSE", true);
                break;
            }
            builder.addResponse(getDialogTranslator().translate(BasePhraseContainer));
        }

        if (index >= dialog.size()) {
            this.getSessionAttributes().remove(USER_REPLY_BREAKPOINT);
            builder.shouldEnd();
        }

        return builder
                .turnOffReprompt()
                .build();
    }
}