package com.muffinsoft.alexa.skills.samuraichef;

import com.amazon.ask.Skill;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.Skills;
import com.muffinsoft.alexa.sdk.handlers.ResponseExceptionHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiActionIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiCancelIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiFallbackIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiHelpIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiLaunchRequestHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiResetIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiSessionEndedRequestHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiStopIntentHandler;

public class SamuraiChefStreamHandler extends SkillStreamHandler {

    public SamuraiChefStreamHandler() {
        super(getSkill());
    }

    private static Skill getSkill() {

        String amazonSkillId = System.getProperty("amazon-skill-id");

        return Skills.standard()
                .addRequestHandlers(
                        new SamuraiActionIntentHandler(IoC.provideConfigurationContainer(), IoC.provideSessionStateFabric()),
                        new SamuraiCancelIntentHandler(IoC.provideConfigurationContainer()),
                        new SamuraiFallbackIntentHandler(IoC.provideConfigurationContainer()),
                        new SamuraiHelpIntentHandler(IoC.provideConfigurationContainer()),
                        new SamuraiLaunchRequestHandler(IoC.provideConfigurationContainer()),
                        new SamuraiStopIntentHandler(IoC.provideConfigurationContainer()),
                        new SamuraiResetIntentHandler(IoC.provideConfigurationContainer()),
                        new SamuraiSessionEndedRequestHandler())
                .addExceptionHandler(new ResponseExceptionHandler())
                .withSkillId(amazonSkillId)
                .withTableName("samurai-chef")
                .build();
    }
}
