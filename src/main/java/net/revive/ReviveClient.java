package net.revive;

import net.fabricmc.api.ClientModInitializer;
import net.revive.packet.ReviveClientPacket;

public class ReviveClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ReviveClientPacket.init();
    }

}
