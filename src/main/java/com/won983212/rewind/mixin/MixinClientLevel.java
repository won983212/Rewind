package com.won983212.rewind.mixin;

import com.won983212.rewind.client.ClientDist;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class MixinClientLevel {
    @Inject(method = "playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", at = @At("HEAD"))
    private void onPlaySound(Player player, double x, double y, double z, SoundEvent event, SoundSource source, float volume, float pitch, CallbackInfo ci) {
        if (Minecraft.getInstance().player == player) {
            ClientDist.RECORDER.writePacket(new ClientboundSoundPacket(event, source, x, y, z, volume, pitch));
        }
    }
}
