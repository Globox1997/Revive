package net.revive.mixin.client;

import java.util.List;

import com.google.common.collect.Lists;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.revive.ReviveMain;
import net.revive.accessor.PlayerEntityAccessor;
import net.revive.packet.ReviveClientPacket;

@Environment(EnvType.CLIENT)
@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen {

    @Shadow
    @Final
    @Mutable
    private List<ButtonWidget> buttons = Lists.newArrayList();
    @Shadow
    private int ticksSinceDeath;

    public DeathScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", shift = Shift.AFTER))
    protected void initMixin(CallbackInfo info) {
        if (!((PlayerEntityAccessor) this.client.player).getDeathReason())
            this.buttons.add((ButtonWidget) this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120, 200, 20, new TranslatableText("text.deathScreen.revive"), (button) -> {
                if (((PlayerEntityAccessor) this.client.player).canRevive() && (ReviveMain.CONFIG.timer == -1 || (ReviveMain.CONFIG.timer != -1 && ReviveMain.CONFIG.timer > this.ticksSinceDeath)))
                    ReviveClientPacket.writeC2SRevivePacket();
            })));
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void tickMixin(CallbackInfo info) {
        if (this.buttons.get(this.buttons.size() - 1).active && (!((PlayerEntityAccessor) this.client.player).canRevive()
                || (ReviveMain.CONFIG.timer != -1 && ReviveMain.CONFIG.timer < this.ticksSinceDeath && !((PlayerEntityAccessor) this.client.player).getDeathReason())))
            this.buttons.get(this.buttons.size() - 1).active = false;
        else if (((PlayerEntityAccessor) this.client.player).canRevive() && !this.buttons.get(this.buttons.size() - 1).active)
            this.buttons.get(this.buttons.size() - 1).active = true;

    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/DeathScreen;drawCenteredText(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V", ordinal = 2))
    private void renderMixin(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (ReviveMain.CONFIG.timer != -1 && ReviveMain.CONFIG.timer >= this.ticksSinceDeath && !((PlayerEntityAccessor) this.client.player).getDeathReason())
            drawCenteredText(matrices, this.textRenderer, new TranslatableText("text.deathScreen.timer", (ReviveMain.CONFIG.timer - this.ticksSinceDeath) / 20), this.width / 2, 115, 16777215);
    }
}
