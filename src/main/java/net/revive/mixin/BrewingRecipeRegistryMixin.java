package net.revive.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.revive.ReviveMain;

@Mixin(BrewingRecipeRegistry.class)
public class BrewingRecipeRegistryMixin {

    @Inject(method = "registerDefaults", at = @At(value = "TAIL"))
    private static void registerDefaultsMixin(CallbackInfo info) {
        registerPotionRecipe(Potions.MUNDANE, Items.GOLDEN_APPLE, ReviveMain.REVIVIFY_POTION);
        registerPotionRecipe(Potions.STRONG_REGENERATION, ReviveMain.REVIVE_ITEM, ReviveMain.SUPPORTIVE_REVIVIFY_POTION);
    }

    @Shadow
    private static void registerPotionRecipe(Potion input, Item item, Potion output) {
    }

}
