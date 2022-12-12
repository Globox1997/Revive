package net.revive.mixin.client;

import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.revive.ReviveMain;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    @Shadow
    @Mutable
    @Final
    protected MinecraftClient client;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "updatePostDeath", at = @At("HEAD"), cancellable = true)
    protected void updatePostDeathMixin(CallbackInfo info) {
        if (this.deathTime == 20 && ReviveMain.CONFIG.thirdPersonOnDeath && this.client.options.getPerspective().isFirstPerson())
            this.client.options.setPerspective(Perspective.THIRD_PERSON_FRONT);
        this.deathTime++;
        if (ReviveMain.CONFIG.timer == -1 || (ReviveMain.CONFIG.timer != -1 && ReviveMain.CONFIG.timer > this.deathTime))
            info.cancel();
        else if (this.deathTime >= 20)
            this.remove(Entity.RemovalReason.KILLED);

    }

}
