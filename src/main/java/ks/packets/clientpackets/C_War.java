package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.pc.L1PcInstance;
import ks.util.L1WarUtils;

public class C_War extends ClientBasePacket {
    public C_War(byte[] data, L1Client client) {
        super(data);

        int type = readC();
        String targetClanName = readS();

        L1PcInstance pc = client.getActiveChar();

        L1WarUtils.war(pc, targetClanName, type);
    }
}
