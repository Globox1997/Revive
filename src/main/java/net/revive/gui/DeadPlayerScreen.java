package net.revive.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.revive.ReviveMain;

@Environment(EnvType.CLIENT)
public class DeadPlayerScreen extends HandledScreen<DeadPlayerScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("revivetextures/gui/dead_player_inventory.png");

    public DeadPlayerScreen(DeadPlayerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        // this.passEvents = false;
        this.backgroundWidth = 176;
        this.backgroundHeight = 204;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        if (ReviveMain.isBackSlotLoaded) {
            context.drawTexture(TEXTURE, i + 97, j + 89, 0, 176, 18, 18);
            context.drawTexture(TEXTURE, i + 115, j + 89, 0, 176, 18, 18);
        }
    }

}
