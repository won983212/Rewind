package com.won983212.rewind.ui.animate;

import com.won983212.rewind.util.Color;
import com.won983212.rewind.util.MathHelper;

public class InterpolatedColor extends InterpolatedValue<Color> {

    public InterpolatedColor(Color from, Color to, int durationTicks) {
        super(from, to, durationTicks);
    }

    @Override
    protected Color lerpValue(float progress) {
        return lerp(from, to, progress);
    }

    public static Color lerp(Color from, Color to, float ratio) {
        int a = (int) MathHelper.lerpDouble(from.getAlpha(), to.getAlpha(), ratio);
        int r = (int) MathHelper.lerpDouble(from.getRed(), to.getRed(), ratio);
        int g = (int) MathHelper.lerpDouble(from.getGreen(), to.getGreen(), ratio);
        int b = (int) MathHelper.lerpDouble(from.getBlue(), to.getBlue(), ratio);
        return Color.of(a, r, g, b);
    }
}
