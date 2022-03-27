package ks.packets.clientpackets;

import ks.core.network.L1Client;

public class C_KeepAlive extends ClientBasePacket {
    public C_KeepAlive(byte[] decrypt, L1Client client) {
        super(decrypt);
    }
}