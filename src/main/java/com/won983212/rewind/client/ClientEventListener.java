package com.won983212.rewind.client;

import com.won983212.rewind.RewindMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.io.File;

@Mod.EventBusSubscriber(modid = RewindMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventListener {
    // TODO for test
    @SubscribeEvent
    public static void onGuiOpen(InputEvent.KeyInputEvent event) {
        if (event.getAction() == GLFW.GLFW_PRESS && event.getKey() == GLFW.GLFW_KEY_SEMICOLON) {
            Screen screen = Minecraft.getInstance().screen;
            if (screen == null) {
                ClientDist.RECORDER.save(new File("C:/users/psvm/desktop/replay.dat"));
                Minecraft.getInstance().gui.getChat().addMessage(new TextComponent("Record Saved."));
            }
            if (screen instanceof TitleScreen) {
                ClientDist.REPLAYER.startReplay(new File("C:/users/psvm/desktop/replay.dat"));
            }
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        ClientDist.RECORDER.start();
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        ClientDist.RECORDER.stop();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent e) {
        ClientDist.RECORDER.worldTick();
        ClientDist.REPLAYER.tick();
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        ClientDist.RECORDER.playerTick(e.player);
    }
}
