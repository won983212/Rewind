package com.won983212.rewind.io;

import com.won983212.rewind.RewindMod;
import com.won983212.rewind.util.Debug;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.SkipPacketException;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;

import java.io.IOException;

public class PacketByteBuffer {
    private final ByteBuf buffer;


    public PacketByteBuffer(ByteBuf buffer) {
        this.buffer = buffer;
    }

    public PacketByteBuffer() {
        this(Unpooled.buffer());
    }

    public void write(Packet<?> packet, int tick) throws IOException {
        Integer id = ConnectionProtocol.PLAY.getPacketId(PacketFlow.CLIENTBOUND, packet);
        if (id == null) {
            throw new IOException("Can't serialize unregistered packet: " + packet.getClass());
        } else {
            try {
                FriendlyByteBuf data = new FriendlyByteBuf(Unpooled.buffer());
                int size = data.writerIndex();

                data.writeInt(tick);
                data.writeVarInt(id);
                packet.write(data);

                size = data.writerIndex() - size;
                if (size > (1 << 23)) {
                    throw new IllegalArgumentException("Packet too big (is " + size + ", should be less than 8388608): " + packet);
                }

                Debug.logPacket(packet, true);
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

    public PacketData read() throws IOException {
        int packetSize = buffer.readInt();
        ByteBuf data = buffer.readBytes(packetSize);
        if (data.readableBytes() <= 0) {
            throw new IOException("Can't read packet");
        }

        FriendlyByteBuf friendlybytebuf = new FriendlyByteBuf(data);
        int tick = friendlybytebuf.readInt();
        int id = friendlybytebuf.readVarInt();
        Packet<?> packet = ConnectionProtocol.PLAY.createPacket(PacketFlow.CLIENTBOUND, id, friendlybytebuf);

        if (packet == null) {
            throw new IOException("Bad packet id " + id);
        }

        return new PacketData(tick, packet);
    }

    public boolean isEmpty() {
        return buffer.readableBytes() <= 0;
    }

    public ByteBuf getBuffer() {
        return buffer;
    }

    public static class PacketData {
        public final int tick;
        public final Packet<?> packet;

        public PacketData(int tick, Packet<?> packet) {
            this.tick = tick;
            this.packet = packet;
        }
    }
}
