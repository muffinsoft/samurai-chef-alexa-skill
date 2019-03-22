package com.muffinsoft.alexa.skills.samuraichef;

import com.amazon.ask.Skill;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.Skills;
import com.muffinsoft.alexa.sdk.handlers.ResponseExceptionHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiActionIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiActionOnlyIntentHanler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiCancelIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiFallbackIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiHelpIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiLaunchRequestHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiMenuIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiMissionNavigationIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiMissionNavigationOnlyIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiNavigateHomeIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiNoIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiResetIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiSelectPathIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiSelectPathOnlyIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiStartOverIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiStopIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiYesIntentHandler;

public class SamuraiChefStreamHandler extends SkillStreamHandler {

    public SamuraiChefStreamHandler() {
        super(getSkill());
    }

    private static Skill getSkill() {

        String amazonSkillId = System.getProperty("amazon-skill-id");

        return Skills.standard()
                .addRequestHandlers(
                        new SamuraiActionIntentHandler(IoC.provideIntentFactory()),
                        new SamuraiActionOnlyIntentHanler(IoC.provideIntentFactory()),
                        new SamuraiMissionNavigationIntentHandler(IoC.provideIntentFactory()),
                        new SamuraiMissionNavigationOnlyIntentHandler(IoC.provideIntentFactory()),
                        new SamuraiSelectPathIntentHandler(IoC.provideIntentFactory()),
                        new SamuraiSelectPathOnlyIntentHandler(IoC.provideIntentFactory()),
                        new SamuraiYesIntentHandler(IoC.provideIntentFactory()),
                        new SamuraiNoIntentHandler(IoC.provideIntentFactory()),
                        new SamuraiStartOverIntentHandler(IoC.provideSettingsDependencies(), IoC.providePhraseDependencies()),
                        new SamuraiNavigateHomeIntentHandler(IoC.provideSettingsDependencies(), IoC.providePhraseDependencies()),
                        new SamuraiMenuIntentHandler(IoC.provideSettingsDependencies(), IoC.providePhraseDependencies()),
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
