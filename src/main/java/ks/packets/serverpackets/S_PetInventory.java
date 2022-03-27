package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1PetInstance;

import java.util.List;

public class S_PetInventory extends ServerBasePacket {
    public S_PetInventory(L1PetInstance pet) {
        List<L1ItemInstance> itemList = pet.getInventory().getItems();

        writeC(L1Opcodes.S_OPCODE_SHOWRETRIEVELIST);
        writeD(pet.getId());
        writeH(itemList.size());
        writeC(0x0b);

        for (L1ItemInstance itemObject : itemList) {
            if (itemObject != null) {
                writeD(itemObject.getId());
                writeC(0x16);// 3
                writeH(itemObject.getGfxId());
                writeC(itemObject.getBless());
                writeD(itemObject.getCount());

                switch (itemObject.isEquipped() ? 1 : 0) {
                    case 0:
                        writeC(itemObject.isIdentified() ? 1 : 0);
                        break;
                    case 1:
                        writeC(3);
                        break;
                }

                writeS(itemObject.getViewName());
            }
        }

        writeC(pet.getAC().getAc());
    }
}
