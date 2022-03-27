package ks.model.item.function.item;

import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_ItemName;
import ks.packets.serverpackets.S_ServerMessage;

public class Arrow extends L1ItemInstance {
    public Arrow(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            L1ItemInstance useItem = pc.getInventory().getItem(getId());
            useItem.setPc(pc);

            int arrowId = useItem.getItem().getItemId();
            int pcArrowId = pc.getInventory().getArrowId();

            if (arrowId == pcArrowId) {
                pc.sendPackets(useItem.getLogName() + "이 해제되었습니다");
                pc.getInventory().setArrowId(0);
            } else {
                pc.getInventory().setArrowId(useItem.getItem().getItemId());
                pc.sendPackets(new S_ServerMessage(452, useItem.getLogName()));
            }

            for (L1ItemInstance item : pc.getInventory().getArrowList(0)) {
                pc.sendPackets(new S_ItemName(item));
            }
        }
    }
}