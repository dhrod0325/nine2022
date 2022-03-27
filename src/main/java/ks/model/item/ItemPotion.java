package ks.model.item;

import ks.constants.L1SkillId;
import ks.core.datatables.item.ItemTable;
import ks.model.L1Character;
import ks.model.L1Inventory;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.L1SkillUse;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_SystemMessage;

public class ItemPotion extends L1ItemInstance {
    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            int itemId = getItemId();
            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());

            if (itemId == 6000065) {
                new L1SkillUse(pc, L1SkillId.STATUS_COMA_3, pc.getId(), pc.getX(), pc.getY(), 0).run();
                pc.getInventory().removeItem(useItem, 1);
            } else if (itemId == 6000066) {
                if (pc.getInventory().checkItem(itemId, 5)) {
                    L1ItemInstance item = ItemTable.getInstance().createItem(6000065);
                    item.setCount(1);

                    pc.getInventory().removeItem(useItem, 5);
                    if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
                        pc.getInventory().storeItem(item);
                    }

                    pc.sendPackets(new S_SystemMessage("코마의조각(5)개가 코마의 축복코인 으로 변환되었습니다"));
                } else {
                    pc.sendPackets(new S_SystemMessage("코마의조각(5)개가 필요합니다"));
                }
            }
        }
    }
}
