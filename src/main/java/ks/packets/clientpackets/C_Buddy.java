package ks.packets.clientpackets;

import ks.core.datatables.BuddyTable;
import ks.core.network.L1Client;
import ks.model.L1Buddy;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Buddy;

public class C_Buddy extends ClientBasePacket {
    public C_Buddy(byte[] data, L1Client client) {
        super(data);
        L1PcInstance pc = client.getActiveChar();

        if (pc == null)
            return;

        L1Buddy buddy = BuddyTable.getInstance().getBuddyTable(pc.getId());
        pc.sendPackets(new S_Buddy(pc.getId(), buddy));
    }
}
