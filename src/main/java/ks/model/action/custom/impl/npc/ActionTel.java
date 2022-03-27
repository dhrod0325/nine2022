package ks.model.action.custom.impl.npc;

import ks.core.datatables.HouseTable;
import ks.model.*;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;

public class ActionTel extends L1AbstractNpcAction {
    public ActionTel(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (action.equalsIgnoreCase("tel0") || action.equalsIgnoreCase("tel1")
                || action.equalsIgnoreCase("tel2") || action.equalsIgnoreCase("tel3")) {
            L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
            if (clan != null) {
                int houseId = clan.getHouseId();

                if (houseId != 0) {
                    L1House house = HouseTable.getInstance().getHouseTable(houseId);
                    int keeperId = house.getKeeperId();
                    if (npcId == keeperId) {
                        int[] loc = new int[3];
                        if (action.equalsIgnoreCase("tel0")) {
                            loc = L1HouseLocation.getHouseTeleportLoc(houseId, 0);
                        } else if (action.equalsIgnoreCase("tel1")) {
                            loc = L1HouseLocation.getHouseTeleportLoc(houseId, 1);
                        } else if (action.equalsIgnoreCase("tel2")) {
                            loc = L1HouseLocation.getHouseTeleportLoc(houseId, 2);
                        } else if (action.equalsIgnoreCase("tel3")) {
                            loc = L1HouseLocation.getHouseTeleportLoc(houseId, 3);
                        }

                        L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
                    }
                }
            }
        }
    }
}
