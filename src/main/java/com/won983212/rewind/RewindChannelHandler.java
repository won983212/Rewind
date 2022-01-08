package com.won983212.rewind;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.world.level.GameType;

public class RewindChannelHandler extends ChannelDuplexHandler {
    private ChannelHandlerContext context;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.context = ctx;
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof Packet<?>)) {
            super.channelRead(ctx, msg);
            return;
        }

        if (msg instanceof ClientboundLoginPacket) {
            ClientboundLoginPacket loginPacket = (ClientboundLoginPacket) msg;
            msg = new ClientboundLoginPacket(-1, false, GameType.SPECTATOR, GameType.SPECTATOR, loginPacket.levels(),
                    loginPacket.registryHolder(), loginPacket.dimensionType(), loginPacket.dimension(), loginPacket.seed(),
                    0, loginPacket.chunkRadius(), loginPacket.simulationDistance(), loginPacket.reducedDebugInfo(),
                    loginPacket.showDeathScreen(), loginPacket.isDebug(), loginPacket.isFlat());
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
