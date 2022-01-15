package com.won983212.rewind.client;

import com.won983212.rewind.RewindMod;
import com.won983212.rewind.recorder.Recorder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.io.File;

@Mod.EventBusSubscriber(modid = RewindMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventListener {

    // TODO 이제 이 방식 말고 실제로 사용할 수 있도록 바꿔보자
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (event.getAction() == GLFW.GLFW_PRESS && event.getKey() == GLFW.GLFW_KEY_SEMICOLON) {
            Screen screen = Minecraft.getInstance().screen;
            if (screen == null && !ClientDist.REPLAYER.isReplaying()) {
                ChatComponent chat = Minecraft.getInstance().gui.getChat();
                if (ClientDist.RECORDER.isRecording()) {
                    ClientDist.RECORDER.stop();
                    chat.addMessage(new TextComponent("Record end."));
                } else {
                    ClientDist.RECORDER.start();
                    chat.addMessage(new TextComponent("Record start."));
                }
            }
            if (screen instanceof TitleScreen) {
                ClientDist.REPLAYER.startReplay(new File("C:/users/psvm/desktop/replay.pkt"));
            }
        }
    }

    @SubscribeEvent
    public static void onHandlePlayerRespawnPacket(ClientPlayerNetworkEvent.RespawnEvent e) {
        Player newPlayer = e.getNewPlayer();
        ClientDist.RECORDER.handlePacket(new ClientboundAddPlayerPacket(newPlayer));
        ClientDist.RECORDER.handlePacket(new ClientboundSetEntityDataPacket(newPlayer.getId(), newPlayer.getEntityData(), true));
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent e) {
        ClientDist.RECORDER.handlePacket(Recorder.makePlayerPositionPacket(e.getPlayer()));
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent e) {
        ClientDist.RECORDER.onWorldTick();
        ClientDist.REPLAYER.onWorldTick();
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        ClientDist.RECORDER.onPlayerTick(e.player);
    }
}
