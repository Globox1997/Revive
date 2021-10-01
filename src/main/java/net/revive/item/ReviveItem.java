package net.revive.item;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.revive.ReviveMain;
import net.revive.accessor.PlayerEntityAccessor;
import net.revive.packet.ReviveServerPacket;

public class ReviveItem extends Item {

    public ReviveItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        PlayerEntity playerEntity = null;
        List<? extends PlayerEntity> list = world.getPlayers();
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
            }
            return TypedActionResult.consume(itemStack);
        } else
            return TypedActionResult.fail(itemStack);

    }

}
