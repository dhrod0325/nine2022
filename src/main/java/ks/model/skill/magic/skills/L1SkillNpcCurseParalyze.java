package ks.model.skill.magic.skills;

import ks.model.L1Character;
import ks.model.L1CurseParalysis;
import ks.model.instance.L1MonsterInstance;
import ks.model.pc.L1PcInstance;
import ks.model.skill.magic.L1SkillRequest;
import ks.model.skill.utils.L1SkillUtils;

import static ks.constants.L1SkillId.EARTH_BIND;

public class L1SkillNpcCurseParalyze extends L1SkillAdapter {
    public L1SkillNpcCurseParalyze(int skillId) {
        super(skillId);
    }

    @Override
    public void runSkill(L1SkillRequest request) {
        super.runSkill(request);

        L1Character targetCharacter = request.getTargetCharacter();

        if (!targetCharacter.getSkillEffectTimerSet().hasSkillEffect(EARTH_BIND)
                && !targetCharacter.getSkillEffectTimerSet().hasSkillEffect(L1SkillUtils.ICE_SKILLS)
        ) {
            if (targetCharacter instanceof L1PcInstance) {
                L1CurseParalysis.curse(targetCharacter, 1000 * 8, 16 * 1000);
            } else if (targetCharacter instanceof L1MonsterInstance) {
                if (targetCharacter.getMaxHp() < 4300) {
                    L1CurseParalysis.curse(targetCharacter, 0, 16 * 1000);
                }
            }
        }
    }

    @Override
    public void stopSkill(L1Character targetCharacter) {
        targetCharacter.cureParalaysis();
    }
}
