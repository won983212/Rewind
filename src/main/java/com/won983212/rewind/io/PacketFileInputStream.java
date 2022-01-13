package com.won983212.rewind.io;

import io.netty.buffer.Unpooled;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class PacketFileInputStream {
    private final PacketByteBuffer buffer;


    public PacketFileInputStream(File file) throws IOException {
        FileChannel fileChannel = new FileInputStream(file).getChannel();
        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
        this.buffer = new PacketByteBuffer(Unpooled.wrappedBuffer(mappedByteBuffer));
    }

    public boolean isEmpty() {
        return buffer.isEmpty();
    }

    public PacketByteBuffer.PacketData read() throws IOException {
        return buffer.read();
    }
}
