package com.muffinsoft.alexa.skills.samuraichef.components;

import com.muffinsoft.alexa.skills.samuraichef.IoC;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;

import java.util.List;

public class UserReplyComparator {

    public static boolean compare(String userReply, UserReplies expectedValue) {
        List<String> values = IoC.provideUserReplyManager().getValueByKey(expectedValue.name());
        return values.contains(userReply.toLowerCase());
    }
}
