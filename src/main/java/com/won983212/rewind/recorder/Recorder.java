package com.won983212.rewind.recorder;

import com.won983212.rewind.PacketByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.function.Consumer;

public class Recorder implements PacketWriter {
    private PacketByteBuf buf;
    private boolean recording;
    private long tickTime;
    private PlayerRecorder clientPlayerRecorder;
    private final PacketHandler handshakePacketHandler;
    private boolean initialPosRecorded = false;


    public Recorder() {
        recording = false;
        handshakePacketHandler = new PacketHandler();
        handshakePacketHandler.addHandler(ClientboundLoginPacket.class, this::handleLoginPacket);
        handshakePacketHandler.addHandler(ClientboundPlayerInfoPacket.class, this::handlePlayerInfo);
    }

    public void start() {
        recording = true;
        tickTime = 0;
    }

    public void stop() {
        recording = false;
        buf = null;
        clientPlayerRecorder = null;
    }

    public void worldTick() {
        if (recording) {
            tickTime++;
        }
    }

    public void playerTick(Player player) {
        if (clientPlayerRecorder == null) {
            clientPlayerRecorder = new PlayerRecorder(player, 10, this);
        }
        clientPlayerRecorder.tick();
    }

    public void writePacket(Packet<?> packet) {
        if (packet instanceof ClientboundCustomPayloadPacket) {
            return;
        }
        if (!recording && !handshakePacketHandler.has(packet)) {
            return;
        }
        BlockableEventLoop<Runnable> mc = Minecraft.getInstance();
        if (!mc.isSameThread()) {
            Minecraft.getInstance().execute(() -> handlePacket(packet));
        } else {
            handlePacket(packet);
        }
    }

    private void handlePacket(Packet<?> packet) {
        if (!handshakePacketHandler.handle(packet)) {
            PacketByteBuf.logPacket(packet, true);
            if (packet instanceof ClientboundAnimatePacket) {
                if (((ClientboundAnimatePacket) packet).getAction() == 2) {
                    return;
                }
            }
            if (packet instanceof ClientboundPlayerPositionPacket) { // TODO 이건 다른 Dimension으로 갈 때 오류를 야기함
                if (!initialPosRecorded) {
                    initialPosRecorded = true;
                } else {
                    return;
                }
            }
            writePacketData(packet);
        }
    }

    private void handleLoginPacket(ClientboundLoginPacket packet) {
        buf = new PacketByteBuf();
        writePacketData(packet);
    }

    private void handlePlayerInfo(ClientboundPlayerInfoPacket packet) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (buf == null || player == null) {
            return;
        }
        writePacketData(packet);
        if (packet.getAction() == ClientboundPlayerInfoPacket.Action.ADD_PLAYER) {
            writePacketData(new ClientboundAddPlayerPacket(player));
            writePacketData(new ClientboundSetEntityDataPacket(player.getId(), player.getEntityData(), true));
        }
    }

    private void writePacketData(Packet<?> packet) {
        if (buf != null) {
            buf.write(packet, tickTime);
        }
    }

    public void save(File output) {
        if (buf != null) {
            try {
                FileOutputStream fos = new FileOutputStream(output);
                fos.write(buf.getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class PacketHandler {
        private final HashMap<Class<? extends Packet<?>>, Consumer<? extends Packet<?>>> handlers = new HashMap<>();

        public <T extends Packet<?>> void addHandler(Class<T> packetClass, Consumer<T> handler) {
            handlers.put(packetClass, handler);
        }

        public <T extends Packet<?>> boolean handle(T packet) {
            Consumer<T> handler = (Consumer<T>) handlers.get(packet.getClass());
            if (handler != null) {
                handler.accept(packet);
                return true;
            }
            return false;
        }

        public boolean has(Packet<?> packet) {
            return handlers.containsKey(packet.getClass());
        }
    }
}
