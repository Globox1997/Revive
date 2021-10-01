package net.revive.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.revive.ReviveMain;
import net.revive.accessor.PlayerEntityAccessor;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityAccessor {

    private boolean isOutOfWorld = false;
    private boolean canRevive = false;

    public PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
    public void readCustomDataFromNbtMixin(NbtCompound nbt, CallbackInfo info) {
        this.isOutOfWorld = nbt.getBoolean("IsOutOfWorld");
        this.canRevive = nbt.getBoolean("CanRevive");
    }

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    public void writeCustomDataToNbtMixin(NbtCompound nbt, CallbackInfo info) {
        nbt.putBoolean("IsOutOfWorld", this.isOutOfWorld);
        nbt.putBoolean("CanRevive", this.canRevive);
    }

    @Override
    protected void updatePostDeath() {
        ++this.deathTime;
        if (!this.world.isClient) {
            if (ReviveMain.CONFIG.timer != -1 && ReviveMain.CONFIG.timer < this.deathTime) {
                if (!ReviveMain.CONFIG.dropLoot)
                    this.drop(DamageSource.GENERIC);
                this.world.sendEntityStatus(this, (byte) 60);
                this.remove(Entity.RemovalReason.KILLED);
            }
        }
    }

    @Override
    public void setDeathReason(boolean outOfWorld) {
        this.isOutOfWorld = outOfWorld;
    }

    @Override
    public boolean getDeathReason() {
        return isOutOfWorld;
    }

    @Override
    public void setCanRevive(boolean canRevive) {
        this.canRevive = canRevive;
    }

    @Override
    public boolean canRevive() {
        return this.canRevive;
    }

}
