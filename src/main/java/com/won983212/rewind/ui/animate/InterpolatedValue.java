package com.won983212.rewind.ui.animate;

import com.won983212.rewind.util.MathHelper;

public abstract class InterpolatedValue<T> {

    public enum LoopingType {
        NO_LOOP, RESET_LOOP, CONTINUOUS_LOOP
    }

    protected T from;
    protected T to;
    private LoopingType looping;
    private int duration;
    private float progress;
    private float lastProgress;
    private EasingFunction easing;
    private Runnable onComplete;

    /**
     * @param durationTicks the animation is reversed if it has a negative value.
     *                      the animation stops if it is zero.
     */
    public InterpolatedValue(T from, T to, int durationTicks) {
        this.from = from;
        this.to = to;
        this.duration = durationTicks;
        this.progress = this.lastProgress = 0;
        this.looping = LoopingType.NO_LOOP;
        this.easing = EasingFunction.NO_EASE;
    }

    protected abstract T lerpValue(float progress);

    public InterpolatedValue<T> setLooping(LoopingType looping) {
        this.looping = looping;
        return this;
    }

    public InterpolatedValue<T> setEasing(EasingFunction easing) {
        this.easing = easing;
        return this;
    }

    public InterpolatedValue<T> setOnComplete(Runnable event) {
        this.onComplete = event;
        return this;
    }

    public T getValue(float partialTime) {
        float p = MathHelper.lerpFloat(lastProgress, progress, partialTime);
        return lerpValue(easing.ease(p));
    }

    public T tickAndGet(float partialTime) {
        tick();
        return getValue(partialTime);
    }

    public void tick() {
        if (duration == 0) {
            return;
        }

        lastProgress = progress;
        progress += 1.0 / duration;

        int exceed = -1;
        if (progress > 1) {
            exceed = 1;
        } else if (progress < 0) {
            exceed = 0;
        }

        if (exceed != -1) {
            if (looping == LoopingType.NO_LOOP) {
                progress = exceed;
                if (onComplete != null) {
                    onComplete.run();
                }
            } else if (looping == LoopingType.RESET_LOOP) {
                progress = ~exceed;
            } else if (looping == LoopingType.CONTINUOUS_LOOP) {
                progress = exceed;
                duration *= -1;
            }
        }
    }
}
