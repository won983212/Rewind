package com.won983212.rewind.io;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.protocol.Packet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class PacketFileOutputStream {
    private static final int FLUSH_BYTES = 1 << 20; // 1MB
    private final PacketByteBuffer buffer;
    private final FileOutputStream out;
    private final FileChannel channel;


    public PacketFileOutputStream(File file) throws FileNotFoundException {
        this.buffer = new PacketByteBuffer();
        this.out = new FileOutputStream(file);
        this.channel = out.getChannel();
    }

    public void write(PacketByteBuffer packets) throws IOException {
        packets.getBuffer().markReaderIndex();
        while (!packets.isEmpty()) {
            PacketByteBuffer.PacketData data = packets.read();
            write(data.packet, data.tick);
        }
        packets.getBuffer().resetReaderIndex();
    }

    // TODO (후순위) 좀 더 세련된 방식으로 buffering or 성능 개선 (in도 포함)
    // TODO (후순위) 압축도 필요할 것임.
    public void write(Packet<?> packet, int tick) throws IOException {
        buffer.write(packet, tick);
        if (buffer.getBuffer().readableBytes() > FLUSH_BYTES) {
            flush();
        }
    }

    public void close() throws IOException {
        flush();
        out.close();
        channel.close();
    }

    public void flush() throws IOException {
        ByteBuf buf = buffer.getBuffer();
        buf.readBytes(channel, buf.readableBytes());
    }
}
