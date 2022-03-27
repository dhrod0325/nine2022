package ks.model.item.function.item;

import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_ServerMessage;

public class Sting extends L1ItemInstance {
    public Sting(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
            pc.getInventory().setStingId(useItem.getItem().getItemId());
            pc.sendPackets(new S_ServerMessage(452, useItem.getLogName()));
        }
    }
}
