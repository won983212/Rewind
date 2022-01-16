package com.won983212.rewind.client;

import com.won983212.rewind.RewindMod;
import com.won983212.rewind.recorder.Recorder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.io.File;

@Mod.EventBusSubscriber(modid = RewindMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventListener {

    @SubscribeEvent
    public static void onButtonClick(PlayerEvent.PlayerLoggedOutEvent event) {
        if (RewindMod.REPLAYER.isReplaying()) {
            RewindMod.REPLAYER.close();
        }
    }

    // TODO 이제 이 방식 말고 실제로 사용할 수 있도록 바꿔보자
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (event.getAction() == GLFW.GLFW_PRESS && event.getKey() == GLFW.GLFW_KEY_SEMICOLON) {
            Screen screen = Minecraft.getInstance().screen;
            if (screen == null && !RewindMod.REPLAYER.isReplaying()) {
                ChatComponent chat = Minecraft.getInstance().gui.getChat();
                if (RewindMod.RECORDER.isRecording()) {
                    RewindMod.RECORDER.stop();
                    chat.addMessage(new TextComponent("Record end."));
                } else {
                    RewindMod.RECORDER.start();
                    chat.addMessage(new TextComponent("Record start."));
                }
            }
            if (screen instanceof TitleScreen) {
                RewindMod.REPLAYER.startReplay(new File("C:/users/psvm/desktop/replay.pkt"));
            }
        }
    }

    @SubscribeEvent
    public static void onHandlePlayerRespawnPacket(ClientPlayerNetworkEvent.RespawnEvent e) {
        Player newPlayer = e.getNewPlayer();
        RewindMod.RECORDER.handlePacket(new ClientboundAddPlayerPacket(newPlayer));
        RewindMod.RECORDER.handlePacket(new ClientboundSetEntityDataPacket(newPlayer.getId(), newPlayer.getEntityData(), true));
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent e) {
        RewindMod.RECORDER.handlePacket(Recorder.makePlayerPositionPacket(e.getPlayer()));
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent e) {
        RewindMod.RECORDER.onWorldTick();
        RewindMod.REPLAYER.onWorldTick();
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        RewindMod.RECORDER.onPlayerTick(e.player);
    }
}
