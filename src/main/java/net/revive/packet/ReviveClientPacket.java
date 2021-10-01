package net.revive.packet;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.revive.accessor.PlayerEntityAccessor;

public class ReviveClientPacket {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ReviveServerPacket.REVIVE_SYNC_PACKET, (client, handler, buf, sender) -> {
            int entityId = buf.readInt();
            int healthPoints = buf.readInt();
            client.execute(() -> {
                if (client.player.getId() == entityId) {
                    ((PlayerEntityAccessor) client.player).setCanRevive(false);
                    client.player.setHealth(healthPoints);
                    client.currentScreen.onClose();
                    client.player.deathTime = 0;
                    client.player.hurtTime = 0;
                } else {
                    PlayerEntity playerEntity = (PlayerEntity) client.world.getEntityById(entityId);
                    playerEntity.setHealth(healthPoints);
                    playerEntity.deathTime = 0;
                    playerEntity.hurtTime = 0;
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(ReviveServerPacket.DEATH_REASON_PACKET, (client, handler, buf, sender) -> {
            boolean isOutOfWorld = buf.readBoolean();
            client.execute(() -> {
                ((PlayerEntityAccessor) client.player).setDeathReason(isOutOfWorld);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(ReviveServerPacket.REVIVABLE_PACKET, (client, handler, buf, sender) -> {
            boolean canRevive = buf.readBoolean();
            client.execute(() -> {
                ((PlayerEntityAccessor) client.player).setCanRevive(canRevive);
            });
        });
    }

    public static void writeC2SRevivePacket() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(ReviveServerPacket.REVIVE_PACKET, buf);
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);
    }

}
