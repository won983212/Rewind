package com.won983212.rewind;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.SkipPacketException;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;

public class PacketByteBuf {
    private final ByteBuf buffer;


    public PacketByteBuf() {
        this.buffer = Unpooled.buffer();
    }

    public PacketByteBuf(ByteBuf buffer) {
        this.buffer = buffer;
    }

    public byte[] getBytes() {
        byte[] data = new byte[buffer.readableBytes()];
        buffer.getBytes(buffer.readerIndex(), data);
        return data;
    }

    public PacketData read() {
        try {
            return readSilently(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasPacket() {
        return buffer.readableBytes() > 0;
    }


    public void write(Packet<?> packet, int tick) {
        try {
            writeTo(packet, buffer, tick);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PacketData readSilently(ByteBuf buffer) throws IOException {
        int tick = buffer.readInt();
        int packetSize = buffer.readInt();
        if (buffer.readableBytes() != 0) {
            ByteBuf data = buffer.readBytes(packetSize);
            FriendlyByteBuf friendlybytebuf = new FriendlyByteBuf(data);
            int id = friendlybytebuf.readVarInt();
            Packet<?> packet = ConnectionProtocol.PLAY.createPacket(PacketFlow.CLIENTBOUND, id, friendlybytebuf);
            if (packet == null) {
                throw new IOException("Bad packet id " + id);
            }
            return new PacketData(tick, packet);
        }
        return null;
    }

    private void writeTo(Packet<?> packet, ByteBuf buffer, int tick) throws IOException {
        Integer id = ConnectionProtocol.PLAY.getPacketId(PacketFlow.CLIENTBOUND, packet);
        if (id == null) {
            throw new IOException("Can't serialize unregistered packet: " + packet.getClass());
        } else {
            try {
                buffer.writeInt(tick);

                FriendlyByteBuf data = new FriendlyByteBuf(Unpooled.buffer());
                int size = data.writerIndex();

                data.writeVarInt(id);
                packet.write(data);
                size = data.writerIndex() - size;

                if (size > 8388608) {
                    throw new IllegalArgumentException("Packet too big (is " + size + ", should be less than 8388608): " + packet);
                }

                buffer.writeInt(size);
                buffer.writeBytes(data);
            } catch (Throwable throwable) {
                RewindMod.LOGGER.error("Error encoding packet", throwable);
                if (packet.isSkippable()) {
                    throw new SkipPacketException(throwable);
                } else {
                    throw throwable;
                }
            }
        }
    }

    public static void logPacket(Packet<?> packet, boolean minimize) {
        Minecraft mc = Minecraft.getInstance();
        StringBuilder sb = new StringBuilder();

        Entity ent = null;
        if (mc.level != null) {
            if (packet instanceof ClientboundMoveEntityPacket) {
                ent = ((ClientboundMoveEntityPacket) packet).getEntity(mc.level);
            } else if (packet instanceof ClientboundTeleportEntityPacket) {
                ent = mc.level.getEntity(((ClientboundTeleportEntityPacket) packet).getId());
            } else if (packet instanceof ClientboundRotateHeadPacket) {
                ent = ((ClientboundRotateHeadPacket) packet).getEntity(mc.level);
            } else if (packet instanceof ClientboundSetEntityMotionPacket) {
                ent = mc.level.getEntity(((ClientboundSetEntityMotionPacket) packet).getId());
            } else if (packet instanceof ClientboundEntityEventPacket) {
                ent = ((ClientboundEntityEventPacket) packet).getEntity(mc.level);
            } else if (packet instanceof ClientboundSetEntityDataPacket) {
                ent = mc.level.getEntity(((ClientboundSetEntityDataPacket) packet).getId());
            } else if (packet instanceof ClientboundUpdateAttributesPacket) {
                ent = mc.level.getEntity(((ClientboundUpdateAttributesPacket) packet).getEntityId());
            }
        }

        if (minimize && ent != null && !(ent instanceof Player)) {
            return;
        }

        sb.append(packet.getClass());

        if (ent != null) {
            sb.append('[');
            sb.append(ent.getClass().getName());
            sb.append(']');
        }
        RewindMod.LOGGER.info(sb.toString());
    }

    public static class PacketData {
        public final long tick;
        public final Packet<?> packet;

        public PacketData(long tick, Packet<?> packet) {
            this.tick = tick;
            this.packet = packet;
        }
    }
}
