package ks.packets.clientpackets;

import ks.core.network.L1Client;

public class C_ExitGhost extends ClientBasePacket {
    public C_ExitGhost(byte[] data, L1Client client) {
        super(data);

//        L1PcInstance pc = client.getActiveChar();
//        if (pc == null) {
//            return;
//        }
//        if (!pc.isGhost()) {
//            return;
//        }
//        pc.makeReadyEndGhost();
    }
}