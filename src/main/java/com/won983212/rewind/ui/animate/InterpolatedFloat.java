package com.won983212.rewind.ui.animate;

import com.won983212.rewind.util.MathHelper;

public class InterpolatedFloat extends InterpolatedValue<Float> {

    public InterpolatedFloat(Float from, Float to, int durationTicks) {
        super(from, to, durationTicks);
    }

    @Override
    protected Float lerpValue(float progress) {
        return MathHelper.lerpFloat(from, to, progress);
    }
}
