package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.util.L1CommonUtils;

public class C_Restart extends ClientBasePacket {
    public C_Restart(byte[] data, L1Client client) {
        super(data);

        L1CommonUtils.returnSelectCharacters(client);
    }
}

