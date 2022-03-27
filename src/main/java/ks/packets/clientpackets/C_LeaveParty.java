package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.pc.L1PcInstance;

public class C_LeaveParty extends ClientBasePacket {
    public C_LeaveParty(byte[] decrypt, L1Client client) {
        super(decrypt);

        L1PcInstance pc = client.getActiveChar();

        if (pc == null) {
            return;
        }

        if (pc.isInParty()) {
            pc.getParty().leaveMember(pc);
        }
    }
}
