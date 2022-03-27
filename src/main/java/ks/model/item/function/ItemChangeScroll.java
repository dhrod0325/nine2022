package ks.model.item.function;

import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.util.L1CommonUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ItemChangeScroll extends L1ItemInstance {
    public ItemChangeScroll(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
            L1ItemInstance targetItem = pc.getInventory().getItem(packet.readD());

            if (targetItem == null) {
                return;
            }

            List<Integer> 고급 = Arrays.asList(62, 190, 9, 127, 81);
            List<Integer> 영웅 = Arrays.asList(205, 84, 54, 58, 76, 450014);
            List<Integer> 전설 = Arrays.asList(12, 61, 134, 45000601, 86);

            List<Integer> changed = new CopyOnWriteArrayList<>();

            switch (useItem.getItemId()) {
                case 6000112:
                    if (고급.contains(targetItem.getItemId())) {
                        changed.addAll(고급);
                    }
                    break;
                case 6000113:
                    if (영웅.contains(targetItem.getItemId())) {
                        changed.addAll(영웅);
                    }
                    break;
                case 6000114:
                    if (전설.contains(targetItem.getItemId())) {
                        changed.addAll(전설);
                    }
                    break;
            }

            L1CommonUtils.changeItem(pc, targetItem, changed, targetItem.getBless());

            pc.getInventory().removeItem(useItem, 1);
        }
    }
}
