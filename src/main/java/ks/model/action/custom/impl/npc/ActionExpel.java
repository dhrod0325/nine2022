package ks.model.action.custom.impl.npc;

import ks.core.datatables.HouseTable;
import ks.model.*;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;

import java.util.Collection;

public class ActionExpel extends L1AbstractNpcAction {
    public ActionExpel(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        expelOtherClan(pc, npcId);
    }

    public void expelOtherClan(L1PcInstance clanPc, int keeperId) {
        int houseId = 0;
        for (L1House house : HouseTable.getInstance().getHouseTableList()) {
            if (house.getKeeperId() == keeperId) {
                houseId = house.getHouseId();
            }
        }
        if (houseId == 0) {
            return;
        }

        Collection<L1PcInstance> players = L1World.getInstance().getAllPlayers();

        for (L1PcInstance pc : players) {
            if (L1HouseLocation.isInHouseLoc(houseId, pc.getX(), pc.getY(), pc.getMapId()) && clanPc.getClanId() != pc.getClanId() && !pc.isGm()) {
                int[] loc = L1HouseLocation.getHouseTeleportLoc(houseId, 0);
                L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
            }
        }
    }
}
