package com.muffinsoft.alexa.skills.samuraichef.activities;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.model.Slot;
import com.muffinsoft.alexa.sdk.activities.BaseSessionStateManager;
import com.muffinsoft.alexa.sdk.model.DialogItem;
import com.muffinsoft.alexa.skills.samuraichef.components.UserReplyComparator;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserLevel;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;

import java.util.HashMap;
import java.util.Map;

import static com.muffinsoft.alexa.skills.samuraichef.constants.SessionConstants.USER_LEVEL;

public class SelectLevelStateManager extends BaseSessionStateManager {

    public SelectLevelStateManager(Map<String, Slot> slots, AttributesManager attributesManager) {
        super(slots, attributesManager);
    }

    @Override
    protected void initializeSessionAttributes() {
        this.sessionAttributes = new HashMap<>();
    }

    @Override
    protected void populateActivityVariables() {

    }

    @Override
    protected void updateSessionAttributes() {

    }

    @Override
    protected void updatePersistentAttributes() {

    }

    @Override
    public DialogItem nextResponse() {

        String dialog;
        if (UserReplyComparator.compare(userReply, UserReplies.YES)) {
            dialog = "Please, select the level";
        }
        if (UserReplyComparator.compare(userReply, UserReplies.LOW)) {
            dialog = "Are you ready to start on the First level?";
            this.sessionAttributes.put(USER_LEVEL, UserLevel.LOW);
        }
        else if (UserReplyComparator.compare(userReply, UserReplies.MEDIUM)) {
            dialog = "Are you ready to start on the Second level?";
            this.sessionAttributes.put(USER_LEVEL, UserLevel.MEDIUM);
        }
        else if (UserReplyComparator.compare(userReply, UserReplies.HIGH)) {
            dialog = "Are you ready to start on the Third level?";
            this.sessionAttributes.put(USER_LEVEL, UserLevel.HIGH);
        }
        else {
            dialog = "I don't understand your choice, Please, select one of three available";
        }
        return new DialogItem(dialog, false, actionSlotName);
    }
}
