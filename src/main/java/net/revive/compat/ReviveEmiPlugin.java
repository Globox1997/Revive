package net.revive.compat;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

public class ReviveEmiPlugin implements EmiPlugin {

    // Auto compatible
    @Override
    public void register(EmiRegistry registry) {
        // registry.addRecipe(new EmiBrewingRecipe(EmiStack.of(PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.MUNDANE)), EmiIngredient.of(Ingredient.ofItems(Items.GOLDEN_APPLE)),
        // EmiStack.of(PotionUtil.setPotion(new ItemStack(Items.POTION), ReviveMain.REVIVIFY_POTION)), new Identifier("revive", "revivify_potion")));
    }

}
