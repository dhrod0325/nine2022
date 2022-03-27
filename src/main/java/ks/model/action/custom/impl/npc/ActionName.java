package ks.model.action.custom.impl.npc;

import ks.core.datatables.HouseTable;
import ks.model.L1Clan;
import ks.model.L1House;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Message_YN;
import ks.packets.serverpackets.S_ServerMessage;

public class ActionName extends L1AbstractNpcAction {
    public ActionName(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
        if (clan != null) {
            int houseId = clan.getHouseId();
            if (houseId != 0) {
                if (!pc.isCrown() || pc.getId() != clan.getLeaderId()) {
                    pc.sendPackets(new S_ServerMessage(518));
                    return;
                }
                L1House house = HouseTable.getInstance().getHouseTable(houseId);
                int keeperId = house.getKeeperId();
                if (npcId == keeperId) {
                    pc.setTempID(houseId);
                    pc.sendPackets(new S_Message_YN(512, ""));
                }
            }
        }
    }
}
