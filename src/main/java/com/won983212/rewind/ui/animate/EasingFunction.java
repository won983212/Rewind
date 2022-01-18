package com.won983212.rewind.ui.animate;

import java.util.function.Function;

public enum EasingFunction {
    NO_EASE((x) -> x),
    QUAD_IN((x) -> x * x),
    QUAD_OUT((x) -> 1 - (1 - x) * (1 - x));


    private final Function<Float, Float> func;

    EasingFunction(Function<Float, Float> func) {
        this.func = func;
    }

    public Float ease(Float progress) {
        return func.apply(progress);
    }
}
