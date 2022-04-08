package net.revive;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;
import net.revive.config.ReviveConfig;
import net.revive.item.RevivalStarItem;
import net.revive.packet.ReviveServerPacket;

public class ReviveMain implements ModInitializer {

    public static ReviveConfig CONFIG = new ReviveConfig();
    public static final RevivalStarItem REVIVE_ITEM = new RevivalStarItem(new Item.Settings().group(ItemGroup.MISC));

    public static final Identifier REVIVE_SOUND = new Identifier("revive:revive");
    public static SoundEvent REVIVE_SOUND_EVENT = new SoundEvent(REVIVE_SOUND);

    @Override
    public void onInitialize() {
        AutoConfig.register(ReviveConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ReviveConfig.class).getConfig();
        ReviveServerPacket.init();
        Registry.register(Registry.ITEM, new Identifier("revive", "revival_star"), REVIVE_ITEM);
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.CLERIC, 1, (factories -> {
            factories.add((entity, random) -> new TradeOffer(new ItemStack(Items.EMERALD, 28), new ItemStack(REVIVE_ITEM), 4, 1, 0.4F));
        }));
        Registry.register(Registry.SOUND_EVENT, REVIVE_SOUND, REVIVE_SOUND_EVENT);
    }

}
