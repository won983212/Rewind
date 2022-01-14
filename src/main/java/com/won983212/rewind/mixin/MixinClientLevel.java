package com.won983212.rewind.mixin;

import com.won983212.rewind.client.ClientDist;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(ClientLevel.class)
public class MixinClientLevel {
    @Inject(method = "playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", at = @At("HEAD"))
    private void onPlaySound(Player player, double x, double y, double z, SoundEvent event, SoundSource source, float volume, float pitch, CallbackInfo ci) {
        if (isClientPlayer(player)) {
            ClientDist.RECORDER.handlePacket(new ClientboundSoundPacket(event, source, x, y, z, volume, pitch));
        }
    }

    @Inject(method = "levelEvent", at = @At("HEAD"))
    private void onLevelEvent(@Nullable Player player, int type, BlockPos pos, int data, CallbackInfo ci) {
        if (isClientPlayer(player)) {
            ClientDist.RECORDER.handlePacket(new ClientboundLevelEventPacket(type, pos, data, false));
        }
    }

    @Inject(method = "disconnect", at = @At("HEAD"))
    private void onDisconnect(CallbackInfo ci) {
        if (ClientDist.RECORDER.isRecording()) {
            ClientDist.RECORDER.stop();
        }
    }

    private boolean isClientPlayer(Player player) {
        return player == Minecraft.getInstance().player;
    }
}
