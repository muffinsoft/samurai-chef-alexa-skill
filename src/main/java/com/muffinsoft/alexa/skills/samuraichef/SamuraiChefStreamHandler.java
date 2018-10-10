package com.muffinsoft.alexa.skills.samuraichef;

import com.amazon.ask.Skill;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.Skills;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SushiSliceIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.CancelandStopIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.HelpIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.LaunchRequestHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SessionEndedRequestHandler;

public class SamuraiChefStreamHandler extends SkillStreamHandler {

    public SamuraiChefStreamHandler() {
        super(getSkill());
    }

    private static Skill getSkill() {

        String amazonSkillId = System.getProperty("amazon-skill-id");

        return Skills.standard()
                .addRequestHandlers(
                        new CancelandStopIntentHandler(),
                        new HelpIntentHandler(),
                        new LaunchRequestHandler(),
                        new SushiSliceIntentHandler(),
                        new SessionEndedRequestHandler())
                .withSkillId(amazonSkillId)
                .build();
    }
}
