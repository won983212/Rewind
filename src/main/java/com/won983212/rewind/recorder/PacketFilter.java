package com.won983212.rewind.recorder;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;

import java.util.HashSet;
import java.util.Set;

public class PacketFilter {
    private static final Set<Class<? extends Packet<?>>> IGNORE_PACKETS = new HashSet<>();

    static {
        // minecraft player update
        IGNORE_PACKETS.add(ClientboundSetHealthPacket.class);
        IGNORE_PACKETS.add(ClientboundSetExperiencePacket.class);

        // recipe
        IGNORE_PACKETS.add(ClientboundRecipePacket.class);
        IGNORE_PACKETS.add(ClientboundUpdateRecipesPacket.class);

        // title
        IGNORE_PACKETS.add(ClientboundSetTitleTextPacket.class);
        IGNORE_PACKETS.add(ClientboundSetTitlesAnimationPacket.class);

        // screen
        IGNORE_PACKETS.add(ClientboundOpenScreenPacket.class);
        IGNORE_PACKETS.add(ClientboundOpenBookPacket.class);
        IGNORE_PACKETS.add(ClientboundOpenSignEditorPacket.class);
        IGNORE_PACKETS.add(ClientboundHorseScreenOpenPacket.class);
        IGNORE_PACKETS.add(ClientboundContainerClosePacket.class);

        // advancement
        IGNORE_PACKETS.add(ClientboundSelectAdvancementsTabPacket.class);
        IGNORE_PACKETS.add(ClientboundUpdateAdvancementsPacket.class);

        // statistics
        IGNORE_PACKETS.add(ClientboundAwardStatsPacket.class);
    }

    public static boolean canHandle(Packet<?> packet) {
        return !IGNORE_PACKETS.contains(packet.getClass());
    }
}
