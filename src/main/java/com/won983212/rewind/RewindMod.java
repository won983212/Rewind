package com.won983212.rewind;

import com.won983212.rewind.mixin.MixinMinecraft;
import com.won983212.rewind.util.PacketByteBuf;
import com.won983212.rewind.util.Lang;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.AttributeKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

@Mod(RewindMod.MODID)
public class RewindMod {
    public static final String MODID = "rewind";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    private static PacketByteBuf buf;

    public RewindMod() {
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("Rewind mod init.");
    }

    public static void writePacket(Packet<?> packet, PacketListener listener) {
        if (listener instanceof ServerLoginPacketListenerImpl) {
            return;
        }
        if (listener instanceof ServerGamePacketListenerImpl) {
            return;
        }
        if (packet instanceof ClientboundCustomPayloadPacket) {
            return;
        }
        LOGGER.info(packet.getClass());
        if (packet instanceof ClientboundPlayerInfoPacket) {
            ClientboundPlayerInfoPacket pk = (ClientboundPlayerInfoPacket) packet;
            if (pk.getAction() == ClientboundPlayerInfoPacket.Action.ADD_PLAYER) {
                if (buf != null) {
                    LocalPlayer player = Minecraft.getInstance().player;
                    if (player != null) {
                        buf.write(new ClientboundAddPlayerPacket(player));
                        buf.write(new ClientboundSetEntityDataPacket(player.getId(), player.getEntityData(), true));
                        return;
                    }
                }
            }
        }
        if (packet instanceof ClientboundLoginPacket) {
            buf = new PacketByteBuf();
        }
        if (buf != null) {
            buf.write(packet);
        }
    }

    @SubscribeEvent
    public void onGuiOpen(ScreenEvent.MouseClickedEvent.Post event) {
        if (event.getScreen() instanceof PauseScreen) {
            if (buf != null) {
                try {
                    FileOutputStream fos = new FileOutputStream("C:/users/psvm/desktop/replay.dat");
                    fos.write(buf.getBytes());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (event.getScreen() instanceof TitleScreen) {
            doLoad();
        }
    }

    private void doLoad() {
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

        RewindChannelHandler handler = new RewindChannelHandler();
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.pipeline().addLast("rewind_packet_handler", handler)
                .addLast("packet_handler", connection)
                .fireChannelActive();
        channel.attr(AttributeKey.valueOf("fml:netversion")).set(NetworkConstants.FMLNETMARKER);

        channel.writeInbound(new ClientboundGameProfilePacket(mc.getUser().getGameProfile()));

        new Thread(() -> {
            try {
                File file = new File("C:/users/psvm/desktop/replay.dat");
                FileInputStream fileInputStream = new FileInputStream(file);
                FileChannel fileChannel = fileInputStream.getChannel();
                MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());

                ByteBuf byteBuf = Unpooled.wrappedBuffer(mappedByteBuffer);
                PacketByteBuf buf = new PacketByteBuf(byteBuf);

                while (buf.hasPacket()) {
                    Packet<?> packet = buf.read();
                    channel.writeInbound(packet);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "rewind-worker-thread").start();

        ((MixinMinecraft) mc).setConnection(connection);
    }
}
