package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.L1Party;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Party;
import ks.packets.serverpackets.S_ServerMessage;

public class C_Party extends ClientBasePacket {
    public C_Party(byte[] data, L1Client clientthread) {
        super(data);
        L1PcInstance pc = clientthread.getActiveChar();
        if (pc == null) {
            return;
        }

        L1Party party = pc.getParty();

        if (pc.isInParty()) {
            pc.sendPackets(new S_Party("party", pc.getId(), party.getLeader().getName(), party.getMembersNameList()));
        } else {
            pc.sendPackets(new S_ServerMessage(425));
        }
    }
}
