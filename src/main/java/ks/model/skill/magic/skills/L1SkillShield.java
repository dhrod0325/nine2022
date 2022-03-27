package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_SkillIconShield;

public class L1SkillShield extends L1SkillAdapter {

    public L1SkillShield(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character cha = request.getTargetCharacter();

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.getAC().addAc(-2);
            pc.sendPackets(new S_SkillIconShield(S_SkillIconShield.SHIELD, request.getDuration()));
        }
    }

    @Override
    public void stopSkill(L1Character cha) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.getAC().addAc(2);
            pc.sendPackets(new S_SkillIconShield(S_SkillIconShield.SHIELD, 0));
        }
    }

    @Override
    public void sendIcon(L1Character cha, int duration) {
        cha.sendPackets(new S_SkillIconShield(S_SkillIconShield.SHIELD, duration));
    }
}
