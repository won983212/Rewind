package com.won983212.rewind.replayer;

import com.won983212.rewind.PacketByteBuf;
import com.won983212.rewind.mixin.MixinMinecraft;
import com.won983212.rewind.util.Lang;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.AttributeKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import net.minecraftforge.network.NetworkConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Replayer {
    private EmbeddedChannel channel;
    private RewindChannelHandler channelHandler;
    private PacketByteBuf packetBuffer;
    private Packet<?> packetQueue;
    private long tickTime;
    private long nextSendTime;


    public void startReplay(File input) {
        Minecraft mc = Minecraft.getInstance();
        GenericDirtMessageScreen screen = new GenericDirtMessageScreen(Lang.getComponent("connection.load"));
        mc.setScreen(screen);

        Connection connection = new Connection(PacketFlow.CLIENTBOUND) {
            @Override
            public void exceptionCaught(ChannelHandlerContext p_129533_, Throwable t) {
                t.printStackTrace();
            }
        };

        connection.setListener(new ClientHandshakePacketListenerImpl(connection, mc, null, $ -> {
        }));

        channelHandler = new RewindChannelHandler();
        channel = new EmbeddedChannel();
        channel.pipeline().addLast("rewind_packet_handler", channelHandler)
                .addLast("packet_handler", connection)
                .fireChannelActive();
        channel.attr(AttributeKey.valueOf("fml:netversion")).set(NetworkConstants.FMLNETMARKER);
        channel.writeInbound(new ClientboundGameProfilePacket(mc.getUser().getGameProfile()));
        ((MixinMinecraft) mc).setConnection(connection);

        packetQueue = null;
        tickTime = 0;
        nextSendTime = 0;

        try {
            FileChannel fileChannel = new FileInputStream(input).getChannel();
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, input.length());
            packetBuffer = new PacketByteBuf(Unpooled.wrappedBuffer(mappedByteBuffer));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isReplaying(){
        return packetBuffer != null;
    }

    public void tick() {
        if (!isReplaying() || nextSendTime > ++tickTime) {
            return;
        }
        if (packetQueue != null) {
            channel.writeInbound(packetQueue);
            PacketByteBuf.logPacket(packetQueue, true);
            packetQueue = null;
        }
        while (packetBuffer.hasPacket()) {
            PacketByteBuf.PacketData packet = packetBuffer.read();
            if (packet != null) {
                if (packet.tick <= tickTime) {
                    channel.writeInbound(packet.packet);
                    PacketByteBuf.logPacket(packet.packet, true);
                } else {
                    packetQueue = packet.packet;
                    nextSendTime = packet.tick;
                    break;
                }
            } else {
                break;
            }
        }
    }

    public void close(){
        Minecraft mc = Minecraft.getInstance();
        packetBuffer = null;
        packetQueue = null;
        channelHandler.close();
        channel.close().awaitUninterruptibly();
        if (mc.level != null) {
            mc.clearLevel();
        }
        mc.setScreen(null);
    }
}
