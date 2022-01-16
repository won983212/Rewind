package com.won983212.rewind.mixin;

import com.won983212.rewind.RewindMod;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.stats.StatsCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(MultiPlayerGameMode.class)
public class MixinMultiPlayerGameMode {

    private static final UUID CAMERA_PLAYER_UUID = UUID.nameUUIDFromBytes("CameraViewEntity".getBytes());

    @Shadow
    @Final
    private ClientPacketListener connection;


    @Inject(method = "createPlayer(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/stats/StatsCounter;Lnet/minecraft/client/ClientRecipeBook;ZZ)Lnet/minecraft/client/player/LocalPlayer;", at = @At("HEAD"), cancellable = true)
    private void onCreatePlayer(ClientLevel level, StatsCounter counter, ClientRecipeBook book, boolean shiftDown, boolean sprinting, CallbackInfoReturnable<LocalPlayer> ci) {
        if (RewindMod.REPLAYER.isReplaying()) {
            LocalPlayer player = new LocalPlayer(Minecraft.getInstance(), level, connection, counter, book, shiftDown, sprinting){
                @Override
                public boolean canBeCollidedWith() {
                    return false;
                }

                @Override
                public boolean isSpectator() {
                    return true;
                }

                @Override
                public boolean isPushable() {
                    return false;
                }
            };
            player.setUUID(CAMERA_PLAYER_UUID);
            RewindMod.LOGGER.info("Camera player setup.");
            ci.setReturnValue(player);
        }
    }
}
