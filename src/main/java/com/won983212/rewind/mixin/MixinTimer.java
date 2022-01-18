package com.won983212.rewind.mixin;

import net.minecraft.client.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Timer.class)
public interface MixinTimer {
    @Accessor("msPerTick")
    float getMsPerTicks();
}
