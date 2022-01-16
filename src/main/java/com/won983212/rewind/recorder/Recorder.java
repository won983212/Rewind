package com.won983212.rewind.recorder;

import com.won983212.rewind.RewindMod;
import com.won983212.rewind.io.PacketFileOutputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class Recorder {
    private final RecordHeaderWriter recordHeaderWriter;
    private PacketFileOutputStream packetWriter;
    private PlayerRecorder clientPlayerRecorder;
    private int tickTime;
    private CompletableFuture<Void> preparingTask;


    public Recorder() {
        recordHeaderWriter = new RecordHeaderWriter();
    }

    public CompletableFuture<Void> startAsync() {
        if (Minecraft.getInstance().level == null) {
            return CompletableFuture.failedFuture(new RecordingFailedException("Level is null"));
        }
        if (preparingTask != null) {
            return CompletableFuture.failedFuture(new RecordingFailedException("Already starting."));
        }
        if (!isRecording()) {
            tickTime = 0;
            try {
                packetWriter = new PacketFileOutputStream(new File("C:/users/psvm/desktop/replay.pkt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            preparingTask = CompletableFuture.runAsync(() -> {
                try {
                    recordHeaderWriter.writeHeaderPacket(packetWriter);
                } catch (IOException e) {
                    throw new RecordingFailedException(e);
                }
            });
            preparingTask.whenComplete(($1, $2) -> preparingTask = null);
        } else {
            return CompletableFuture.failedFuture(new RecordingFailedException("Already recording."));
        }
        return preparingTask;
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
        return packetWriter != null && preparingTask == null;
    }

    public void onClientTick() {
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
        if (RewindMod.REPLAYER.isReplaying()) {
            return;
        }
        RewindMod.runAtMainThread(() -> writePacket(packet));
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
