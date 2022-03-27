package ks.test;

import ks.base.AbstractTest;
import ks.core.datatables.item.ItemTable;
import ks.core.datatables.pc.CharacterTable;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;

public class DragonArmorTest extends AbstractTest {
    public void papooTest() {
        L1PcInstance pc = CharacterTable.getInstance().loadCharacter("메티스");
        pc.setWorld(true);

        int itemId = 420104;

        L1ItemInstance papooArmor = ItemTable.getInstance().createItem(itemId);
        papooArmor.setEnchantLevel(5);
        papooArmor.setEquipped(true);

        pc.getInventory().storeItem(papooArmor);
        pc.getInventory().toSlotPacket(pc, papooArmor);

        if (pc.getInventory().getCurrentItem().isPapooArmor()) {

        }
    }
}
