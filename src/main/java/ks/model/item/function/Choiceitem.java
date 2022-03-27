package ks.model.item.function;

import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.L1CommonUtils;

public class Choiceitem extends L1ItemInstance {
    public Choiceitem(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
            L1ItemInstance item = pc.getInventory().getItem(packet.readD());
            int itemId = this.getItemId();

            if (itemId >= 41048 && 41055 >= itemId) {
                int logbookId = item.getItem().getItemId();

                if (logbookId == (itemId + 8034)) {
                    L1CommonUtils.createNewItem(pc, logbookId + 2, 1);
                    pc.getInventory().removeItem(item, 1);
                    pc.getInventory().removeItem(useItem, 1);
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
            } else if (itemId == 41056 || itemId == 41057) {
                int logbookId = item.getItem().getItemId();
                if (logbookId == (itemId + 8034)) {
                    L1CommonUtils.createNewItem(pc, 41058, 1);
                    pc.getInventory().removeItem(item, 1);
                    pc.getInventory().removeItem(useItem, 1);
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
            } else if (itemId == 490503) {
                int runeId = item.getItem().getItemId();
                if (runeId >= 600000 && runeId <= 600004) {
                    L1CommonUtils.createNewItem(pc, 490502, 1);
                    pc.getInventory().removeItem(item, 1);
                    pc.getInventory().removeItem(useItem, 1);
                } else {
                    pc.sendPackets(new S_ServerMessage(79));
                }
            }
        }
    }
}
