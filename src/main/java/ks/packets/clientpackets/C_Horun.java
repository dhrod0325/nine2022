package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Horun;

public class C_Horun extends ClientBasePacket {
    public C_Horun(byte[] data, L1Client clientthread) throws Exception {
        super(data);

        int i = readD();

        L1PcInstance pc = clientthread.getActiveChar();

        if (pc == null) {
            return;
        }

        pc.sendPackets(new S_Horun(i, pc));
    }
}
