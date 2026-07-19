package net.revive.mixin.client;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.revive.ReviveMain;
import net.revive.accessor.PlayerEntityAccessor;
import net.revive.network.packet.RevivePacket;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen {

    @Shadow
    @Final
    @Mutable
    private List<ButtonWidget> buttons = Lists.newArrayList();
    @Shadow
    private int ticksSinceDeath;
    @Unique
    private ButtonWidget reviveButton;

    public DeathScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", shift = Shift.AFTER))
    protected void initMixin(CallbackInfo info) {
        if (this.client != null && this.client.player != null && this.client.player instanceof PlayerEntityAccessor playerEntityAccessor && !playerEntityAccessor.isOutOfWorld()) {
            this.reviveButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("text.deathScreen.revive"), (button) -> {
                if (this.client != null && button.active) {
                    if (!playerEntityAccessor.isOutOfWorld() && playerEntityAccessor.canRevive()) {
                        if (ReviveMain.CONFIG.timer != -1) {
                            if (ReviveMain.CONFIG.timer > this.client.player.deathTime) {
                                ClientPlayNetworking.send(new RevivePacket());
                            }
                        } else {
                            ClientPlayNetworking.send(new RevivePacket());
                        }
                        button.active = false;
                    }
                }
            }).dimensions(this.width / 2 - 100, this.height / 4 + 120, 200, 20).build());
            this.buttons.add(this.reviveButton);
        }
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void tickMixin(CallbackInfo info) {
        if (this.ticksSinceDeath > 20 && this.client != null && this.client.player != null && this.client.player instanceof PlayerEntityAccessor playerEntityAccessor) {
            if (playerEntityAccessor.isOutOfWorld()) {
                this.reviveButton.active = false;
            } else if (playerEntityAccessor.canRevive()) {
                if (ReviveMain.CONFIG.timer != -1) {
                    this.reviveButton.active = ReviveMain.CONFIG.timer >= this.client.player.deathTime;
                } else {
                    this.reviveButton.active = true;
                }
            } else {
                this.reviveButton.active = false;
            }
        } else {
            this.reviveButton.active = false;
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawCenteredTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V", ordinal = 2))
    private void renderMixin(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (this.client != null && this.client.player != null && this.client.player instanceof PlayerEntityAccessor playerEntityAccessor) {
            if (ReviveMain.CONFIG.timer != -1 && ReviveMain.CONFIG.timer >= this.client.player.deathTime && !playerEntityAccessor.isOutOfWorld()) {
                context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable("text.deathScreen.timer", (ReviveMain.CONFIG.timer - this.client.player.deathTime) / 20), this.width / 2, 115,
                        16777215);
            }
            // Coordinates
            if (ReviveMain.CONFIG.showDeathCoordinates) {
                context.drawCenteredTextWithShadow(this.textRenderer,
                        Text.translatable("text.deathScreen.coordinates", this.client.player.getBlockX(), this.client.player.getBlockY(), this.client.player.getBlockZ()), this.width / 2,
                        this.height / 4 + 146 + (!((PlayerEntityAccessor) this.client.player).isOutOfWorld() ? 0 : -24), 16777215);
            }
        }

    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.client.options.chatKey.matchesKey(keyCode, scanCode)) {
            ((MinecraftClientAccessor) this.client).callOpenChatScreen("");
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }
}
