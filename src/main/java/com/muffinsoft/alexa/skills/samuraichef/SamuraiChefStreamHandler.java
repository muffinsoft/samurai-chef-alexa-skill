package com.muffinsoft.alexa.skills.samuraichef;

import com.amazon.ask.Skill;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.Skills;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiCancelandStopIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiHelpIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiLaunchRequestHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiSessionEndedRequestHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SushiSliceIntentHandler;

public class SamuraiChefStreamHandler extends SkillStreamHandler {

    public SamuraiChefStreamHandler() {
        super(getSkill());
    }

    private static Skill getSkill() {

        String amazonSkillId = System.getProperty("amazon-skill-id");

        return Skills.standard()
                .addRequestHandlers(
                        new SamuraiCancelandStopIntentHandler(),
                        new SamuraiHelpIntentHandler(),
                        new SamuraiLaunchRequestHandler(),
                        new SushiSliceIntentHandler(),
                        new SamuraiSessionEndedRequestHandler())
                .withSkillId(amazonSkillId)
                .build();
    }
}