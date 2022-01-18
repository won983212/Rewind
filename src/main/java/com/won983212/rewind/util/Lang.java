package com.won983212.rewind.util;

import com.won983212.rewind.RewindMod;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.TranslatableComponent;

public class Lang {
    public static String getString(String childKey, Object... args) {
        return String.format(Language.getInstance().getOrDefault(RewindMod.MODID + "." + childKey), args);
    }

    public static TranslatableComponent getComponent(String childKey, Object... args) {
        return new TranslatableComponent(RewindMod.MODID + "." + childKey, args);
    }
}
