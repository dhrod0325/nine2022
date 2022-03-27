package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Item;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1MonsterInstance;

import java.util.ArrayList;
import java.util.List;

public class S_DropInfo2 extends ServerBasePacket {
    public S_DropInfo2(L1MonsterInstance mon) {

        if (mon != null) {
            List<L1Item> dropList = new ArrayList<>();

            for (L1ItemInstance k : mon.getInventory().getItems()) {
                dropList.add(k.getItem());
            }

            writeC(L1Opcodes.S_OPCODE_SHOWSHOPBUYLIST);
            writeD(0);
            writeH(dropList.size());

            for (int i = 0; i < dropList.size(); i++) {
                L1Item item = dropList.get(i);
                writeD(i);
                writeH(item.getGfxId());
                writeD(0);

                String itemName = item.getName();

                if (item.getBless() == 0) {
                    itemName = "(축) " + itemName;
                } else if (item.getBless() == 2) {
                    itemName = "(저) " + itemName;
                }

                writeS(itemName);
                writeC(0);
            }

            writeC(0x07);
            writeC(0x00);
        }
    }
}
