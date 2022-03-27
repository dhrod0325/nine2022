package ks.model.item.function;

import ks.core.datatables.polyCard.action.L1PolyActionUtils;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import ks.model.map.L1Map;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_ServerMessage;
import ks.packets.serverpackets.S_ShowPolyList;

public class PolyScroll extends L1ItemInstance {
    public PolyScroll(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());

            if (pc.getMapId() == L1Map.MAP_FISHING || pc.getMapId() == 5001 || pc.getMapId() == 5153) {
                pc.sendPackets(new S_ServerMessage(1170));
                return;
            }

            int itemId = getItemId();

            if (L1Opcodes.SERVER_VERSION == 3.1) {
                pc.getEtcMap().put("polyItem", itemId);
                pc.sendPackets(new S_ShowPolyList(pc.getId(), pc));
            } else if (L1Opcodes.SERVER_VERSION == 3.8) {
                if (L1PolyActionUtils.usePolyScroll(pc, itemId, packet.readS())) {
                    pc.getInventory().removeItem(useItem, 1);
                }
            }
        }
    }
}
