package com.won983212.rewind.util;

import com.won983212.rewind.RewindMod;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class Debug {
    public static void logPacket(Packet<?> packet, boolean minimize) {
        Minecraft mc = Minecraft.getInstance();
        StringBuilder sb = new StringBuilder();

        Entity ent = null;
        if (mc.level != null) {
            if (packet instanceof ClientboundMoveEntityPacket) {
                ent = ((ClientboundMoveEntityPacket) packet).getEntity(mc.level);
            } else if (packet instanceof ClientboundTeleportEntityPacket) {
                ent = mc.level.getEntity(((ClientboundTeleportEntityPacket) packet).getId());
            } else if (packet instanceof ClientboundRotateHeadPacket) {
                ent = ((ClientboundRotateHeadPacket) packet).getEntity(mc.level);
            } else if (packet instanceof ClientboundSetEntityMotionPacket) {
                ent = mc.level.getEntity(((ClientboundSetEntityMotionPacket) packet).getId());
            } else if (packet instanceof ClientboundEntityEventPacket) {
                ent = ((ClientboundEntityEventPacket) packet).getEntity(mc.level);
            } else if (packet instanceof ClientboundSetEntityDataPacket) {
                ent = mc.level.getEntity(((ClientboundSetEntityDataPacket) packet).getId());
            } else if (packet instanceof ClientboundUpdateAttributesPacket) {
                ent = mc.level.getEntity(((ClientboundUpdateAttributesPacket) packet).getEntityId());
            }
        }

        if (minimize && ent != null && !(ent instanceof Player)) {
            return;
        }

        sb.append(packet.getClass());

        if (ent != null) {
            sb.append('[');
            sb.append(ent.getClass().getName());
            sb.append(']');
        }
        RewindMod.LOGGER.info(sb.toString());
    }
}
