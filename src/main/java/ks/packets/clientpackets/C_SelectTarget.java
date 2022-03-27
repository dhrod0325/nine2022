package ks.packets.clientpackets;

import ks.model.L1Character;
import ks.model.L1Object;
import ks.model.L1World;
import ks.model.instance.L1PetInstance;
import ks.model.pc.L1PcInstance;

public class C_SelectTarget extends ClientBasePacket {
    public C_SelectTarget(byte[] data) {
        super(data);

        int objectId = readD();
        int type = readC();
        int targetId = readD();

        L1Object o = L1World.getInstance().findObject(objectId);
        L1Object t = L1World.getInstance().findObject(targetId);

        if (o instanceof L1PetInstance) {
            L1PetInstance pet = (L1PetInstance) o;

            if (t instanceof L1Character) {
                L1Character target = (L1Character) L1World.getInstance().findObject(targetId);

                if (target != null) {
                    if (target instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) target;
                        if (pc.checkNonPvP()) {
                            return;
                        }
                    }

                    pet.setMasterTarget(target);
                }
            }
        }
    }
}
