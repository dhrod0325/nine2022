package ks.model.action.custom.impl.npc;

import ks.core.datatables.HouseTable;
import ks.model.*;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.instance.L1HousekeeperInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;

public class ActionHall extends L1AbstractNpcAction {
    public ActionHall(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (obj instanceof L1HousekeeperInstance) {
            L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
            if (clan != null) {
                int houseId = clan.getHouseId();
                if (houseId != 0) {
                    L1House house = HouseTable.getInstance().getHouseTable(houseId);
                    int keeperId = house.getKeeperId();
                    if (npcId == keeperId) {
                        if (house.isPurchaseBasement()) {
                            int[] loc = L1HouseLocation.getBasementLoc(houseId);
                            L1Teleport.teleport(pc, loc[0], loc[1], (short) (loc[2]), 5, true);
                        } else {
                            pc.sendPackets(new S_ServerMessage(1098));
                        }
                    }
                }
            }
        }
    }
}
