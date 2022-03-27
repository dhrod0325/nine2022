package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Emblem;

public class C_Clan extends ClientBasePacket {
    public C_Clan(byte[] data, L1Client client) {
        super(data);

        int emblemId = readD();

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        pc.sendPackets(new S_Emblem(emblemId));
    }
}
