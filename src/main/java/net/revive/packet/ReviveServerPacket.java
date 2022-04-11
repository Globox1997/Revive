package net.revive.packet;

import java.util.List;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
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
    public static final Identifier FIRST_PERSON_PACKET = new Identifier("revive", "first_person");

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(REVIVE_PACKET, (server, player, handler, buffer, sender) -> {
            if (player != null) {
                ((PlayerEntityAccessor) player).setCanRevive(false);
                player.deathTime = 0;
                player.hurtTime = 0;
                player.extinguish();

                boolean isSupportiveRevival = buffer.readBoolean();
                int healthPoints = ReviveMain.CONFIG.reviveHealthPoints;
                if (isSupportiveRevival)
                    healthPoints = ReviveMain.CONFIG.reviveSupportiveHealthPoints;
                player.setHealth(healthPoints);
                player.onSpawn();

                if (ReviveMain.CONFIG.reviveEffects) {
                    if (isSupportiveRevival)
                        player.addStatusEffect(new StatusEffectInstance(ReviveMain.LIVELY_AFTERMATH_EFFECT, ReviveMain.CONFIG.effectLivelyAftermath, 0, false, false, true));
                    else
                        player.addStatusEffect(new StatusEffectInstance(ReviveMain.AFTERMATH_EFFECT, ReviveMain.CONFIG.effectAftermath, 0, false, false, true));
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

    public static void writeS2CRevivablePacket(ServerPlayerEntity serverPlayerEntity, boolean canRevive, boolean isSupportiveRevival) {
        ((PlayerEntityAccessor) serverPlayerEntity).setCanRevive(canRevive);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(canRevive);
        buf.writeBoolean(isSupportiveRevival);
        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(REVIVABLE_PACKET, buf);
        serverPlayerEntity.networkHandler.sendPacket(packet);
    }

    public static void writeS2CFirstPersonPacket(ServerPlayerEntity serverPlayerEntity) {
        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(FIRST_PERSON_PACKET, new PacketByteBuf(Unpooled.buffer()));
        serverPlayerEntity.networkHandler.sendPacket(packet);
    }

}
