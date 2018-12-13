package com.muffinsoft.alexa.skills.samuraichef.tests.handlers;

import com.amazon.ask.model.Intent;
import com.amazon.ask.model.Slot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

class GameIntentHandler {

    @Test
    void testField() throws NoSuchFieldException, IllegalAccessException {

        Intent intent = Intent.builder().build();
        Map<String, Slot> slots = Collections.emptyMap();

        Field field = intent.getClass().getDeclaredField("slots");
        field.setAccessible(true);
        field.set(intent, slots);

        Assertions.assertEquals(intent.getSlots(), slots);
    }
}
