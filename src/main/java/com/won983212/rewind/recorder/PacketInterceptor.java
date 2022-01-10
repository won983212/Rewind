package com.won983212.rewind.recorder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.network.protocol.Packet;

public class PacketInterceptor extends ChannelInboundHandlerAdapter {
    private final PacketWriter packetWriter;

    public PacketInterceptor(PacketWriter packetWriter) {
        this.packetWriter = packetWriter;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Packet<?>) {
            packetWriter.writePacket((Packet<?>) msg);
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}
