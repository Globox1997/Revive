package net.revive.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "isAffectedBySplashPotions", at = @At("RETURN"), cancellable = true)
    private void isAffectedBySplashPotionsMixin(CallbackInfoReturnable<Boolean> info) {
        if (!info.getReturnValue() && (Object) this instanceof PlayerEntity playerEntity && playerEntity.isDead()) {
            info.setReturnValue(true);
        }
    }
}
