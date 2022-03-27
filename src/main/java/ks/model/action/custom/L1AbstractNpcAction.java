package ks.model.action.custom;

import ks.model.L1Object;
import ks.model.instance.L1NpcInstance;
import ks.model.pc.L1PcInstance;

public abstract class L1AbstractNpcAction extends L1AbstractAction {
    protected int npcId;
    protected L1NpcInstance npc;

    public L1AbstractNpcAction(String action, L1PcInstance pc, L1Object obj) {
        this(action, pc, obj, null);
    }

    public L1AbstractNpcAction(String action, L1PcInstance pc, L1Object obj, String param) {
        super(action, pc, obj, param);

        if (obj instanceof L1NpcInstance) {
            this.npc = (L1NpcInstance) obj;
            this.npcId = npc.getNpcId();
        } else {
            logger.error("npc가 아닌 캐릭터의 호출");
        }
    }
}
