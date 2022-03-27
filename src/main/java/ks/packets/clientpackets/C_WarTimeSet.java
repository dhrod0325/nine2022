package ks.packets.clientpackets;

import ks.core.network.L1Client;

public class C_WarTimeSet extends ClientBasePacket {

    public C_WarTimeSet(byte[] data, L1Client client) throws Exception {
        super(data);
    }

}
