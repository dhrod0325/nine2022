package ks.packets.clientpackets;

import ks.core.datatables.DungeonTable;
import ks.core.network.L1Client;
import ks.model.pc.L1PcInstance;

public class C_EnterPortal extends ClientBasePacket {
    public C_EnterPortal(byte[] data, L1Client client) {
        super(data);
        int locx = readH();
        int locy = readH();

        L1PcInstance pc = client.getActiveChar();
        if (pc == null) {
            return;
        }

        if (pc.isTeleport()) { // 텔레포트 처리중
            return;
        }

        DungeonTable.getInstance().dg(locx, locy, pc.getMap().getId(), pc);
    }
}
