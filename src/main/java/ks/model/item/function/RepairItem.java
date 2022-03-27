package ks.model.item.function;

import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_ServerMessage;

public class RepairItem extends L1ItemInstance {
    public RepairItem(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            L1ItemInstance useItem = pc.getInventory().findItemId(getId());
            L1ItemInstance targetItem = pc.getInventory().getItem(packet.readD());
            repair(pc, useItem, targetItem);
        }
    }

    public void repair(L1PcInstance pc, L1ItemInstance useItem, L1ItemInstance targetItem) {
        if (targetItem.getItem().getType2() != 0 && targetItem.getDurability() > 0) {
            String msg = targetItem.getLogName();
            pc.getInventory().recoveryDamage(targetItem);

            if (targetItem.getDurability() == 0) {
                pc.sendPackets(new S_ServerMessage(464, msg));
            } else {
                pc.sendPackets(new S_ServerMessage(463, msg));
            }
        } else {
            pc.sendPackets(new S_ServerMessage(79));
        }

        pc.getInventory().removeItem(useItem, 1);
    }
}