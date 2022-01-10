package com.won983212.rewind.mixin;

import com.won983212.rewind.client.ClientDist;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.stats.StatsCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(MultiPlayerGameMode.class)
public class MixinMultiPlayerGameMode {
    private static final UUID CAMERA_PLAYER_UUID = UUID.nameUUIDFromBytes("CameraViewEntity".getBytes());

    @Inject(method = "createPlayer(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/stats/StatsCounter;Lnet/minecraft/client/ClientRecipeBook;)Lnet/minecraft/client/player/LocalPlayer;", at = @At("HEAD"), cancellable = true)
    private void onCreatePlayer(ClientLevel level, StatsCounter counter, ClientRecipeBook book, CallbackInfoReturnable<LocalPlayer> ci) {
        if (ClientDist.REPLAYER.isReplaying()) {
            LocalPlayer player = ((MultiPlayerGameMode) (Object) this).createPlayer(level, counter, book, false, false);
            player.setUUID(CAMERA_PLAYER_UUID);
            ci.setReturnValue(player);
        }
    }
}
