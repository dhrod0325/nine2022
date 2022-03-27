package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.packets.serverpackets.S_SPMR;
import ks.packets.serverpackets.S_SkillIconShield;

public class L1SkillShadowArmor extends L1SkillAdapter {

    public L1SkillShadowArmor(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character cha = request.getTargetCharacter();

        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;

            pc.getResistance().addMr(5);
            pc.sendPackets(new S_SPMR(pc));
            pc.sendPackets(new S_SkillIconShield(S_SkillIconShield.SHADOW_ARMOR, request.getDuration()));
        }
    }

    @Override
    public void stopSkill(L1Character cha) {
        if (cha instanceof L1PcInstance) {
            L1PcInstance pc = (L1PcInstance) cha;
            pc.getResistance().addMr(-5);
            pc.sendPackets(new S_SPMR(pc));
            pc.sendPackets(new S_SkillIconShield(S_SkillIconShield.SHADOW_ARMOR, 0));
        }
    }
}
