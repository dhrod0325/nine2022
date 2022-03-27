package ks.model.action.custom.impl.npc;

import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.instance.L1NpcInstance;
import ks.model.instance.L1PetInstance;
import ks.model.pc.L1PcInstance;

import java.util.Collection;

public class ActionDepositNpc extends L1AbstractNpcAction {
    public ActionDepositNpc(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        Collection<L1NpcInstance> petList = pc.getPetList().values();

        for (L1NpcInstance petObject : petList) {
            if (petObject instanceof L1PetInstance) {
                L1PetInstance pet = (L1PetInstance) petObject;
                pet.removeMe();
            }
        }
    }
}
