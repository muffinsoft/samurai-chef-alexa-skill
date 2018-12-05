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
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiStopIntentHandler;

public class SamuraiChefStreamHandler extends SkillStreamHandler {

    public SamuraiChefStreamHandler() {
        super(getSkill());
    }

    private static Skill getSkill() {

        String amazonSkillId = System.getProperty("amazon-skill-id");

        return Skills.standard()
                .addRequestHandlers(
                        new SamuraiActionIntentHandler(IoC.provideSettingsDependencies(), IoC.providePhraseDependencies(), IoC.provideSessionStateFabric()),
                        new SamuraiCancelIntentHandler(IoC.provideSettingsDependencies(), IoC.providePhraseDependencies()),
                        new SamuraiFallbackIntentHandler(IoC.provideSettingsDependencies(), IoC.providePhraseDependencies()),
                        new SamuraiHelpIntentHandler(IoC.provideSettingsDependencies(), IoC.providePhraseDependencies()),
                        new SamuraiLaunchRequestHandler(IoC.provideSettingsDependencies(), IoC.providePhraseDependencies()),
                        new SamuraiStopIntentHandler(IoC.provideSettingsDependencies(), IoC.providePhraseDependencies()),
                        new SamuraiResetIntentHandler(IoC.provideSettingsDependencies(), IoC.providePhraseDependencies())
                )
                .addExceptionHandler(new ResponseExceptionHandler())
                .withSkillId(amazonSkillId)
                .withTableName("samurai-chef")
                .build();
    }
}
