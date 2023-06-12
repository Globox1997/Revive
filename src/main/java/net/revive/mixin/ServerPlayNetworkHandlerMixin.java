package net.revive.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.revive.ReviveMain;
import net.revive.mixin.accessor.LivingEntityAccessor;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onClientStatus", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/PlayerManager;respawnPlayer(Lnet/minecraft/server/network/ServerPlayerEntity;Z)Lnet/minecraft/server/network/ServerPlayerEntity;", ordinal = 1, shift = Shift.BEFORE))
    private void onClientStatusMixin(ClientStatusC2SPacket packet, CallbackInfo info) {
        if (!ReviveMain.CONFIG.dropLoot) {
            ((LivingEntityAccessor) player).callDrop(player.getDamageSources().generic());
        }
    }
}
