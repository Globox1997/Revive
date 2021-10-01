package net.revive.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "revive")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class ReviveConfig implements ConfigData {
    @Comment("Time in ticks: -1 disables timer")
    public int timer = -1;
    @Comment("Drops player loot directly")
    public boolean dropLoot = false;
    public int reviveHealthPoints = 2;

    public boolean reviveEffects = true;
    public int effectSlowness = 600;
    public int effectHunger = 300;
    public int effectWeakness = 900;
}