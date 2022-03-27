package ks.run;

import ks.app.config.prop.CodeConfig;
import ks.base.AbstractTest;
import ks.core.datatables.ResolventTable;
import ks.core.datatables.item.ItemTable;
import ks.model.L1EtcItem;
import ks.model.L1Item;
import ks.model.item.L1TreasureBox;

import java.util.List;
import java.util.Map;

public class ResolveBugTest extends AbstractTest {
    public void test() {
        Map<Integer, L1EtcItem> itemList = ItemTable.getInstance().etcItems;

        for (L1EtcItem etcItem : itemList.values()) {
            if (etcItem.getType() == 16) {
                L1TreasureBox box = L1TreasureBox.get(etcItem.getItemId());

                if (box != null) {
                    L1Item boxItem = ItemTable.getInstance().findItem(box.getBoxId());

                    List<L1TreasureBox.Item> items = box.getItems();

                    System.out.println("박스 : " + boxItem.getName());

                    for (L1TreasureBox.Item o : items) {
                        L1Item realItem = ItemTable.getInstance().findItem(o.getItemId());
                        int crystalCount = ResolventTable.getInstance().getCrystalCount(realItem.getItemId());

                        System.out.println(String.format("지급아이템 : %s, 용해금액 : %d", realItem.getName(), crystalCount * CodeConfig.RATE_CRISTAL * 5));
                    }

                    System.out.println();
                }
            }
        }
    }
}
