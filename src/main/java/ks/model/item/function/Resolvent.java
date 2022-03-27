package ks.model.item.function;

import ks.app.config.prop.CodeConfig;
import ks.core.datatables.ResolventTable;
import ks.core.datatables.item.ItemTable;
import ks.model.*;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_ServerMessage;
import ks.util.common.random.RandomUtils;

public class Resolvent extends L1ItemInstance {
    public Resolvent(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
            L1ItemInstance targetItem = pc.getInventory().getItem(packet.readD());
            useResolvent(pc, targetItem, useItem);
        }
    }

    private void useResolvent(L1PcInstance pc, L1ItemInstance item, L1ItemInstance resolvent) {

        if (item == null || resolvent == null) {
            pc.sendPackets(new S_ServerMessage(79));
            return;
        }

        if (item.getItem().getType2() == 1 || item.getItem().getType2() == 2) {
            if (item.getEnchantLevel() != 0) { // 강화가 끝난 상태
                pc.sendPackets(new S_ServerMessage(1161)); // 용해할 수 없습니다.
                return;
            }

            if (item.isEquipped()) { // 장비중
                pc.sendPackets(new S_ServerMessage(1161)); // 용해할 수 없습니다.
                return;
            }

            if (item.getBless() >= 128) { // 봉인중
                pc.sendPackets(new S_ServerMessage(1161)); // 용해할 수 없습니다.
                return;
            }
        }

        int crystalCount = ResolventTable.getInstance().getCrystalCount(item.getItem().getItemId());

        if (crystalCount == 0) {
            pc.sendPackets(new S_ServerMessage(1161)); // 용해할 수 없습니다.
            return;
        }

        if (RandomUtils.isWinning(100, CodeConfig.RESOLVENT_FAIL_PER)) {
            crystalCount = 0;
            pc.sendPackets(new S_ServerMessage(158, item.getName()));
        }

        if (crystalCount != 0) {
            L1ItemInstance crystal = ItemTable.getInstance().createItem(41246);
            crystal.setCount(crystalCount);

            if (pc.getInventory().checkAddItem(crystal, 1) == L1Inventory.OK) {
                pc.getInventory().storeItem(crystal);
                pc.sendPackets(new S_ServerMessage(403, crystal.getLogName()));
            } else {
                L1World.getInstance().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(crystal);
            }
        }

        pc.getInventory().removeItem(item, 1);
        pc.getInventory().removeItem(resolvent, 1);

        L1ItemDelay.onItemUse(pc, item);
    }
}
