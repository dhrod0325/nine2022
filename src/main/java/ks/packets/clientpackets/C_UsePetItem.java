package ks.packets.clientpackets;

import ks.constants.L1PacketBoxType;
import ks.core.network.L1Client;
import ks.model.L1World;
import ks.model.instance.L1ItemInstance;
import ks.model.instance.L1PetInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_PacketBox;
import ks.packets.serverpackets.S_ServerMessage;

public class C_UsePetItem extends ClientBasePacket {
    public C_UsePetItem(byte[] data, L1Client client) {
        super(data);

        int data1 = readC();
        int petId = readD();
        int listNo = readC();

        L1PetInstance pet = (L1PetInstance) L1World.getInstance().findObject(petId);
        L1PcInstance pc = client.getActiveChar();

        if (pet == null || pc == null) {
            return;
        }

        L1ItemInstance item = pet.getInventory().getItems().get(listNo);

        if (item == null) {
            return;
        }

        if (item.getItem().getType2() == 2 && item.getItem().isUseHighPet()) {
            int itemId = item.getItem().getItemId();
            if (itemId >= 427100 && itemId <= 427109) {
                pet.usePetWeapon(item);
                pc.sendPackets(new S_PacketBox(L1PacketBoxType.PET_ITEM, data1, pet.getId(), pet.getAC().getAc()));
            } else if (itemId >= 427000 && itemId <= 427007) {
                pet.usePetArmor(item);
                pc.sendPackets(new S_PacketBox(L1PacketBoxType.PET_ITEM, data1, pet.getId(), pet.getAC().getAc()));
            } else {
                pc.sendPackets(new S_ServerMessage(79));
            }
        } else {
            pc.sendPackets(new S_ServerMessage(79));
        }
    }
}
