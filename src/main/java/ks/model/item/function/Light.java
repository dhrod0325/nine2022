package ks.model.item.function;

import ks.model.L1Character;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.clientpackets.ClientBasePacket;
import ks.packets.serverpackets.S_ItemName;

public class Light extends L1ItemInstance {

    public Light(L1Item item) {
        super(item);
    }

    @Override
    public void clickItem(L1Character cha, ClientBasePacket packet) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            L1ItemInstance useItem = pc.getInventory().getItem(this.getId());

            int itemId = useItem.getItemId();
            if (useItem.getRemainingTime() <= 0 && itemId != 40004) {
                return;
            }

            if (useItem.isNowLighting()) {
                useItem.setNowLighting(false);
                pc.getLight().turnOnOffLight();
            } else {
                useItem.setNowLighting(true);
                pc.getLight().turnOnOffLight();
            }
            pc.sendPackets(new S_ItemName(useItem));
        }
    }
}
