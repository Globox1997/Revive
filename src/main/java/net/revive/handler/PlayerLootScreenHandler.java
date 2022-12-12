package net.revive.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class PlayerLootScreenHandler extends ScreenHandler {

    private final PlayerInventory lootablePlayerInventory;

    public PlayerLootScreenHandler(int syncId, PlayerInventory playerInventory, PlayerInventory otherPlayerInventory) {
        super(ScreenHandlerType.GENERIC_9X4, syncId);
        this.lootablePlayerInventory = otherPlayerInventory;
        int m;
        for (m = 0; m < 3; ++m) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(this.lootablePlayerInventory, l + m * 9 + 9, 8 + l * 18, m * 18));
            }
        }
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(this.lootablePlayerInventory, m, 8 + m * 18, 54));
        }
        // Armor slots + offhand slot + other mods slot could get added with GENERIC_9x5
        // but if more than 4 items would be available 9X5 wouldn't be enough
        // and mod compatibilities have to get done by hand here at canInsert

        // for (m = 0; m < 9; ++m) {
        // this.addSlot(new Slot(this.lootablePlayerInventory, m + 36, 8 + m * 18, 72) {
        // @Override
        // public boolean canInsert(ItemStack stack) {
        // System.out.println(this.getIndex());
        // if (lootablePlayerInventory.size() <= this.getIndex())
        // return false;
        // else if (this.getIndex() >= 36 && this.getIndex() < 40 && !(stack.getItem() instanceof ArmorItem)
        // || (stack.getItem() instanceof ArmorItem && !canInsertArmorStack(stack, this.getIndex())))
        // return false;
        // else if ((this.getIndex() == 41 || this.getIndex() == 42) && !(stack.getItem() instanceof SwordItem))
        // return false;
        // else
        // return true;
        // }

        // @Override
        // public boolean isEnabled() {
        // if (lootablePlayerInventory.size() <= this.getIndex()) {
        // System.out.println("Not enabled");
        // return false;
        // } else
        // return true;
        // }

        // @Override
        // public boolean canTakeItems(PlayerEntity playerEntity) {
        // ItemStack itemStack = this.getStack();
        // return !itemStack.isEmpty() && EnchantmentHelper.hasBindingCurse(itemStack) ? false : super.canTakeItems(playerEntity);
        // }
        // });
        // }

        for (m = 0; m < 3; ++m) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, m * 18 + 90));
            }
        }
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 148));
        }

    }

    // private boolean canInsertArmorStack(ItemStack stack, int slot) {
    // switch (slot) {
    // case 36:
    // return MobEntity.getPreferredEquipmentSlot(stack) == EquipmentSlot.FEET;
    // case 37:
    // return MobEntity.getPreferredEquipmentSlot(stack) == EquipmentSlot.LEGS;
    // case 38:
    // return MobEntity.getPreferredEquipmentSlot(stack) == EquipmentSlot.CHEST;
    // case 39:
    // return MobEntity.getPreferredEquipmentSlot(stack) == EquipmentSlot.HEAD;
    // default:
    // return false;
    // }
    // }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.lootablePlayerInventory.player.isDead() && this.lootablePlayerInventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();

            if (index < this.lootablePlayerInventory.size()) {
                if (!this.insertItem(itemStack2, this.lootablePlayerInventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, this.lootablePlayerInventory.size(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return itemStack;
    }

}
