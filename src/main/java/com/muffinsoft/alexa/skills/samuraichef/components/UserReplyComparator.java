package com.muffinsoft.alexa.skills.samuraichef.components;

import com.muffinsoft.alexa.skills.samuraichef.IoC;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class UserReplyComparator {

    private static final Logger logger = LogManager.getLogger(UserReplyComparator.class);

    public static boolean compare(String userReply, UserReplies expectedValue) {
        if (userReply == null) {
            return false;
        }
        List<String> values = IoC.provideUserReplyManager().getValueByKey(expectedValue.name());
        boolean contains = values.contains(userReply.toLowerCase());
        logger.debug("Comparing user input '" + userReply + "' with values [" + String.join(", ", values) + "] returns " + contains);
        return contains;
    }
}
