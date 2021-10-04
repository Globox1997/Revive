package net.revive.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.revive.mixin.PlayerInventoryAccessor;

public class PlayerLootScreenHandler extends ScreenHandler {
    private final PlayerInventory lootablePlayerInventory;

    public PlayerLootScreenHandler(int syncId, PlayerInventory playerInventory, PlayerInventory otherPlayerInventory) {
        super(ScreenHandlerType.GENERIC_9X5, syncId);
        this.lootablePlayerInventory = otherPlayerInventory;

        int m;
        for (m = 0; m < 3; ++m) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(lootablePlayerInventory, l + m * 9 + 9, 8 + l * 18, m * 18));
            }
        }
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(lootablePlayerInventory, m, 8 + m * 18, 54));
        }
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(lootablePlayerInventory, m + 36, 8 + m * 18, 72) {
                @Override
                public boolean canInsert(ItemStack stack) {
                    int inventorySize = 0;
                    for (int i = 0; i < ((PlayerInventoryAccessor) lootablePlayerInventory).getCombinedInventory().size(); i++) {
                        for (int u = 0; u < ((PlayerInventoryAccessor) lootablePlayerInventory).getCombinedInventory().get(i).size(); u++)
                            inventorySize++;
                    }
                    if (inventorySize <= this.getIndex())
                        return false;
                    else
                        return true;
                }

                // @Override
                // public boolean isEnabled() {
                //     int inventorySize = 0;
                //     for (int i = 0; i < ((PlayerInventoryAccessor) lootablePlayerInventory).getCombinedInventory().size(); i++) {
                //         for (int u = 0; u < ((PlayerInventoryAccessor) lootablePlayerInventory).getCombinedInventory().get(i).size(); u++)
                //             inventorySize++;
                //     }
                //     if (inventorySize <= this.getIndex())
                //         return false;
                //     else
                //         return true;
                // }
            });
        }

        for (m = 0; m < 3; ++m) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, m * 18 + 90));
            }
        }
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 148));
        }

    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

}
