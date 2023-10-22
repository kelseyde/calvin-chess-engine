package com.kelseyde.calvin.utils;

public class MathUtils {

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

}
