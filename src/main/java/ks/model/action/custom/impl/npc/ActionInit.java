package ks.model.action.custom.impl.npc;

import ks.constants.L1SkillId;
import ks.core.datatables.SkillsTable;
import ks.model.L1Object;
import ks.model.L1Skills;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_DelSkill;
import ks.packets.serverpackets.S_ServerMessage;

public class ActionInit extends L1AbstractNpcAction {
    public ActionInit(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        try {
            if (pc.isElf()) {
                if (pc.getElfAttr() == 0) {
                    return;
                }

                for (int cnt = 129; cnt <= 176; cnt++) {
                    try {
                        L1Skills skill = SkillsTable.getInstance().getTemplate(cnt);

                        int skill_attr = skill.getAttr();

                        if (skill_attr != 0) {
                            SkillsTable.getInstance().spellLost(pc.getId(), skill.getSkillId());
                        }
                    } catch (Exception e) {
                        logger.error("오류", e);
                    }
                }

                if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.ELEMENTAL_PROTECTION)) {
                    pc.getSkillEffectTimerSet().removeSkillEffect(L1SkillId.ELEMENTAL_PROTECTION);
                }

                pc.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 248, 252, 252, 255, 0, 0, 0, 0, 0, 0));
                pc.setElfAttr(0);
                pc.save();
                pc.sendPackets(new S_ServerMessage(678));
            }
        } catch (Exception e) {
            logger.error("오류", e);
        }
    }
}
