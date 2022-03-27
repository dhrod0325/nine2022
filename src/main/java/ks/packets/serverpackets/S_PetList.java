package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1PetInstance;
import ks.model.pc.L1PcInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class S_PetList extends ServerBasePacket {
    public S_PetList(L1PcInstance pc) {
        buildPacket(pc);
    }

    private void buildPacket(L1PcInstance pc) {
        List<L1ItemInstance> amuletList = new ArrayList<>();

        for (L1ItemInstance item : pc.getInventory().getItems()) {
            if (item.getItem().getItemId() == 40314 || item.getItem().getItemId() == 40316) {
                if (!isWithdraw(pc, item)) {
                    amuletList.add(item);
                }
            }
        }

        if (amuletList.size() != 0) {
            writeC(L1Opcodes.S_OPCODE_SELECTLIST);
            writeD(0x00000046); // Price
            writeH(amuletList.size());

            for (L1ItemInstance _item : amuletList) {
                writeD(_item.getId());
                writeC(_item.getCount());
            }
        } else {
            pc.sendPackets("찾으실 펫이 존재하지 않습니다");
        }
    }

    private boolean isWithdraw(L1PcInstance pc, L1ItemInstance item) {
        Collection<L1NpcInstance> petlist = pc.getPetList().values();

        for (L1NpcInstance petObject : petlist) {
            if (petObject instanceof L1PetInstance) {
                L1PetInstance pet = (L1PetInstance) petObject;
                if (item.getId() == pet.getItemObjId()) {
                    return true;
                }
            }
        }
        return false;
    }
}
