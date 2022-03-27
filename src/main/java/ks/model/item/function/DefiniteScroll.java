package ks.model.item.function;

import ks.core.datatables.ResolventTable;
import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.L1PcInventory;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_IdentifyDesc;
import ks.packets.serverpackets.S_ItemStatus;

public class DefiniteScroll extends L1ItemInstance {
    public DefiniteScroll(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
            L1ItemInstance targetItem = pc.getInventory().getItem(packet.readD());

            pc.sendPackets(new S_IdentifyDesc(targetItem));

            if (!targetItem.isIdentified()) {
                targetItem.setIdentified(true);
                pc.getInventory().updateItem(targetItem, L1PcInventory.COL_IS_ID);
            } else {
                pc.sendPackets(new S_ItemStatus(targetItem));
            }

            pc.getInventory().removeItem(useItem, 1);

            int crystalCount = ResolventTable.getInstance().getCrystalCount(targetItem.getItemId());
            pc.sendPackets("용해수량 : " + crystalCount);
        }
    }
}
