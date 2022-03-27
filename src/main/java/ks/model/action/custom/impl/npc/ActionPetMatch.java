package ks.model.action.custom.impl.npc;

import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_ServerMessage;
import ks.system.petMatch.PetMatch;

import java.util.Collection;

public class ActionPetMatch extends L1AbstractNpcAction {
    public ActionPetMatch(String action, L1PcInstance pc, L1Object obj, String param) {
        super(action, pc, obj, param);
    }

    @Override
    public void execute() {
        if ("ent".equalsIgnoreCase(action)) {
            Collection<L1NpcInstance> petList = pc.getPetList().values();

            if (!petList.isEmpty()) {
                pc.sendPackets(new S_ServerMessage(1187)); // 펫의 아뮤렛트가 사용중입니다.
                return;
            }

            logger.debug("param:{}", param);

            if (!PetMatch.getInstance().enterPetMatch(pc, Integer.parseInt(param))) {
                pc.sendPackets(new S_ServerMessage(1182));
            }
        }
    }
}
