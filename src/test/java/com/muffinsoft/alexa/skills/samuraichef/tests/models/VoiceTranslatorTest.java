package com.muffinsoft.alexa.skills.samuraichef.tests.models;

import com.muffinsoft.alexa.sdk.enums.SpeechType;
import com.muffinsoft.alexa.sdk.model.Speech;
import com.muffinsoft.alexa.skills.samuraichef.components.VoiceTranslator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VoiceTranslatorTest {

    @Test
    void test() {
        Speech translate = VoiceTranslator.translate("test", "Kiara");
        Assertions.assertEquals(translate.getType(), SpeechType.JOANNA);
    }
}
