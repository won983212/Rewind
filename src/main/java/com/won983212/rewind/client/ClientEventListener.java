package com.won983212.rewind.client;

import com.won983212.rewind.RewindMod;
import com.won983212.rewind.recorder.Recorder;
import com.won983212.rewind.ui.screen.RecordingStatusScreen;
import com.won983212.rewind.util.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.io.File;

@Mod.EventBusSubscriber(modid = RewindMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventListener {
    private static RecordingStatusScreen recordingStatusScreen;


    @SubscribeEvent
    public static void onLoggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        if (RewindMod.REPLAYER.isReplaying()) {
            RewindMod.REPLAYER.close();
        }
    }

    @SubscribeEvent
    public static void onIngameRender(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.CHAT) {
            return;
        }

        if (recordingStatusScreen != null) {
            Minecraft mc = Minecraft.getInstance();
            int x = (int) (mc.mouseHandler.xpos() * (double) mc.getWindow().getGuiScaledWidth() / (double) mc.getWindow().getScreenWidth());
            int y = (int) (mc.mouseHandler.ypos() * (double) mc.getWindow().getGuiScaledHeight() / (double) mc.getWindow().getScreenHeight());
            recordingStatusScreen.render(event.getMatrixStack(), x, y, event.getPartialTicks());
            if (recordingStatusScreen.isDestroyed()) {
                recordingStatusScreen = null;
            }
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
                    recordingStatusScreen.animateHide();
                } else {
                    chat.addMessage(Lang.getComponent("record_starting"));
                    RewindMod.RECORDER.startAsync()
                            .whenComplete(($1, $2) ->
                                    RewindMod.runAtMainThread(() -> {
                                        recordingStatusScreen = new RecordingStatusScreen();
                                        recordingStatusScreen.animateShow();
                                        chat.addMessage(Lang.getComponent("recording"));
                                    }))
                            .exceptionally((t) -> {
                                RewindMod.LOGGER.error(t);
                                return null;
                            });
                }
                return;
            }
            if (screen instanceof TitleScreen) {
                RewindMod.REPLAYER.startReplay(new File("C:/users/psvm/desktop/replay.pkt"));
            } else if (RewindMod.REPLAYER.isReplaying()) {
                RewindMod.REPLAYER.restart();
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
        RewindMod.RECORDER.onClientTick();
        RewindMod.REPLAYER.onClientTick();
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        RewindMod.RECORDER.onPlayerTick(e.player);
    }
}
