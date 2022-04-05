package net.revive.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.BlockView;
import net.revive.ReviveMain;

@Environment(EnvType.CLIENT)
@Mixin(Camera.class)
public class CameraMixin {
    @Shadow
    private float pitch;
    @Shadow
    private float yaw;

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;moveBy(DDD)V", ordinal = 0))
    private void updateMixin(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info) {
        if (((LivingEntity) focusedEntity).deathTime > 20 && ReviveMain.CONFIG.thirdPersonOnDeath)
            this.setRotation(((LivingEntity) focusedEntity).deathTime * ReviveMain.CONFIG.rotationSpeed, 0.0F);
    }

    @Shadow
    protected void setRotation(float yaw, float pitch) {
    }
}
