package net.revive.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.revive.ReviveMain;
import net.revive.accessor.PlayerEntityAccessor;
import net.revive.network.packet.RevivablePacket;
import net.revive.network.packet.RevivableSyncPacket;
import net.revive.network.packet.ReviveSyncPacket;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReviveHelper {

    public static void revivePlayer(ServerPlayerEntity serverPlayerEntity) {
        if (serverPlayerEntity instanceof PlayerEntityAccessor playerEntityAccessor && playerEntityAccessor.canRevive() && !playerEntityAccessor.isOutOfWorld()) {

            serverPlayerEntity.deathTime = 0;
            serverPlayerEntity.hurtTime = 0;
            serverPlayerEntity.extinguish();

            int healthPoints = ReviveMain.CONFIG.reviveHealthPoints;
            if (playerEntityAccessor.isSupportiveRevival()) {
                healthPoints = ReviveMain.CONFIG.reviveSupportiveHealthPoints;
            }

            serverPlayerEntity.setHealth(healthPoints);
            serverPlayerEntity.onSpawn();

            if (ReviveMain.CONFIG.reviveEffects) {
                if (playerEntityAccessor.isSupportiveRevival()) {
                    serverPlayerEntity.addStatusEffect(new StatusEffectInstance(ReviveMain.LIVELY_AFTERMATH_EFFECT, ReviveMain.CONFIG.effectLivelyAftermath, 0, false, false, true));
                } else {
                    serverPlayerEntity.addStatusEffect(new StatusEffectInstance(ReviveMain.AFTERMATH_EFFECT, ReviveMain.CONFIG.effectAftermath, 0, false, false, true));
                }
            }
            List<? extends PlayerEntity> list = serverPlayerEntity.getWorld().getPlayers();
            for (PlayerEntity playerEntity : list) {
                ServerPlayNetworking.send((ServerPlayerEntity) playerEntity, new ReviveSyncPacket(serverPlayerEntity.getId(), healthPoints));
            }
            ((PlayerEntityAccessor) serverPlayerEntity).setCanRevive(false, false, false);
        }
    }

    public static void setPlayerRevivable(ServerPlayerEntity serverPlayerEntity, boolean canRevive, boolean outOfWorld, boolean supportiveRevival) {
        ((PlayerEntityAccessor) serverPlayerEntity).setCanRevive(canRevive, outOfWorld, supportiveRevival);
        ServerPlayNetworking.send(serverPlayerEntity, new RevivablePacket(canRevive, outOfWorld, supportiveRevival));

        List<? extends PlayerEntity> list = serverPlayerEntity.getWorld().getPlayers();
        System.out.println("SSS "+list);

        for (PlayerEntity playerEntity : list) {
            if (playerEntity.isPlayer() && !playerEntity.getUuid().equals(serverPlayerEntity.getUuid())) {
                System.out.println("SEND "+playerEntity);
                ServerPlayNetworking.send((ServerPlayerEntity) playerEntity, new RevivableSyncPacket(serverPlayerEntity.getId(), canRevive));
            }
        }

        if (canRevive) {
            serverPlayerEntity.getWorld().playSound(null, serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), ReviveMain.REVIVE_SOUND_EVENT, SoundCategory.PLAYERS, 1.0F,
                    0.9F + serverPlayerEntity.getWorld().getRandom().nextFloat() * 0.2F, serverPlayerEntity.getRandom().nextLong());
            if (ReviveMain.CONFIG.automaticRevive) {
                revivePlayer(serverPlayerEntity);
            }
        }
    }

    @Nullable
    public static PlayerEntity getClosestPlayer(PlayerEntity player, double maxDistance) {
        double d = -1.0;
        PlayerEntity playerEntity = null;

        for (PlayerEntity playerEntity2 : player.getWorld().getPlayers()) {
            if (EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(playerEntity2) && !playerEntity2.getUuid().equals(player.getUuid())) {
                double e = playerEntity2.squaredDistanceTo(player.getX(), player.getY(), player.getZ());
                if ((maxDistance < 0.0 || e < maxDistance * maxDistance) && (d == -1.0 || e < d)) {
                    d = e;
                    playerEntity = playerEntity2;
                }
            }
        }

        return playerEntity;
    }

}
