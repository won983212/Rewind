package com.won983212.rewind.recorder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.network.protocol.Packet;

import java.util.function.Consumer;

public class PacketInterceptor extends ChannelInboundHandlerAdapter {
    private final Consumer<Packet<?>> packetHandler;

    public PacketInterceptor(Consumer<Packet<?>> packetHandler) {
        this.packetHandler = packetHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Packet<?>) {
            packetHandler.accept((Packet<?>) msg);
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}
