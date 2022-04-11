package net.revive.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.revive.ReviveMain;
import net.revive.accessor.PlayerEntityAccessor;
import net.revive.packet.ReviveServerPacket;

public class LivelyAftermathEffect extends StatusEffect {

    public LivelyAftermathEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        super.applyUpdateEffect(entity, amplifier);
        if (!entity.world.isClient && entity instanceof PlayerEntity)
            ((PlayerEntity) entity).getHungerManager().add(amplifier + 1, 1.0f);
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);
        if (!entity.world.isClient && entity.isDead() && entity instanceof PlayerEntity && !((PlayerEntityAccessor) entity).canRevive()) {
            ReviveServerPacket.writeS2CRevivablePacket((ServerPlayerEntity) entity, true, true);
            entity.world.playSound(null, entity.getBlockPos(), ReviveMain.REVIVE_SOUND_EVENT, SoundCategory.PLAYERS, 1.0F, 0.9F + entity.world.random.nextFloat() * 0.2F);
        }
    }

}
