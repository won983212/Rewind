package com.won983212.rewind.io;

import io.netty.buffer.Unpooled;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class PacketFileInputStream {
    private final PacketByteBuffer buffer;


    public PacketFileInputStream(File file) throws IOException {
        InputStream is = new GZIPInputStream(new FileInputStream(file));
        this.buffer = new PacketByteBuffer(Unpooled.wrappedBuffer(is.readAllBytes()));
    }

    public boolean isEmpty() {
        return buffer.isEmpty();
    }

    public PacketByteBuffer.PacketData read() throws IOException {
        return buffer.read();
    }
}
