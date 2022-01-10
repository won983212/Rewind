package com.won983212.rewind;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.SkipPacketException;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;

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

    public void write(Packet<?> packet, long tick) {
        try {
            writeTo(packet, buffer, tick);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PacketData readSilently(ByteBuf buffer) throws IOException {
        long tick = buffer.readLong();
        int i = buffer.readableBytes();
        if (i != 0) {
            FriendlyByteBuf friendlybytebuf = new FriendlyByteBuf(buffer);
            int j = friendlybytebuf.readVarInt();
            Packet<?> packet = ConnectionProtocol.PLAY.createPacket(PacketFlow.CLIENTBOUND, j, friendlybytebuf);
            if (packet == null) {
                throw new IOException("Bad packet id " + j);
            }
            return new PacketData(tick, packet);
        }
        return null;
    }

    private void writeTo(Packet<?> packet, ByteBuf buffer, long tick) throws IOException {
        Integer integer = ConnectionProtocol.PLAY.getPacketId(PacketFlow.CLIENTBOUND, packet);
        if (integer == null) {
            throw new IOException("Can't serialize unregistered packet: " + packet.getClass());
        } else {
            buffer.writeLong(tick);
            FriendlyByteBuf friendlybytebuf = new FriendlyByteBuf(buffer);
            friendlybytebuf.writeVarInt(integer);
            try {
                int i = friendlybytebuf.writerIndex();
                packet.write(friendlybytebuf);
                int j = friendlybytebuf.writerIndex() - i;
                if (j > 8388608) {
                    throw new IllegalArgumentException("Packet too big (is " + j + ", should be less than 8388608): " + packet);
                }
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

    public static class PacketData {
        public final long tick;
        public final Packet<?> packet;

        public PacketData(long tick, Packet<?> packet) {
            this.tick = tick;
            this.packet = packet;
        }
    }
}
