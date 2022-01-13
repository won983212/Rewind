package com.won983212.rewind.replayer;

import com.won983212.rewind.io.PacketByteBuffer;
import com.won983212.rewind.io.PacketFileInputStream;
import com.won983212.rewind.mixin.MixinMinecraft;
import com.won983212.rewind.util.Debug;
import com.won983212.rewind.util.Lang;
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
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class Replayer {
    private EmbeddedChannel channel;
    private RewindChannelHandler channelHandler;
    private PacketFileInputStream packetReader;
    private Packet<?> packetQueue;
    private long tickTime;
    private long nextSendTime;


    public void startReplay(File input) {
        Minecraft mc = Minecraft.getInstance();
        GenericDirtMessageScreen screen = new GenericDirtMessageScreen(Lang.getComponent("connection.load"));
        mc.setScreen(screen);

        Connection connection = new Connection(PacketFlow.CLIENTBOUND) {
            @Override
            public void exceptionCaught(@NotNull ChannelHandlerContext p_129533_, Throwable t) {
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
            packetReader = new PacketFileInputStream(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isReplaying() {
        return packetReader != null;
    }

    public void tick() {
        if (!isReplaying() || nextSendTime > ++tickTime) {
            return;
        }
        if (packetQueue != null) {
            channel.writeInbound(packetQueue);
            Debug.logPacket(packetQueue, true);
            packetQueue = null;
        }
        try {
            while (!packetReader.isEmpty()) {
                PacketByteBuffer.PacketData packet = packetReader.read();
                if (packet != null) {
                    if (packet.tick <= tickTime) {
                        channel.writeInbound(packet.packet);
                        Debug.logPacket(packet.packet, true);
                    } else {
                        packetQueue = packet.packet;
                        nextSendTime = packet.tick;
                        break;
                    }
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        Minecraft mc = Minecraft.getInstance();
        packetReader = null;
        packetQueue = null;
        channelHandler.close();
        channel.close().awaitUninterruptibly();
        if (mc.level != null) {
            mc.clearLevel();
        }
        mc.setScreen(null);
    }
}
