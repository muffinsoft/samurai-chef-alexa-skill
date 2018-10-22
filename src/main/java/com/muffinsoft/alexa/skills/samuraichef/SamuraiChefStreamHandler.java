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
                        new SamuraiCancelIntentHandler(DependenciesContainer.provideCardManager(), DependenciesContainer.providePhraseManager()),
                        new SamuraiStopIntentHandler(DependenciesContainer.provideCardManager(), DependenciesContainer.providePhraseManager()),
                        new SamuraiFallbackIntentHandler(DependenciesContainer.provideCardManager(), DependenciesContainer.providePhraseManager()),
                        new SamuraiHelpIntentHandler(DependenciesContainer.provideCardManager(), DependenciesContainer.providePhraseManager()),
                        new SamuraiLaunchRequestHandler(DependenciesContainer.provideCardManager(), DependenciesContainer.providePhraseManager()),
                        new SamuraiActionIntentHandler(DependenciesContainer.providePhraseManager(), DependenciesContainer.provideActivitiesManager(), DependenciesContainer.provideCardManager(), DependenciesContainer.provideIngredientsManager()),
                        new SamuraiSessionEndedRequestHandler())
                .withSkillId(amazonSkillId)
                .build();
    }
}
