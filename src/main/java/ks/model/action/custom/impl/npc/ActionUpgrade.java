package ks.model.action.custom.impl.npc;

import ks.constants.L1ItemId;
import ks.core.datatables.HouseTable;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Clan;
import ks.model.L1House;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ChatPacket;
import ks.packets.serverpackets.S_ServerMessage;

public class ActionUpgrade extends L1AbstractNpcAction {
    public ActionUpgrade(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        L1Clan clan = L1World.getInstance().getClan(pc.getClanName());

        if (clan != null) {
            int houseId = clan.getHouseId();
            if (houseId != 0) {
                L1House house = HouseTable.getInstance().getHouseTable(houseId);
                int keeperId = house.getKeeperId();

                if (npcId == keeperId) {
                    if (pc.isCrown() && pc.getId() == clan.getLeaderId()) {
                        if (house.isPurchaseBasement()) {
                            pc.sendPackets(new S_ServerMessage(1135));
                        } else {
                            if (pc.getInventory().consumeItem(L1ItemId.ADENA, 5000000)) {
                                house.setPurchaseBasement(true);
                                HouseTable.getInstance().updateHouse(house);
                                pc.sendPackets(new S_ServerMessage(1099));
                            } else {
                                pc.sendPackets(new S_ChatPacket(pc, "아데나가 충분치않습니다.", L1Opcodes.S_OPCODE_MSG, 20));
                            }
                        }
                    } else {
                        pc.sendPackets(new S_ServerMessage(518));
                    }
                }
            }
        }
    }
}
