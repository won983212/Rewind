package com.won983212.rewind.recorder;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.won983212.rewind.RewindMod;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.List;

public class PlayerRecorder {
    private final Player player;
    private final int updateInterval;
    private final PacketWriter packetSender;
    private long xp;
    private long yp;
    private long zp;
    private int yRotp;
    private int xRotp;
    private int yHeadRotp;
    private Vec3 motionp = Vec3.ZERO;
    private int tickCount;
    private int teleportDelay;
    private List<Entity> lastPassengers = Collections.emptyList();
    private boolean wasRiding;
    private boolean wasOnGround;
    private boolean wasHurtMarkProcessed;


    public PlayerRecorder(Player player, int updateInterval, PacketWriter packetSender) {
        this.packetSender = packetSender;
        this.player = player;
        this.updateInterval = updateInterval;
        this.updatePrevPos();
        this.yRotp = Mth.floor(player.getYRot() * 256.0F / 360.0F);
        this.xRotp = Mth.floor(player.getXRot() * 256.0F / 360.0F);
        this.yHeadRotp = Mth.floor(player.getYHeadRot() * 256.0F / 360.0F);
        this.wasOnGround = player.isOnGround();
    }

    public void tick() {
        if (player.isRemoved()) {
            RewindMod.LOGGER.warn("Fetching packet for removed entity {}", player);
        }

        sendPassengersChanges();

        if (tickCount % updateInterval == 0) {
            int yRot = Mth.floor(player.getYRot() * 256.0F / 360.0F);
            int xRot = Mth.floor(player.getXRot() * 256.0F / 360.0F);
            boolean updateRot = Math.abs(yRot - yRotp) >= 1 || Math.abs(xRot - xRotp) >= 1;

            if (player.isPassenger()) {
                if (updateRot) {
                    packetSender.writePacket(new ClientboundMoveEntityPacket.Rot(player.getId(), (byte) yRot, (byte) xRot, player.isOnGround()));
                    yRotp = yRot;
                    xRotp = xRot;
                }

                updatePrevPos();
                wasRiding = true;
            } else {
                ++teleportDelay;
                Vec3 delta = player.position().subtract(ClientboundMoveEntityPacket.packetToEntity(xp, yp, zp));
                boolean updatePos = delta.lengthSqr() >= (double) 7.6293945E-6F || tickCount % 60 == 0;
                Packet<?> updatePacket = null;

                if (tickCount > 0) {
                    long x = ClientboundMoveEntityPacket.entityToPacket(delta.x);
                    long y = ClientboundMoveEntityPacket.entityToPacket(delta.y);
                    long z = ClientboundMoveEntityPacket.entityToPacket(delta.z);
                    boolean outBounds = x < -32768L || x > 32767L || y < -32768L || y > 32767L || z < -32768L || z > 32767L;
                    if (!outBounds && teleportDelay <= 400 && !wasRiding && wasOnGround == player.isOnGround()) {
                        if (updatePos && updateRot) {
                            updatePacket = new ClientboundMoveEntityPacket.PosRot(player.getId(), (short) x, (short) y, (short) z, (byte) yRot, (byte) xRot, player.isOnGround());
                        } else if (updatePos) {
                            updatePacket = new ClientboundMoveEntityPacket.Pos(player.getId(), (short) x, (short) y, (short) z, player.isOnGround());
                        } else if (updateRot) {
                            updatePacket = new ClientboundMoveEntityPacket.Rot(player.getId(), (byte) yRot, (byte) xRot, player.isOnGround());
                        }
                    } else {
                        wasOnGround = player.isOnGround();
                        teleportDelay = 0;
                        updatePacket = new ClientboundTeleportEntityPacket(player);
                    }
                }

                sendMotionChanges();

                if (updatePacket != null) {
                    packetSender.writePacket(updatePacket);
                }

                sendEquipment();
                sendMobEffect();

                if (updatePos) {
                    updatePrevPos();
                }

                if (updateRot) {
                    yRotp = yRot;
                    xRotp = xRot;
                }
                wasRiding = false;
            }
            sendYHeadChanges();
        }
        sendAnimation();

        ++tickCount;
        if (player.hurtMarked && !wasHurtMarkProcessed) {
            packetSender.writePacket(new ClientboundSetEntityMotionPacket(player));
            wasHurtMarkProcessed = true;
        } else {
            wasHurtMarkProcessed = false;
        }
    }

    private void sendPassengersChanges() {
        List<Entity> list = player.getPassengers();
        if (!list.equals(lastPassengers)) {
            lastPassengers = list;
            packetSender.writePacket(new ClientboundSetPassengersPacket(player));
        }
    }

    private void sendMotionChanges() {
        Vec3 vec = player.getDeltaMovement();
        double d0 = vec.distanceToSqr(motionp);
        if (d0 > 1.0E-7D || d0 > 0.0D && vec.lengthSqr() == 0.0D) {
            motionp = vec;
            packetSender.writePacket(new ClientboundSetEntityMotionPacket(player.getId(), motionp));
        }
    }

    private void sendYHeadChanges() {
        int yHeadRot = Mth.floor(player.getYHeadRot() * 256.0F / 360.0F);
        if (Math.abs(yHeadRot - yHeadRotp) >= 1) {
            packetSender.writePacket(new ClientboundRotateHeadPacket(player, (byte) yHeadRot));
            yHeadRotp = yHeadRot;
        }
    }

    private void sendAnimation() {
        if (player.swinging && player.swingTime == 0) {
            int arm = player.swingingArm == InteractionHand.MAIN_HAND ? 0 : 3;
            packetSender.writePacket(new ClientboundAnimatePacket(player, arm));
        }
    }

    private void sendMobEffect() {
        for (MobEffectInstance mobeffectinstance : player.getActiveEffects()) {
            packetSender.writePacket(new ClientboundUpdateMobEffectPacket(player.getId(), mobeffectinstance));
        }
    }

    private void sendEquipment() {
        List<Pair<EquipmentSlot, ItemStack>> list1 = Lists.newArrayList();

        for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
            ItemStack itemstack = ((LivingEntity) player).getItemBySlot(equipmentslot);
            if (!itemstack.isEmpty()) {
                list1.add(Pair.of(equipmentslot, itemstack.copy()));
            }
        }

        if (!list1.isEmpty()) {
            packetSender.writePacket(new ClientboundSetEquipmentPacket(player.getId(), list1));
        }
    }

    private void updatePrevPos() {
        xp = ClientboundMoveEntityPacket.entityToPacket(player.getX());
        yp = ClientboundMoveEntityPacket.entityToPacket(player.getY());
        zp = ClientboundMoveEntityPacket.entityToPacket(player.getZ());
    }
}