package net.revive.mixin.client;

import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.revive.ReviveMain;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "updatePostDeath", at = @At("HEAD"), cancellable = true)
    protected void updatePostDeathMixin(CallbackInfo info) {
        this.deathTime++;
        if (ReviveMain.CONFIG.timer == -1 || (ReviveMain.CONFIG.timer != -1 && ReviveMain.CONFIG.timer > this.deathTime))
            info.cancel();
        else if (this.deathTime >= 20) {
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

}
