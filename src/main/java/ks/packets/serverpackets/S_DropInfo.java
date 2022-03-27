package ks.packets.serverpackets;

import ks.core.datatables.drop.DropTable;
import ks.core.datatables.item.ItemTable;
import ks.core.datatables.npc.NpcTable;
import ks.core.network.opcode.L1Opcodes;
import ks.model.L1Drop;
import ks.model.L1Item;
import ks.model.L1Npc;

import java.util.ArrayList;
import java.util.List;

public class S_DropInfo extends ServerBasePacket {
    public S_DropInfo(int monsterId) {
        L1Npc npc = NpcTable.getInstance().getTemplate(monsterId);

        if (npc != null) {
            List<L1Item> dropList = new ArrayList<>();

            for (List<L1Drop> k : DropTable.getInstance().getDropList().values()) {
                for (L1Drop d : k) {
                    if (d.getChance() == 0)
                        continue;

                    if (d.getMobId() == npc.getNpcId()) {
                        L1Item item = ItemTable.getInstance().findItem(d.getItemId());

                        if (item != null) {
                            dropList.add(item);
                        }
                    }
                }
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
