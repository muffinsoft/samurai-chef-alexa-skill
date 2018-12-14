package com.muffinsoft.alexa.skills.samuraichef.components;

public class BeltColorDefiner {

    public static String defineColor(int stripe) {
        switch (stripe) {
            case 0:
                return "white";
            case 1:
                return "yellow";
            case 2:
                return "orange";
            case 3:
                return "green";
            case 4:
                return "purple";
            default:
                return "white";
        }
    }
}
