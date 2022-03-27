package ks.packets.serverpackets;

import ks.core.network.opcode.L1Opcodes;
import ks.model.instance.L1ItemInstance;
import ks.model.pc.L1PcInstance;

import java.util.ArrayList;
import java.util.List;

public class S_FixWeaponList extends ServerBasePacket {
    public S_FixWeaponList(L1PcInstance pc) {
        buildPacket(pc);
    }

    private void buildPacket(L1PcInstance pc) {
        writeC(L1Opcodes.S_OPCODE_SELECTLIST);
        writeD(0x000000c8); // Price

        List<L1ItemInstance> weaponList = new ArrayList<>();
        List<L1ItemInstance> itemList = pc.getInventory().getItems();
        for (L1ItemInstance item : itemList) {

            // Find Weapon
            if (item.getItem().getType2() == 1) {
                if (item.getDurability() > 0) {
                    weaponList.add(item);
                }
            }
        }

        writeH(weaponList.size()); // Weapon Amount

        for (L1ItemInstance weapon : weaponList) {

            writeD(weapon.getId()); // Item ID
            writeC(weapon.getDurability()); // Fix Level
        }
    }
}