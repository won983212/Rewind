package com.won983212.rewind.recorder;

import net.minecraft.network.protocol.Packet;

import java.util.HashMap;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class PacketHandler {
    private final HashMap<Class<? extends Packet<?>>, Consumer<? extends Packet<?>>> handlers = new HashMap<>();

    public <T extends Packet<?>> void addHandler(Class<T> packetClass, Consumer<T> handler) {
        handlers.put(packetClass, handler);
    }

    public <T extends Packet<?>> boolean handle(T packet) {
        Consumer<T> handler = (Consumer<T>) handlers.get(packet.getClass());
        if (handler != null) {
            handler.accept(packet);
            return true;
        }
        return false;
    }

    public boolean has(Packet<?> packet) {
        return handlers.containsKey(packet.getClass());
    }
}