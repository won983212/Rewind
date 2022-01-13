package com.won983212.rewind.recorder;

import com.won983212.rewind.RewindMod;
import com.won983212.rewind.client.ClientDist;
import com.won983212.rewind.io.PacketFileOutputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.IOException;

public class Recorder {
    private final RecordInitializer recordInitializer;
    private PacketFileOutputStream packetWriter;
    private PlayerRecorder clientPlayerRecorder;
    private int tickTime;


    public Recorder() {
        recordInitializer = new RecordInitializer();
    }

    public void start() {
        if (!isRecording()) {
            try {
                tickTime = 0;
                packetWriter = new PacketFileOutputStream(new File("C:/users/psvm/desktop/replay.pkt"));
                recordInitializer.writeInitialPacket(packetWriter);
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

    public void worldTick() {
        if (isRecording()) {
            tickTime++;
        }
    }

    public void playerTick(Player player) {
        if (!isRecording()) {
            return;
        }
        if (clientPlayerRecorder == null || clientPlayerRecorder.isPlayerInvaild()) {
            clientPlayerRecorder = new PlayerRecorder(player, 10, this::handlePacket);
        }
        clientPlayerRecorder.tick();
    }

    // TODO (후순위) Chunk는 좀 더 효율적인 방법으로 load하도록
    // TODO 다른 dimension으로 가면 handle못함
    // TODO 잠자고 일어나면 텔레포트 (PlayerPositionPacket과 연관)
    public void handlePacket(Packet<?> packet) {
        if (!RecordPacketFilter.canHandle(packet)) {
            return;
        }
        if (!isRecording() && !RecordPacketFilter.isAlwaysHandlingPacket(packet)) {
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
        if (!isRecording() && recordInitializer.handleAlwaysActivePacket(packet)) {
            return;
        }
        if (packet instanceof ClientboundAnimatePacket) {
            if (((ClientboundAnimatePacket) packet).getAction() == 2) {
                return;
            }
        }
        writePacketToFile(packet);
    }

    private void writePacketToFile(Packet<?> packet) {
        try {
            if (packetWriter != null) {
                packetWriter.write(packet, tickTime);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
