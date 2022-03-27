package ks.packets.clientpackets;

import ks.core.datatables.BuddyTable;
import ks.core.network.L1Client;
import ks.model.pc.L1PcInstance;

public class C_DelBuddy extends ClientBasePacket {
    public C_DelBuddy(byte[] data, L1Client clientthread) {
        super(data);
        L1PcInstance pc = clientthread.getActiveChar();

        if (pc == null)
            return;

        String charName = readS();
        BuddyTable.getInstance().removeBuddy(pc.getId(), charName);
    }
}
