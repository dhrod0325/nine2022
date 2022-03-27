package ks.model.instance;

import ks.core.datatables.HouseTable;
import ks.core.datatables.NPCTalkDataTable;
import ks.model.*;
import ks.model.attack.physics.L1AttackRun;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCTalkReturn;

public class L1HousekeeperInstance extends L1NpcInstance {
    public L1HousekeeperInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onAction(L1PcInstance pc) {
        L1AttackRun attack = new L1AttackRun(pc, this);
        attack.action();
    }

    @Override
    public void onTalkAction(L1PcInstance pc) {
        int objid = getId();
        L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(
                getTemplate().getNpcId());
        int npcid = getTemplate().getNpcId();
        String htmlid = null;
        String[] htmldata = null;
        boolean isOwner = false;

        if (talking != null) {
            L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
            if (clan != null) {
                int houseId = clan.getHouseId();

                if (houseId != 0) {
                    L1House house = HouseTable.getInstance().getHouseTable(houseId);
                    if (npcid == house.getKeeperId()) {
                        isOwner = true;
                    }
                }
            }

            if (!isOwner) {
                L1House targetHouse = null;

                for (L1House house : HouseTable.getInstance().getHouseTableList()) {
                    if (npcid == house.getKeeperId()) {
                        targetHouse = house;
                        break;
                    }
                }

                boolean isOccupy = false;
                String clanName = null;
                String leaderName = null;

                for (L1Clan targetClan : L1World.getInstance().getAllClans()) {
                    if (targetHouse == null)
                        continue;

                    if (targetHouse.getHouseId() == targetClan.getHouseId()) {
                        isOccupy = true;
                        clanName = targetClan.getClanName();
                        leaderName = targetClan.getLeaderName();
                        break;
                    }
                }

                if (isOccupy) {
                    htmlid = "agname";
                    htmldata = new String[]{clanName, leaderName, targetHouse.getHouseName()};
                } else {
                    htmlid = "agnoname";
                    if (targetHouse != null) {
                        htmldata = new String[]{targetHouse.getHouseName()};
                    }
                }
            }

            if (htmlid != null) {
                pc.sendPackets(new S_NPCTalkReturn(objid, htmlid, htmldata));
            } else {
                if (pc.getLawful() < -1000) {
                    pc.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
                } else {
                    pc.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
                }
            }
        }
    }

    @Override
    public void onFinalAction(L1PcInstance pc, String action) {
    }
}
