package com.won983212.rewind.recorder;

import com.won983212.rewind.RewindMod;
import com.won983212.rewind.client.ClientDist;
import com.won983212.rewind.io.PacketByteBuffer;
import com.won983212.rewind.io.PacketFileOutputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;

import java.io.IOException;
import java.util.List;

public class RecordHeaderWriter {
    private final PacketByteBuffer initialPacketBuffer;
    private ClientboundLoginPacket lastLoginPacket;


    public RecordHeaderWriter() {
        this.initialPacketBuffer = new PacketByteBuffer();
    }

    public boolean handleHeaderPacket(Packet<?> packet) {
        if (RecordPacketFilter.isHeaderPacket(packet)) {
            if (packet instanceof ClientboundLoginPacket loginPacket) {
                initialPacketBuffer.getBuffer().clear();
                lastLoginPacket = loginPacket;
                return false;
            }
            if (packet instanceof ClientboundRespawnPacket respawnPacket) {
                if (lastLoginPacket != null && lastLoginPacket.dimension() != respawnPacket.getDimension()) {
                    lastLoginPacket = new ClientboundLoginPacket(-1, lastLoginPacket.hardcore(),
                            GameType.SPECTATOR, null, lastLoginPacket.levels(), lastLoginPacket.registryHolder(),
                            respawnPacket.getDimensionType(), respawnPacket.getDimension(), respawnPacket.getSeed(),
                            0, lastLoginPacket.chunkRadius(), lastLoginPacket.simulationDistance(),
                            lastLoginPacket.reducedDebugInfo(), lastLoginPacket.showDeathScreen(), lastLoginPacket.isDebug(),
                            lastLoginPacket.isFlat());
                }
                return false;
            }
            writeToBuffer(packet);
            return true;
        }
        return false;
    }

    public void writeHeaderPacket(PacketFileOutputStream packetFileWriter) throws IOException {
        recordLoginPacket(packetFileWriter);
        packetFileWriter.write(initialPacketBuffer);
        recordPlayers(packetFileWriter);
        recordCurrentLoadedChunk(packetFileWriter);
        recordPlayerPosition(packetFileWriter);
    }

    private void recordLoginPacket(PacketFileOutputStream packetFileWriter) throws IOException {
        if (lastLoginPacket == null) {
            throw new IOException("Not found login packet.");
        }

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            throw new IOException("Level is null.");
        }

        ClientLevel.ClientLevelData data = level.getLevelData();
        packetFileWriter.write(lastLoginPacket, 0);
        packetFileWriter.write(new ClientboundChangeDifficultyPacket(data.getDifficulty(), data.isDifficultyLocked()), 0);
    }

    private void recordCurrentLoadedChunk(PacketFileOutputStream packetFileWriter) throws IOException {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            throw new IOException("Can't record world chunks.");
        }
        int viewDist = mc.options.renderDistance;
        ChunkPos chunkPos = mc.player.chunkPosition();
        LevelLightEngine lightEngine = mc.level.getLightEngine();
        for (int chunkX = chunkPos.x - viewDist - 1; chunkX <= chunkPos.x + viewDist + 1; ++chunkX) {
            for (int chunkZ = chunkPos.z - viewDist - 1; chunkZ <= chunkPos.z + viewDist + 1; ++chunkZ) {
                if (!ChunkMap.isChunkInRange(chunkX, chunkZ, chunkPos.x, chunkPos.z, viewDist)) {
                    continue;
                }
                LevelChunk chunk = mc.level.getChunkSource().getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
                if (chunk == null) {
                    continue;
                }
                ClientboundLevelChunkWithLightPacket packet =
                        new ClientboundLevelChunkWithLightPacket(chunk, lightEngine, null, null, true);
                packetFileWriter.write(packet, 0);
            }
        }
    }

    private void recordPlayers(PacketFileOutputStream packetFileWriter) throws IOException {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            throw new IOException("Can't record players.");
        }
        ClientPacketListener listener = mc.getConnection();
        if (listener == null || ClientDist.REPLAYER.isReplaying()) {
            throw new IOException("listener is null");
        }
        for (AbstractClientPlayer player : mc.level.players()) {
            PlayerInfo playerInfo = listener.getPlayerInfo(player.getUUID());
            if (playerInfo != null) {
                FriendlyByteBuf data = new FriendlyByteBuf(Unpooled.buffer());
                data.writeEnum(ClientboundPlayerInfoPacket.Action.ADD_PLAYER);
                data.writeCollection(List.of(new ClientboundPlayerInfoPacket.PlayerUpdate(playerInfo.getProfile(), 0,
                        playerInfo.getGameMode(), playerInfo.getTabListDisplayName())), this::recordPlayerInfo);

                packetFileWriter.write(new ClientboundPlayerInfoPacket(data), 0);
                packetFileWriter.write(new ClientboundAddPlayerPacket(player), 0);
                packetFileWriter.write(new ClientboundSetEntityDataPacket(player.getId(), player.getEntityData(), true), 0);
            } else {
                RewindMod.LOGGER.warn("Can't find player info: " + player.getDisplayName().getContents());
            }
        }
    }

    private void recordPlayerInfo(FriendlyByteBuf data, ClientboundPlayerInfoPacket.PlayerUpdate player) {
        data.writeUUID(player.getProfile().getId());
        data.writeUtf(player.getProfile().getName());
        data.writeCollection(player.getProfile().getProperties().values(), (buffer, property) -> {
            buffer.writeUtf(property.getName());
            buffer.writeUtf(property.getValue());
            if (property.hasSignature()) {
                buffer.writeBoolean(true);
                buffer.writeUtf(property.getSignature());
            } else {
                buffer.writeBoolean(false);
            }

        });

        data.writeVarInt(player.getGameMode().getId());
        data.writeVarInt(0);

        if (player.getDisplayName() == null) {
            data.writeBoolean(false);
        } else {
            data.writeBoolean(true);
            data.writeComponent(player.getDisplayName());
        }
    }

    private void recordPlayerPosition(PacketFileOutputStream data) throws IOException {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            throw new IOException("Can't record player position.");
        }
        data.write(Recorder.makePlayerPositionPacket(mc.player), 0);
    }

    private void writeToBuffer(Packet<?> packet) {
        try {
            initialPacketBuffer.write(packet, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
