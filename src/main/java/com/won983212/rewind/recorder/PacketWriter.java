package com.won983212.rewind.recorder;

import net.minecraft.network.protocol.Packet;

public interface PacketWriter {
    void writePacket(Packet<?> packet);
}
