package com.won983212.rewind.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Timer;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MixinMinecraft {
    @Accessor("pendingConnection")
    void setConnection(Connection connection);

    @Accessor("timer")
    Timer getTimer();
}
