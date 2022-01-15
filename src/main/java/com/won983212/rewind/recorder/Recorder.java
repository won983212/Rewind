package com.won983212.rewind.recorder;

import com.won983212.rewind.RewindMod;
import com.won983212.rewind.client.ClientDist;
import com.won983212.rewind.io.PacketFileOutputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class Recorder {
    private final RecordHeaderWriter recordHeaderWriter;
    private PacketFileOutputStream packetWriter;
    private PlayerRecorder clientPlayerRecorder;
    private int tickTime;


    public Recorder() {
        recordHeaderWriter = new RecordHeaderWriter();
    }

    public void start() {
        if (Minecraft.getInstance().level == null) {
            RewindMod.LOGGER.warn("Level is null");
            return;
        }
        if (!isRecording()) {
            try {
                tickTime = 0;
                packetWriter = new PacketFileOutputStream(new File("C:/users/psvm/desktop/replay.pkt"));
                recordHeaderWriter.writeHeaderPacket(packetWriter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            RewindMod.LOGGER.warn("Already recording.");
        }
    }

    public void stop() {
        if (isRecording()) {
            try {
                packetWriter.close();
                packetWriter = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            RewindMod.LOGGER.warn("Recording did not start.");
        }
    }

    public boolean isRecording() {
        return packetWriter != null;
    }

    public void onWorldTick() {
        if (isRecording()) {
            tickTime++;
        }
    }

    public void onPlayerTick(Player player) {
        if (!isRecording()) {
            return;
        }
        if (clientPlayerRecorder == null || clientPlayerRecorder.isPlayerInvaild()) {
            clientPlayerRecorder = new PlayerRecorder(player, 10, this::handlePacket);
        }
        clientPlayerRecorder.tick();
    }

    // TODO (후순위) Chunk는 좀 더 효율적인 방법으로 load하도록
    // TODO (후순위) 죽었다 살아나면 다시 chunk로딩한다.
    public void handlePacket(Packet<?> packet) {
        if (!RecordPacketFilter.canHandle(packet)) {
            return;
        }
        if (!isRecording() && !RecordPacketFilter.isHeaderPacket(packet)) {
            return;
        }
        if (ClientDist.REPLAYER.isReplaying()) {
            return;
        }
        BlockableEventLoop<Runnable> mc = Minecraft.getInstance();
        if (!mc.isSameThread()) {
            Minecraft.getInstance().execute(() -> writePacket(packet));
        } else {
            writePacket(packet);
        }
    }

    private void writePacket(Packet<?> packet) {
        if (!isRecording() && recordHeaderWriter.handleHeaderPacket(packet)) {
            return;
        }
        if (packet instanceof ClientboundAnimatePacket animatePacket) {
            if (animatePacket.getAction() == 2) {
                return;
            }
        }
        if (packet instanceof ClientboundPlayerPositionPacket positionPacket) {
            if (positionPacket.getId() != -1) {
                return;
            }
        }
        if (packet instanceof ClientboundRespawnPacket respawnPacket) {
            Player player = Minecraft.getInstance().player;
            if (player != null && respawnPacket.getDimension() == player.getLevel().dimension()) {
                return;
            }
        }
        try {
            if (packetWriter != null) {
                packetWriter.write(packet, tickTime);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ClientboundPlayerPositionPacket makePlayerPositionPacket(@Nonnull Player player) {
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        float yRot = player.getYRot();
        float xRot = player.getXRot();
        return new ClientboundPlayerPositionPacket(x, y, z, yRot, xRot, Collections.emptySet(), -1, false);
    }
}
