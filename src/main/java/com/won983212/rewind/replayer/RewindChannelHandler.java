package com.won983212.rewind.replayer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.world.level.GameType;

public class RewindChannelHandler extends ChannelInboundHandlerAdapter {
    private ChannelHandlerContext ctx;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        super.channelActive(ctx);
    }

    @SuppressWarnings("PatternVariableCanBeUsed")
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof Packet<?>)) {
            super.channelRead(ctx, msg);
            return;
        }

        if (msg instanceof ClientboundLoginPacket) {
            ClientboundLoginPacket packet = (ClientboundLoginPacket) msg;
            msg = new ClientboundLoginPacket(-123, false, GameType.SPECTATOR, null, packet.levels(),
                    packet.registryHolder(), packet.dimensionType(), packet.dimension(), packet.seed(),
                    0, packet.chunkRadius(), packet.simulationDistance(), packet.reducedDebugInfo(),
                    packet.showDeathScreen(), packet.isDebug(), packet.isFlat());
        }

        if (msg instanceof ClientboundGameEventPacket) {
            ClientboundGameEventPacket packet = (ClientboundGameEventPacket) msg;
            if (packet.getEvent() == ClientboundGameEventPacket.CHANGE_GAME_MODE) {
                msg = null;
            }
        }

        if (msg instanceof ClientboundRespawnPacket) {
            ClientboundRespawnPacket packet = (ClientboundRespawnPacket) msg;
            msg = new ClientboundRespawnPacket(packet.getDimensionType(), packet.getDimension(), packet.getSeed(),
                    GameType.SPECTATOR, null, packet.isDebug(), packet.isFlat(), packet.shouldKeepAllPlayerData());
        }

        if (msg != null) {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public void close() {
        try {
            channelInactive(ctx);
            ctx.channel().pipeline().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
