package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.packets.serverpackets.S_ServerVersion;

public class C_ServerVersion extends ClientBasePacket {
    public C_ServerVersion(byte[] decrypt, L1Client client) {
        super(decrypt);

        client.setClientVersionCheck(true);
        client.sendPacket(new S_ServerVersion());
    }
}