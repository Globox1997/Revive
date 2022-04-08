package net.revive.item;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.revive.ReviveMain;
import net.revive.accessor.PlayerEntityAccessor;
import net.revive.packet.ReviveServerPacket;

public class RevivalStarItem extends Item {

    public RevivalStarItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        PlayerEntity playerEntity = null;
        List<? extends PlayerEntity> list = world.getPlayers();
        System.out.println(user.raycast(2.5D, 1.0F, false));
        if (user.raycast(2.5D, 1.0F, false) != null && user.raycast(2.5D, 1.0F, false).getType().equals(HitResult.Type.ENTITY))
            System.out.println();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() != user.getId() && list.get(i).isDead() && list.get(i).getBlockPos().isWithinDistance(user.getBlockPos(), 2.5D)) {
                playerEntity = list.get(i);
                break;
            }
        }
        if (playerEntity != null && !((PlayerEntityAccessor) playerEntity).canRevive()) {
            if (!world.isClient) {
                ReviveServerPacket.writeS2CRevivablePacket((ServerPlayerEntity) playerEntity, true);
                world.playSound(null, user.getBlockPos(), ReviveMain.REVIVE_SOUND_EVENT, SoundCategory.PLAYERS, 1.0F, 0.9F + world.random.nextFloat() * 0.2F);
                itemStack.decrement(1);
                if (world instanceof ServerWorld) {
                    for (int u = 0; u < 20; u++)
                        // world.addParticle(ParticleTypes.END_ROD, (double) playerEntity.getX() - 0.5D + world.random.nextFloat(), playerEntity.getRandomBodyY(),
                        // (double) playerEntity.getZ() - 0.5D + world.random.nextFloat(), 0.1D * world.random.nextFloat(), 0.3D * world.random.nextFloat(), 0.1D * world.random.nextFloat());

                        ((ServerWorld) world).spawnParticles(ParticleTypes.END_ROD, (double) playerEntity.getX() - 0.5D + world.random.nextFloat(), playerEntity.getRandomBodyY(),
                                (double) playerEntity.getZ() - 0.5D + world.random.nextFloat(), 0, 0.1D * world.random.nextFloat(), 0.3D * world.random.nextFloat(), 0.1D * world.random.nextFloat(),
                                0.1D);
                }
            }
            // else {
            // for (int u = 0; u < 20; u++)
            // world.addParticle(ParticleTypes.END_ROD, (double) playerEntity.getX() - 0.5D + world.random.nextFloat(), playerEntity.getRandomBodyY(),
            // (double) playerEntity.getZ() - 0.5D + world.random.nextFloat(), 0.1D * world.random.nextFloat(), 0.3D * world.random.nextFloat(), 0.1D * world.random.nextFloat());
            // }
            return TypedActionResult.consume(itemStack);
        } else
            return TypedActionResult.fail(itemStack);

    }

}
