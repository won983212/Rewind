package com.won983212.rewind.util;

import com.won983212.rewind.RewindMod;
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
    private long time;


    public PacketByteBuf() {
        this.buffer = Unpooled.buffer();
        this.time = System.currentTimeMillis();
    }

    public PacketByteBuf(ByteBuf buffer) {
        this.buffer = buffer;
        this.time = System.currentTimeMillis();
    }

    public byte[] getBytes() {
        byte[] data = new byte[buffer.readableBytes()];
        buffer.getBytes(buffer.readerIndex(), data);
        return data;
    }

    public Packet<?> read() {
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

    public void write(Packet<?> packet) {
        try {
            writeTo(packet, buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Packet<?> readSilently(ByteBuf buffer) throws IOException {
        long time = buffer.readLong();
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int i = buffer.readableBytes();
        Packet<?> packet = null;
        if (i != 0) {
            FriendlyByteBuf friendlybytebuf = new FriendlyByteBuf(buffer);
            int j = friendlybytebuf.readVarInt();
            packet = ConnectionProtocol.PLAY.createPacket(PacketFlow.CLIENTBOUND, j, friendlybytebuf);
            if (packet == null) {
                throw new IOException("Bad packet id " + j);
            }
        }
        return packet;
    }

    private void writeTo(Packet<?> packet, ByteBuf buffer) throws IOException {
        buffer.writeLong(System.currentTimeMillis() - time);
        time = System.currentTimeMillis();

        Integer integer = ConnectionProtocol.PLAY.getPacketId(PacketFlow.CLIENTBOUND, packet);
        if (integer == null) {
            throw new IOException("Can't serialize unregistered packet: " + packet.getClass());
        } else {
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
}
