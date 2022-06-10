package net.revive.mixin;

import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.revive.ReviveMain;
import net.revive.packet.ReviveServerPacket;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, PlayerPublicKey publicKey) {
        super(world, pos, yaw, gameProfile, publicKey);
    }

    @Inject(method = "onDeath", at = @At(value = "HEAD"))
    private void onDeathMixin(DamageSource source, CallbackInfo info) {
        ReviveServerPacket.writeS2CDeathReasonPacket((ServerPlayerEntity) (Object) this, source.equals(DamageSource.OUT_OF_WORLD));
    }

    @Redirect(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;drop(Lnet/minecraft/entity/damage/DamageSource;)V"))
    private void onDeathRedirectMixin(ServerPlayerEntity serverPlayerEntity, DamageSource damageSource) {
        if (ReviveMain.CONFIG.dropLoot)
            this.drop(damageSource);
        else if ((ReviveMain.CONFIG.dropRandomOnExplosion && damageSource.isExplosive()) || ReviveMain.CONFIG.dropRandom) {
            for (int i = 0; i < this.getInventory().size(); ++i) {
                ItemStack itemStack = this.getInventory().getStack(i);
                if (!itemStack.isEmpty() && this.random.nextFloat() < ReviveMain.CONFIG.dropChance) {
                    if (!EnchantmentHelper.hasVanishingCurse(itemStack))
                        this.dropStack(itemStack);
                    this.getInventory().removeStack(i);
                }
            }
        }
    }
}
