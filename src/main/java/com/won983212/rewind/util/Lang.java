package com.won983212.rewind.util;

import com.won983212.rewind.RewindMod;
import com.won983212.rewind.mixin.MixinMinecraft;
import com.won983212.rewind.mixin.MixinTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.TranslatableComponent;

public class Lang {
    private static float msPerTicks = 0;

    public static String getString(String childKey, Object... args) {
        return String.format(Language.getInstance().getOrDefault(RewindMod.MODID + "." + childKey), args);
    }

    public static TranslatableComponent getComponent(String childKey, Object... args) {
        return new TranslatableComponent(RewindMod.MODID + "." + childKey, args);
    }

    public static String tickToTimeString(int ticks) {
        if (msPerTicks == 0) {
            MixinTimer timer = (MixinTimer) ((MixinMinecraft) Minecraft.getInstance()).getTimer();
            msPerTicks = timer.getMsPerTicks();
        }
        int seconds = (int) ((ticks / msPerTicks) % 60);
        int minutes = (int) ((ticks / (msPerTicks * 60)) % 60);
        int hours = (int) (ticks / (msPerTicks * 60 * 60));
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
