package ks.packets.clientpackets;

import ks.core.network.L1Client;

public class C_QuitGame extends ClientBasePacket {
    public C_QuitGame(byte[] data, L1Client client) {
        super(data);
        client.disconnect(0);
    }
}
