package net.revive;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.revive.gui.DeadPlayerScreen;
import net.revive.packet.ReviveClientPacket;

public class ReviveClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ReviveClientPacket.init();
        // HandledScreens.register(ReviveMain.DEAD_PLAYER, DeadPlayerScreen::new);
    }

}
