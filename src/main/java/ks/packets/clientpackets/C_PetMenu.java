package ks.packets.clientpackets;

import ks.core.network.L1Client;
import ks.model.L1World;
import ks.model.instance.L1PetInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_PetInventory;

public class C_PetMenu extends ClientBasePacket {
    public C_PetMenu(byte[] data, L1Client client) {
        super(data);

        int petId = readD();

        Object o = L1World.getInstance().findObject(petId);

        if (o instanceof L1PetInstance) {
            L1PetInstance pet = (L1PetInstance) L1World.getInstance().findObject(petId);
            L1PcInstance pc = client.getActiveChar();

            if (pet != null && pc != null) {
                pc.sendPackets(new S_PetInventory(pet));
            }
        }
    }
}
