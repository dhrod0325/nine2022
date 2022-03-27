package ks.model.action.custom.impl.npc;

import ks.constants.L1SkillId;
import ks.model.Broadcaster;
import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SkillHaste;
import ks.packets.serverpackets.S_SkillSound;

public class ActionHaste extends L1AbstractNpcAction {
    public ActionHaste(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (npcId == 70514) {
            if (pc.getLevel() < 13) {
                pc.sendPackets(new S_SkillHaste(pc.getId(), 1, 1800));
                Broadcaster.broadcastPacket(pc, new S_SkillHaste(pc.getId(), 1, 0));
                pc.sendPackets(new S_SkillSound(pc.getId(), 755));
                Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 755));
                pc.getMoveState().setMoveSpeed(1);
                pc.getSkillEffectTimerSet().setSkillEffect(L1SkillId.STATUS_HASTE, 1800 * 1000);
            }
        }
    }
}
