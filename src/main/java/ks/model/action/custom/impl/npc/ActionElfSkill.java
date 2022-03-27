package ks.model.action.custom.impl.npc;

import ks.constants.L1SkillIcon;
import ks.model.L1Object;
import ks.model.action.custom.L1AbstractNpcAction;
import ks.model.pc.L1PcInstance;
import ks.packets.serverpackets.S_SkillIconGFX;

public class ActionElfSkill extends L1AbstractNpcAction {
    public ActionElfSkill(String action, L1PcInstance pc, L1Object obj) {
        super(action, pc, obj);
    }

    @Override
    public void execute() {
        if (action.equalsIgnoreCase("fire")) {
            if (pc.isElf()) {
                if (pc.getElfAttr() != 0) {
                    return;
                }

                pc.setElfAttr(2);
                pc.save();
                pc.sendPackets(new S_SkillIconGFX(L1SkillIcon.정령계열못배움멘트, 1));
            }
        } else if (action.equalsIgnoreCase("water")) {
            if (pc.isElf()) {
                if (pc.getElfAttr() != 0) {
                    return;
                }

                pc.setElfAttr(4);
                pc.save();
                pc.sendPackets(new S_SkillIconGFX(L1SkillIcon.정령계열못배움멘트, 2));
            }
        } else if (action.equalsIgnoreCase("air")) {
            if (pc.isElf()) {
                if (pc.getElfAttr() != 0) {
                    return;
                }

                pc.setElfAttr(8);
                pc.save();
                pc.sendPackets(new S_SkillIconGFX(L1SkillIcon.정령계열못배움멘트, 3));
            }
        } else if (action.equalsIgnoreCase("earth")) {
            if (pc.isElf()) {
                if (pc.getElfAttr() != 0) {
                    return;
                }

                pc.setElfAttr(1);
                pc.save();
                pc.sendPackets(new S_SkillIconGFX(L1SkillIcon.정령계열못배움멘트, 4));
            }
        }
    }
}
