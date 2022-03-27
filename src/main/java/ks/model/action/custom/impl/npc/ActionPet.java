package ks.model.action.custom.impl.npc;

import ks.model.L1Character;
import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_Message_YN;
import ks.packets.serverpackets.S_PetList;
import ks.packets.serverpackets.S_SelectTarget;

public class ActionPet extends L1AbstractNpcAction {
    public ActionPet(String action, L1PcInstance pc, L1Object obj, String param) {
        super(action, pc, obj, param);
    }

    @Override
    public void execute() {
        if (action.equalsIgnoreCase("withdrawnpc")) {
            pc.sendPackets(new S_PetList(pc));
        } else if (action.equalsIgnoreCase("changename")) {
            pc.setTempID(objId);
            pc.sendPackets(new S_Message_YN(325, ""));
        } else if (action.equalsIgnoreCase("attackchr")) {
            if (obj instanceof L1Character) {
                L1Character cha = (L1Character) obj;
                pc.sendPackets(new S_SelectTarget(cha.getId()));
            }
        }
    }
}
