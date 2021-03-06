package com.won983212.rewind.mixin;

import com.won983212.rewind.RewindMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {
    @Inject(method = "destroyBlockProgress", at = @At("HEAD"))
    private void onDestroyBlockProgress(int id, BlockPos pos, int progress, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && id == player.getId()) {
            RewindMod.RECORDER.handlePacket(new ClientboundBlockDestructionPacket(id, pos, progress));
        }
    }
}
