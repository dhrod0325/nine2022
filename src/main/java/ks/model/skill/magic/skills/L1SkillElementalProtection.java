package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_ElfIcon;
import ks.packets.serverpackets.S_OwnCharStatus;

public class L1SkillElementalProtection extends L1SkillAdapter {
    public L1SkillElementalProtection(int skillId) {
        super(skillId);
    }

    private void statUp(L1Character cha, int type) {
        int i = 50 * type;

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            int attr = pc.getElfAttr();

            if (attr == 1) {
                pc.getResistance().addEarth(i);
            } else if (attr == 2) {
                pc.getResistance().addFire(i);
            } else if (attr == 4) {
                pc.getResistance().addWater(i);
            } else if (attr == 8) {
                pc.getResistance().addWind(i);
            }

            pc.sendPackets(new S_OwnCharStatus(pc));
        }
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        statUp(request.getTargetCharacter(), 1);

        request.getTargetCharacter().sendPackets(new S_ElfIcon(0, 0, 0, request.getDuration() / 16));
    }

    @Override
    public void stopSkill(L1Character cha) {
        statUp(cha, -1);
        cha.sendPackets(new S_ElfIcon(0, 0, 0, 0));
    }
}
