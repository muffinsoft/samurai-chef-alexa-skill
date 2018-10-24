package com.muffinsoft.alexa.skills.samuraichef;

import com.amazon.ask.Skill;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.Skills;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiActionIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiCancelIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiFallbackIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiHelpIntentHandler;
import com.muffinsoft.alexa.skills.samuraichef.handlers.SamuraiLaunchRequestHandler;
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
                        new SamuraiCancelIntentHandler(IoC.provideCardManager(), IoC.providePhraseManager()),
                        new SamuraiStopIntentHandler(IoC.provideCardManager(), IoC.providePhraseManager()),
                        new SamuraiFallbackIntentHandler(IoC.provideCardManager(), IoC.providePhraseManager()),
                        new SamuraiHelpIntentHandler(IoC.provideCardManager(), IoC.providePhraseManager()),
                        new SamuraiLaunchRequestHandler(IoC.provideCardManager(), IoC.providePhraseManager()),
                        new SamuraiActionIntentHandler(IoC.providePhraseManager(), IoC.provideActivitiesManager(), IoC.provideCardManager(), IoC.provideIngredientsManager(), IoC.providePowerUpsManager()),
                        new SamuraiSessionEndedRequestHandler())
                .withSkillId(amazonSkillId)
                .build();
    }
}
