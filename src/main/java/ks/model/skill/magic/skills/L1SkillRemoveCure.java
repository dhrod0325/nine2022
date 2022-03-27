package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.model.skill.utils.L1SkillUtils;
import ks.packets.serverpackets.S_CurseBlind;

import static ks.constants.L1SkillId.CURSE_BLIND;
import static ks.constants.L1SkillId.DARKNESS;

public class L1SkillRemoveCure extends L1SkillAdapter {
    public L1SkillRemoveCure(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character cha = request.getTargetCharacter();
        cha.curePoison();

        if (cha.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.CURSE_SKILLS)) {
            cha.cureParalaysis();
        }

        if (cha.getSkillEffectTimerSet().hasSkillEffect(CURSE_BLIND) || cha.getSkillEffectTimerSet().hasSkillEffect(DARKNESS)) {
            if (cha.getSkillEffectTimerSet().hasSkillEffect(CURSE_BLIND)) {
                cha.getSkillEffectTimerSet().removeSkillEffect(CURSE_BLIND);
            } else if (cha.getSkillEffectTimerSet().hasSkillEffect(DARKNESS)) {
                cha.getSkillEffectTimerSet().removeSkillEffect(DARKNESS);
            }

            if (cha instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) cha;
                pc.sendPackets(new S_CurseBlind(0));
            }
        }
    }
}
