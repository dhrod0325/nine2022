package ks.model.action.custom.impl.npc;

import ks.core.datatables.HouseTable;
import ks.model.L1Clan;
import ks.model.L1House;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_NPCTalkReturn;
import ks.packets.serverpackets.S_SellHouse;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_ShopBuyList;

public class ActionSell extends L1AbstractNpcAction {
    public ActionSell(String actionName, L1PcInstance pc, L1Object obj) {
        super(actionName, pc, obj);
    }

    @Override
    public void execute() {
        if (npcId == 50527 || npcId == 50505 || npcId == 50519 || npcId == 50545 || npcId == 50531 || npcId == 50529
                || npcId == 50516 || npcId == 50538 || npcId == 50518 || npcId == 50509 || npcId == 50536 || npcId == 50520
                || npcId == 50543 || npcId == 50526 || npcId == 50512 || npcId == 50510 || npcId == 50504 || npcId == 50525
                || npcId == 50534 || npcId == 50540 || npcId == 50515 || npcId == 50513 || npcId == 50528 || npcId == 50533
                || npcId == 50542 || npcId == 50511 || npcId == 50501 || npcId == 50503 || npcId == 50508 || npcId == 50514
                || npcId == 50532 || npcId == 50544 || npcId == 50524 || npcId == 50535 || npcId == 50521 || npcId == 50517
                || npcId == 50537 || npcId == 50539 || npcId == 50507 || npcId == 50530 || npcId == 50502 || npcId == 50506
                || npcId == 50522 || npcId == 50541 || npcId == 50523 || npcId == 50620 || npcId == 50623 || npcId == 50619
                || npcId == 50621 || npcId == 50622 || npcId == 50624 || npcId == 50617 || npcId == 50614 || npcId == 50618
                || npcId == 50616 || npcId == 50615 || npcId == 50626 || npcId == 50627 || npcId == 50628 || npcId == 50629
                || npcId == 50630 || npcId == 50631) {

            String sellHouseMessage = sellHouse(pc, objId, npcId);

            if (sellHouseMessage != null) {
                pc.sendPackets(new S_NPCTalkReturn(objId, sellHouseMessage));
            }
        } else {
            pc.sendPackets(new S_ShopBuyList(objId, pc));
        }
    }

    public String sellHouse(L1PcInstance pc, int objectId, int npcId) {
        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());
        if (clan == null) {
            return "";
        }
        int houseId = clan.getHouseId();
        if (houseId == 0) {
            return "";
        }
        L1House house = HouseTable.getInstance().getHouseTable(houseId);
        int keeperId = house.getKeeperId();
        if (npcId != keeperId) {
            return "";
        }
        if (!pc.isCrown()) {
            pc.sendPackets(new S_ServerMessage(518));
            return "";
        }
        if (pc.getId() != clan.getLeaderId()) {
            pc.sendPackets(new S_ServerMessage(518));
            return "";
        }
        if (house.isOnSale()) {
            return "agonsale";
        }

        pc.sendPackets(new S_SellHouse(objectId, String.valueOf(houseId)));
        return null;
    }
}
