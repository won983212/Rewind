package com.won983212.rewind.mixin;

import com.won983212.rewind.client.ClientDist;
import com.won983212.rewind.recorder.PacketInterceptor;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientHandshakePacketListenerImpl.class)
public class MixinClientHandshakePacketListener {

    @Shadow
    @Final
    private Connection connection;

    @Inject(method = "handleGameProfile", at = @At("HEAD"))
    private void onLogin(ClientboundGameProfilePacket packet, CallbackInfo ci) {
        if (!ClientDist.REPLAYER.isReplaying()) {
            connection.channel().pipeline()
                    .addBefore("packet_handler", "packet_recorder", new PacketInterceptor(ClientDist.RECORDER::handlePacket));
        }
    }
}
