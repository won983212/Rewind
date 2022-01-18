package com.won983212.rewind.util;

public class MathHelper {
    public static double lerpDouble(double from, double to, float ratio) {
        if (ratio < 0) {
            ratio = 0;
        }
        if (ratio > 1) {
            ratio = 1;
        }
        return from + (to - from) * ratio;
    }

    public static float lerpFloat(float from, float to, float ratio) {
        if (ratio < 0) {
            ratio = 0;
        }
        if (ratio > 1) {
            ratio = 1;
        }
        return from + (to - from) * ratio;
    }
}
