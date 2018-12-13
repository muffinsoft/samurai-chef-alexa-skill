package com.muffinsoft.alexa.skills.samuraichef.components;

import com.muffinsoft.alexa.skills.samuraichef.IoC;
import com.muffinsoft.alexa.skills.samuraichef.enums.UserReplies;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

public class UserReplyComparator {

    protected static final Logger logger = LogManager.getLogger(UserReplyComparator.class);

    public static boolean compare(List<String> userReplies, UserReplies expectedValue) {
        logger.info("Going to compare " + expectedValue + " with user replies " + userReplies);
        for (String reply : userReplies) {
            if (compareSingleValue(reply, expectedValue)) {
                return true;
            }
        }
        return false;
    }

    public static boolean compare(List<String> userReplies, String expectedValue) {
        logger.info("Going to compare " + expectedValue + " with user replies " + userReplies);
        for (String reply : userReplies) {
            if (compareSingleValue(reply, Collections.singletonList(expectedValue))) {
                return true;
            }
        }
        return false;
    }

    private static boolean compareSingleValue(String userReply, UserReplies expectedValue) {
        if (userReply == null) {
            return false;
        }
        List<String> values = IoC.provideUserReplyManager().getValueByKey(expectedValue.name());

        return compareSingleValue(userReply, values);
    }

    private static boolean compareSingleValue(String userReply, List<String> expectedValue) {

        boolean contains = expectedValue.contains(userReply.toLowerCase());

        if (contains) {
            return true;
        }
        else {
            for (String value : expectedValue) {
                if (userReply.contains(value)) {
                    return true;
                }
            }
            return false;
        }
    }
}
