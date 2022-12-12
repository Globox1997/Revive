package net.revive.compat;

import java.util.Iterator;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.plugin.common.displays.brewing.BrewingRecipe;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SmithingRecipe;
import net.revive.ReviveMain;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class ReviveReiPlugin implements REIClientPlugin {

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        // registry.add(new BrewingRecipe(Ingredient.ofItems(PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.MUNDANE).getItem()), Ingredient.ofItems(Items.GOLDEN_APPLE),
        // PotionUtil.setPotion(new ItemStack(Items.POTION), ReviveMain.REVIVIFY_POTION)));
    }

}
