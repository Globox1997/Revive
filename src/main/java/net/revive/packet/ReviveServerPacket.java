package net.revive.packet;

import java.util.List;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.revive.ReviveMain;
import net.revive.accessor.PlayerEntityAccessor;

public class ReviveServerPacket {

    public static final Identifier REVIVE_PACKET = new Identifier("revive", "revive_player");
    public static final Identifier REVIVE_SYNC_PACKET = new Identifier("revive", "revive_health");
    public static final Identifier DEATH_REASON_PACKET = new Identifier("revive", "death_reason");
    public static final Identifier REVIVABLE_PACKET = new Identifier("revive", "revivable");

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(REVIVE_PACKET, (server, player, handler, buffer, sender) -> {
            if (player != null) {
                ((PlayerEntityAccessor) player).setCanRevive(false);
                player.deathTime = 0;
                player.hurtTime = 0;
                player.extinguish();
                int healthPoints = ReviveMain.CONFIG.reviveHealthPoints;
                player.setHealth(healthPoints);
                player.onSpawn();

                if (ReviveMain.CONFIG.reviveEffects) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, ReviveMain.CONFIG.effectSlowness, 1, false, false, true));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, ReviveMain.CONFIG.effectHunger, 1, false, false, true));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, ReviveMain.CONFIG.effectWeakness, 1, false, false, true));
                }

                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeInt(player.getId());
                buf.writeInt(healthPoints);
                CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(REVIVE_SYNC_PACKET, buf);
                List<? extends PlayerEntity> list = player.world.getPlayers();
                for (int i = 0; i < list.size(); i++)
                    ((ServerPlayerEntity) list.get(i)).networkHandler.sendPacket(packet);
            }
        });
    }

    public static void writeS2CDeathReasonPacket(ServerPlayerEntity serverPlayerEntity, boolean outOFWorld) {
        ((PlayerEntityAccessor) serverPlayerEntity).setDeathReason(outOFWorld);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(outOFWorld);
        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(DEATH_REASON_PACKET, buf);
        serverPlayerEntity.networkHandler.sendPacket(packet);
    }

    public static void writeS2CRevivablePacket(ServerPlayerEntity serverPlayerEntity, boolean canRevive) {
        ((PlayerEntityAccessor) serverPlayerEntity).setCanRevive(canRevive);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(canRevive);
        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(REVIVABLE_PACKET, buf);
        serverPlayerEntity.networkHandler.sendPacket(packet);
    }

}
